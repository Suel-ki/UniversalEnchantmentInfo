import java.io.File

plugins {
	id("mod-platform")
	id("dev.kikugie.loom-back-compat")
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
	loader = "fabric"
	dependencies {
		required("minecraft") { fabricLikeVersionRange = prop("deps.minecraft") }
		required("fabricloader") { fabricLikeVersionRange = ">=0.15" }
		required("fabric-api") { slug("fabric-api"); fabricLikeVersionRange = "*" }
		if (hasProp("deps.modmenu")) optional("modmenu") { slug("modmenu") }
		if (hasProp("deps.jei")) optional("jei") { slug("jei") }
		if (hasProp("deps.rei")) optional("rei") { slug("roughly-enough-items") }
		if (hasProp("deps.emi")) optional("emi") { slug("emi") }
		if (hasProp("deps.rrv")) optional("rrv") { slug("rrv") }
	}
}

loom {
	runs.named("client") {
		client()
		runDirectory.set(layout.projectDirectory.dir("run"))
		programArguments.add("--username=Dev")
		displayName.set("Fabric Client (${sc.current.version})")
	}
	runs.named("server") {
		server()
		runDirectory.set(layout.projectDirectory.dir("run"))
		displayName.set("Fabric Server (${sc.current.version})")
	}
}

repositories {
	mavenCentral()
	maven("https://maven.blamejared.com/") { name = "BlameJaredPlain" }
	maven("https://maven.shedaniel.me/") { name = "ShedanielPlain" }
	maven("https://repo.sleeping.town/") { name = "EMIPlain" }
	maven("https://maven.architectury.dev/") { name = "ArchitecturyPlain" }
	maven("https://maven.architectury.dev/") { name = "ArchitecturyPlain2" }
	strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc") { name = "TerraformersMC" }
	strictMaven("https://maven.blamejared.com/", "mezz.jei") { name = "BlameJared" }
	strictMaven("https://maven.shedaniel.me/", "me.shedaniel", "me.shedaniel.cloth") { name = "Shedaniel" }
	strictMaven("https://repo.sleeping.town/", "dev.emi") { name = "EMI" }
	strictMaven("https://maven.cassian.cc", "cc.cassian", "cc.cassian.rrv") { name = "Cassian" }
	strictMaven("https://maven.architectury.dev/", "dev.architectury") { name = "Architectury" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	strictMaven("https://www.cursemaven.com", "curse.maven") { name = "CurseMaven" }
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
	if (sc.current.parsed < "26") mappings(loom.layered { officialMojangMappings() })
	modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric-loader")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	if (hasProp("deps.modmenu")) {
		modCompileOnly("com.terraformersmc:modmenu:${prop("deps.modmenu")}")
		modLocalRuntime("com.terraformersmc:modmenu:${prop("deps.modmenu")}")
	}
	if (hasProp("deps.jei")) {
		modCompileOnly("mezz.jei:jei-${prop("deps.minecraft")}-fabric:${prop("deps.jei")}")
		if (viewerSelected("jei")) modLocalRuntime("mezz.jei:jei-${prop("deps.minecraft")}-fabric:${prop("deps.jei")}")
	}
	if (hasProp("deps.rei")) {
		modCompileOnly("me.shedaniel:RoughlyEnoughItems-api:${prop("deps.rei")}")
		modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin:${prop("deps.rei")}")
		if (viewerSelected("rei")) {
			modLocalRuntime("me.shedaniel:RoughlyEnoughItems-fabric:${prop("deps.rei")}")
			modLocalRuntime("me.shedaniel.cloth:cloth-config-fabric:${prop("deps.cloth-config")}")
			modLocalRuntime("dev.architectury:architectury-fabric:${prop("deps.architectury")}")
		}
	}
	if (hasProp("deps.emi")) {
		if (sc.current.parsed < "1.21.2") {
			compileOnly("dev.emi:emi-xplat-mojmap:${prop("deps.emi")}:api")
			compileOnly("dev.emi:emi-xplat-mojmap:${prop("deps.emi")}")
			if (viewerSelected("emi")) modLocalRuntime("dev.emi:emi-fabric:${prop("deps.emi")}")
		} else {
			modCompileOnly("maven.modrinth:pf-emi:${prop("deps.emi")}")
			if (viewerSelected("emi")) modLocalRuntime("maven.modrinth:pf-emi:${prop("deps.emi")}")
		}
	}
	if (hasProp("deps.rrv")) {
		modCompileOnly("cc.cassian.rrv:reliable-recipe-viewer-fabric:${prop("deps.rrv")}+${prop("deps.minecraft")}")
		if (viewerSelected("rrv")) modLocalRuntime("cc.cassian.rrv:reliable-recipe-viewer-fabric:${prop("deps.rrv")}+${prop("deps.minecraft")}")
	}
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
