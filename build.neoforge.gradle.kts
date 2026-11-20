import java.io.File

plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
}

val requestedViewer: String = (findProperty("recipe_viewer") ?: "all").toString().lowercase()
fun viewerSelected(viewer: String): Boolean = requestedViewer == "all" || requestedViewer == viewer

stonecutter {
	constants["jei"] = hasProp("deps.jei")
	constants["rei"] = hasProp("deps.rei")
	constants["emi"] = hasProp("deps.emi")
	constants["rrv"] = hasProp("deps.rrv")
	constants["apothic"] = hasProp("deps.apothic") || hasProp("deps.apotheosis")
	replacements.regex(current.parsed >= "1.21.11") {
		replace("\\bResourceLocation\\b", "Identifier", "\\bIdentifier\\b", "ResourceLocation")
		replace("\\blocation\\(\\)", "identifier()", "\\bidentifier\\(\\)", "location()")
	}
	replacements.regex(current.parsed >= "26") {
		replace("(?<!compat\\.)\\bGuiGraphics\\b", "GuiGraphicsExtractor", "(?<!compat\\.)\\bGuiGraphicsExtractor\\b", "GuiGraphics")
		replace("\\bdrawString\\(", "text(", "\\btext\\(", "drawString(")
		replace("\\bdrawCenteredString\\(", "centeredText(", "\\bcenteredText\\(", "drawCenteredString(")
		replace("\\brenderContent\\(", "extractContent(", "\\bextractContent\\(", "renderContent(")
		replace("\\.setScreen\\(", ".setScreenAndShow(", "\\.setScreenAndShow\\(", ".setScreen(")
	}
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") { forgeLikeVersionRange = prop("deps.minecraft") }
		required("neoforge") { forgeLikeVersionRange.set("[${prop("deps.neoforge")},)") }
		if (hasProp("deps.jei")) optional("jei") { slug("jei") }
		if (hasProp("deps.rei")) optional("rei") { slug(modrinthSlug = "rei", curseforgeSlug = "roughly-enough-items") }
		if (hasProp("deps.emi")) optional("emi") { slug("emi") }
		if (hasProp("deps.rrv")) optional("rrv") { slug("rrv") }
	}
}

neoForge {
	version = prop("deps.neoforge")
	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${sc.current.version})"
			programArgument("--username=Dev")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "NeoForge Server (${sc.current.version})"
		}
	}
	mods {
		register(prop("mod.id")) { sourceSet(sourceSets["main"]) }
	}
}

repositories {
	mavenCentral()
	maven("https://maven.blamejared.com/") { name = "BlameJaredPlain" }
	maven("https://maven.shedaniel.me/") { name = "ShedanielPlain" }
	maven("https://repo.sleeping.town/") { name = "EMIPlain" }
	maven("https://maven.architectury.dev/") { name = "ArchitecturyPlain" }
	maven("https://maven.architectury.dev/") { name = "ArchitecturyPlain2" }
	strictMaven("https://maven.blamejared.com/", "mezz.jei") { name = "BlameJared" }
	strictMaven("https://maven.shedaniel.me/", "me.shedaniel", "me.shedaniel.cloth") { name = "Shedaniel" }
	strictMaven("https://repo.sleeping.town/", "dev.emi") { name = "EMI" }
	strictMaven("https://maven.cassian.cc", "cc.cassian", "cc.cassian.rrv") { name = "Cassian" }
	strictMaven("https://maven.architectury.dev/", "dev.architectury") { name = "Architectury" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	strictMaven("https://www.cursemaven.com", "curse.maven") { name = "CurseMaven" }
}

dependencies {
	if (hasProp("deps.jei")) {
		compileOnly("mezz.jei:jei-${prop("deps.minecraft")}-neoforge:${prop("deps.jei")}")
		if (viewerSelected("jei")) runtimeOnly("mezz.jei:jei-${prop("deps.minecraft")}-neoforge:${prop("deps.jei")}")
	}
	if (hasProp("deps.rei")) {
		compileOnly("me.shedaniel:RoughlyEnoughItems-api-neoforge:${prop("deps.rei")}")
		if (viewerSelected("rei")) {
			runtimeOnly("me.shedaniel:RoughlyEnoughItems-neoforge:${prop("deps.rei")}")
			runtimeOnly("me.shedaniel.cloth:cloth-config-neoforge:${prop("deps.cloth-config")}")
			runtimeOnly("dev.architectury:architectury-neoforge:${prop("deps.architectury")}")
		}
	}
	if (hasProp("deps.emi")) {
		if (sc.current.parsed < "1.21.2") {
			compileOnly("dev.emi:emi-xplat-mojmap:${prop("deps.emi")}:api")
			compileOnly("dev.emi:emi-xplat-mojmap:${prop("deps.emi")}")
			if (viewerSelected("emi")) runtimeOnly("dev.emi:emi-neoforge:${prop("deps.emi")}")
		} else {
			compileOnly("maven.modrinth:pf-emi:${prop("deps.emi")}")
			if (viewerSelected("emi")) runtimeOnly("maven.modrinth:pf-emi:${prop("deps.emi")}")
		}
	}
	if (hasProp("deps.rrv")) {
		compileOnly("cc.cassian.rrv:reliable-recipe-viewer-neoforge:${prop("deps.rrv")}+${prop("deps.minecraft")}")
		if (viewerSelected("rrv")) runtimeOnly("cc.cassian.rrv:reliable-recipe-viewer-neoforge:${prop("deps.rrv")}+${prop("deps.minecraft")}")
	}
	if (hasProp("deps.apothic")) {
		compileOnly("maven.modrinth:apothic-enchanting:${prop("deps.apothic")}")
	}
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}

tasks.matching { it.name == "runClient" }.configureEach {
	doFirst {
		require(requestedViewer in setOf("none", "all", "jei", "rei", "emi", "rrv")) {
			"Unknown recipe_viewer '$requestedViewer'. Use all, jei, rei, emi or rrv."
		}
		require(requestedViewer in setOf("none", "all") || hasProp("deps.$requestedViewer")) {
			"recipe_viewer '$requestedViewer' is not enabled on ${project.name}. " +
				"Enabled here: " + listOf("jei", "rei", "emi", "rrv").filter { hasProp("deps.$it") }.ifEmpty { listOf("(none)") }
		}
	}
}


listOf("jei", "rei", "emi", "rrv").filter { hasProp("deps.$it") }.forEach { viewer ->
	val taskName = "runClient" + viewer.replaceFirstChar { it.uppercase() }
	val nodeTarget = project.path + ":runClient"
	tasks.register<GradleBuild>(taskName) {
		group = "run"
		description = "Runs this node's client with $viewer"
		dir = rootDir
		setTasks(listOf(nodeTarget))
		startParameter.projectProperties = mapOf("recipe_viewer" to viewer)
	}
}
