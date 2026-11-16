import dev.kikugie.fletching_table.extension.FletchingTableExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.expand
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.plugins.ide.idea.model.IdeaModel
import javax.inject.Inject

abstract class ModPlatformPlugin @Inject constructor() : Plugin<Project> {
	override fun apply(project: Project) = with(project) {
		val inferredLoader = Loader.of(project.buildFile.name.substringAfter('.').replace(".gradle.kts", ""))

		val extension = extensions.create("platform", ModPlatformExtension::class.java).apply {
			loader.convention(inferredLoader.id)
		}

		when (inferredLoader) {
			is Loader.Fabric -> {
				extension.jarTask.convention(providers.provider {
					extensions.getByType<dev.kikugie.loomx.LoomCompatProjectExtension>().modJar.name
				})
				extension.sourcesJarTask.convention(providers.provider {
					extensions.getByType<dev.kikugie.loomx.LoomCompatProjectExtension>().modSourcesJar.name
				})
			}

			is Loader.Forge -> {
				extension.jarTask.convention("reobfJar")
				extension.sourcesJarTask.convention("sourcesJar")
			}

			else -> {
				extension.jarTask.convention("jar")
				extension.sourcesJarTask.convention("sourcesJar")
			}
		}

		listOf(
			"org.jetbrains.kotlin.jvm",
			"dev.kikugie.fletching-table",
			"me.modmuss50.mod-publish-plugin"
		).forEach {
			apply(
				plugin = it
			)
		}

		val (version, loader) = name.split('-', limit = 2)
		project.sc.properties.tags(version, loader)

		val ctx = Context(
			project = this,
			extension = extension,
			loader = Loader.of(extension.loader.get()),
			stonecutter = project.sc
		)
		configureProject(ctx)
	}

	private fun Project.configureProject(ctx: Context) {
		version = ctx.fullVersion

		listOf("java", "idea").forEach { apply(plugin = it) }
		ctx.extension.requiredJava.set(ctx.javaVersion)

		if (ctx.loader.isFabricLike) {
			ctx.extension.dependencies {
				required("java") { fabricLikeVersionRange = ">=${ctx.javaVersion.majorVersion}" }
			}
		}

		configureFletchingTable(ctx)
		registerGenerateManifestTask(ctx)
		configureJarTask(ctx)
		configureIdea()
		configureProcessResources(ctx)
		configureJava(ctx)
		configureStonecutterSources()
		registerBuildAndCollectTask(ctx)

		configureModPublishing(ctx)

		if (envTrue("PUB_MAVEN_ENABLE")) {
			apply(plugin = "com.vanniktech.maven.publish")
			configureMavenPublishing(ctx)
		}
	}

	private fun Project.configureStonecutterSources() {
		val activeNode = rootProject.file(".sc_active_version").takeIf { it.exists() }?.readText()?.trim()
		if (name == activeNode) return
		the<JavaPluginExtension>().sourceSets.named("main") {
			java.setSrcDirs(listOf(layout.buildDirectory.dir("generated/stonecutter/main/java")))
		}
		tasks.named("compileJava") { dependsOn(tasks.named("stonecutterGenerate")) }
		tasks.matching { it.name == "kspKotlin" || it.name == "compileKotlin" }.configureEach { dependsOn(tasks.named("stonecutterGenerate")) }
		tasks.matching { it.name == "sourcesJar" || it.name == "remapSourcesJar" }.configureEach {
			dependsOn(tasks.named("stonecutterGenerate"))
		}
	}

	private fun Project.configureJava(ctx: Context) {
		extensions.configure<JavaPluginExtension>("java") {
			withSourcesJar()
			withJavadocJar()
			sourceCompatibility = ctx.javaVersion
			targetCompatibility = ctx.javaVersion
			toolchain {
				languageVersion.set(JavaLanguageVersion.of(ctx.javaVersion.majorVersion))
			}
		}
	}

	private fun Project.registerGenerateManifestTask(ctx: Context) {
		val manifestOutputDir = layout.buildDirectory.dir("generated/modManifest")
		abstract class GenerateModManifestTask : DefaultTask() {
			@get:Input
			abstract val content: Property<String>

			@get:OutputFile
			abstract val outputFile: RegularFileProperty

			@TaskAction
			fun generate() {
				val file = outputFile.get().asFile
				file.parentFile.mkdirs()
				file.writeText(content.get())
			}
		}
		val generateTask = tasks.register<GenerateModManifestTask>("generateModManifest") {
			content.set(ctx.loader.generateManifest(ctx))
			outputFile.set(layout.buildDirectory.file("generated/modManifest/${ctx.loader.modManifestPath}"))
		}

		the<JavaPluginExtension>().sourceSets.named("main") { resources.srcDir(manifestOutputDir) }
		tasks.named<ProcessResources>("processResources") { dependsOn(generateTask) }
	}

	private fun Project.configureProcessResources(ctx: Context) {
		val javaVersionValue = "JAVA_${ctx.javaVersion.majorVersion}"
		val excluded = ctx.loader.excludedResources
			val disabledMixins = listOf("${ctx.modId}.emi.mixins.json", "${ctx.modId}.rrv.mixins.json").filter { it !in ctx.mixinConfigs }
		tasks.named<ProcessResources>("processResources") {
			dependsOn(tasks.named("stonecutterGenerate"))
			filesMatching("*.mixins.json") {
				expand("java" to javaVersionValue)
			}
			exclude(excluded)
			exclude(disabledMixins)
		}
	}

	private fun Project.configureJarTask(ctx: Context) {
		val generateTask = tasks.named("generateModManifest")
		tasks.withType<Jar>().configureEach {
			archiveBaseName.set(ctx.modId)
			dependsOn(generateTask)
			if (ctx.loader is Loader.Forge && ctx.mixinConfigs.isNotEmpty()) {
				manifest.attributes(ctx.loader.mixinConfigAttribute to ctx.mixinConfigs.joinToString(","))
			}
		}
	}

	private fun Project.configureIdea() {
		extensions.configure<IdeaModel>("idea") {
			module {
				isDownloadJavadoc = true
				isDownloadSources = true
			}
		}
	}

	private fun Project.configureFletchingTable(ctx: Context) {
		extensions.configure<FletchingTableExtension> {
			j52j.register("main") { extension("json", "**/*.json5") }
		}
	}

	private fun Project.registerBuildAndCollectTask(ctx: Context) {
		tasks.register<Copy>("buildAndCollect") {
			from(
				tasks.named(ctx.extension.jarTask.get()),
				tasks.named(ctx.extension.sourcesJarTask.get()),
				tasks.named("javadocJar")
			)
			into(rootProject.layout.buildDirectory.file("libs/${ctx.basicVersion}"))
			dependsOn("build")
			group = "build"
		}
	}
}
