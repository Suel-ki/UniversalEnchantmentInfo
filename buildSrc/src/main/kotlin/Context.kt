import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment
import org.gradle.api.JavaVersion
import org.gradle.api.Project

class Context(
	val project: Project,
	val extension: ModPlatformExtension,
	val loader: Loader,
	val stonecutter: StonecutterBuildExtension
) {
	private fun require(key: String): String =
		runCatching { project.sc.properties.getAs<String>(key) }.getOrNull()?.takeIf { it.isNotBlank() }
			?: error("Missing required property '$key' in stonecutter.properties.toml")

	private fun optional(key: String, fallback: String = ""): String =
		runCatching { project.sc.properties.getAs<String>(key) }.getOrNull()?.takeIf { it.isNotBlank() } ?: fallback

	val currentMcVersion: String by lazy {
		stonecutter.current.version
	}

	val modId: String by lazy { require("mod.id") }
	val modName: String by lazy { require("mod.name") }
	val modGroup: String by lazy { require("mod.group") }
	val modVersion: String by lazy { require("mod.version") }
	val channelTag: String by lazy { optional("mod.channel_tag") }
	val description: String by lazy { optional("mod.description") }
	val licenseName: String by lazy { require("mod.license.name") }
	val licenseUrl: String by lazy { require("mod.license.url") }
	val licenseDist: String by lazy { optional("mod.license.dist", "repo") }
	val inceptionYear: String by lazy { optional("mod.inception_year") }

	val environment: ModrinthEnvironment by lazy {
		val env = require("mod.environment")
		runCatching { ModrinthEnvironment.valueOf(env.uppercase()) }.getOrElse {
			val entries = ModrinthEnvironment.entries.joinToString { it.name.lowercase() }
			error("""
				Invalid mod.environment '$env' in stonecutter.properties.toml.
				Valid values: $entries
				See https://github.com/modmuss50/mod-publish-plugin/blob/main/src/main/kotlin/me/modmuss50/mpp/platforms/modrinth/ModrinthEnvironment.kt for documentation.
			""".trimIndent()
			)
		}
	}
	val environmentPhysicalClient: Boolean by lazy {
		when (environment) {
			ModrinthEnvironment.DEDICATED_SERVER_ONLY -> false

			ModrinthEnvironment.CLIENT_ONLY,
			ModrinthEnvironment.SERVER_ONLY,
			ModrinthEnvironment.CLIENT_AND_SERVER,
			ModrinthEnvironment.SERVER_ONLY_CLIENT_OPTIONAL,
			ModrinthEnvironment.CLIENT_ONLY_SERVER_OPTIONAL,
			ModrinthEnvironment.CLIENT_OR_SERVER_PREFERS_BOTH,
			ModrinthEnvironment.CLIENT_OR_SERVER,
			ModrinthEnvironment.SINGLEPLAYER_ONLY -> true
		}
	}
	val environmentPhysicalServer: Boolean by lazy {
		when (environment) {
			ModrinthEnvironment.CLIENT_ONLY,
			ModrinthEnvironment.SINGLEPLAYER_ONLY -> false

			ModrinthEnvironment.SERVER_ONLY,
			ModrinthEnvironment.DEDICATED_SERVER_ONLY,
			ModrinthEnvironment.CLIENT_AND_SERVER,
			ModrinthEnvironment.SERVER_ONLY_CLIENT_OPTIONAL,
			ModrinthEnvironment.CLIENT_ONLY_SERVER_OPTIONAL,
			ModrinthEnvironment.CLIENT_OR_SERVER_PREFERS_BOTH,
			ModrinthEnvironment.CLIENT_OR_SERVER -> true
		}
	}

	val authors: List<String> by lazy {
		runCatching {
			project.sc.properties.raw("mod.authors").asList().map { it.toString() }
		}.getOrElse { error("Missing or malformed 'mod.authors' in stonecutter.properties.toml") }
	}

	val contributors: List<String> by lazy {
		runCatching {
			project.sc.properties.raw("mod.contributors").asList().map { it.toString() }
		}.getOrElse { emptyList() }
	}

	val sourcesUrl: String by lazy { require("mod.sources_url") }
	val homepageUrl: String by lazy { require("mod.homepage_url") }
	val discordUrl: String by lazy { optional("mod.discord_url") }
	val issuesUrl: String by lazy { optional("mod.issues_url", "$sourcesUrl/issues") }

	val isSnapshot: Boolean by lazy {
		project.envTrue("MOD_IS_SNAPSHOT") || project.env("MOD_IS_RELEASE")?.equals("false", true) == true
	}
	val baseVersion: String by lazy { "$modVersion$channelTag" }
	val snapshotSuffix: String by lazy { if (isSnapshot) "-SNAPSHOT" else "" }
	val fullVersion: String by lazy { "$baseVersion-${loader.id}+$currentMcVersion$snapshotSuffix" }
	val basicVersion: String by lazy { "$baseVersion$snapshotSuffix" }

	val publishAdditionalVersions: List<String> by lazy {
		project.sc.properties.rawOrNull("publish.additionalVersions")?.to<List<String>>().orEmpty()
	}

	val viewerKeys: List<String> by lazy {
		listOf("jei", "rei", "emi", "rrv").filter { project.hasProp("deps.$it") }
	}

	val mixinConfigs: List<String> by lazy {
		buildList {
			if (loader.id != "forge") add("$modId.mixins.json")
			if ("emi" in viewerKeys) add("$modId.emi.mixins.json")
			if ("rei" in viewerKeys && stonecutter.eval(currentMcVersion, "<=1.20.1")) add("$modId.rei.mixins.json")
			if ("rrv" in viewerKeys) add("$modId.rrv.mixins.json")
		}
	}

	val fabricEntrypoints: Map<String, List<String>> by lazy {
		val base = "$modGroup.$modId"
		buildMap {
			put("main", listOf("$base.fabric.UeiFabric"))
			put("modmenu", listOf("$base.fabric.modmenu.ModMenuIntegration"))
			if ("jei" in viewerKeys) put("jei_mod_plugin", listOf("$base.compat.jei.JEIPlugin"))
			if ("rei" in viewerKeys) put("rei_client", listOf("$base.compat.rei.REIPlugin"))
			if ("emi" in viewerKeys) put("emi", listOf("$base.compat.emi.EMIPlugin"))
			if ("rrv" in viewerKeys) put("rrv_client", listOf("$base.compat.rrv.RRVPlugin"))
		}
	}

	val iconPath: String by lazy { "assets/$modId/icon.png" }

	val javaVersion: JavaVersion by lazy {
		when {
			stonecutter.eval(currentMcVersion, ">=26") -> JavaVersion.VERSION_25
			stonecutter.eval(currentMcVersion, ">=1.20.5") -> JavaVersion.VERSION_21
			stonecutter.eval(currentMcVersion, ">=1.18") -> JavaVersion.VERSION_17
			stonecutter.eval(currentMcVersion, ">=1.17") -> JavaVersion.VERSION_16
			else -> JavaVersion.VERSION_1_8
		}
	}
}
