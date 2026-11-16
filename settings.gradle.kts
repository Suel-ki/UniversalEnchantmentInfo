pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.architectury.dev/") { name = "Architectury" }
		maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
		maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.10-beta.1"
	id("dev.kikugie.loom-back-compat") version "0.4.1"
}

stonecutter {
	create(rootProject) {
		fun match(version: String, vararg loaders: String) =
			loaders.forEach { version("$version-$it", version).buildscript = "build.$it.gradle.kts" }

		match("1.20.1", "fabric", "forge")
		match("1.21.1", "fabric", "neoforge")
		match("1.21.4", "fabric", "neoforge")
		match("1.21.8", "fabric", "neoforge")
		match("1.21.10", "fabric", "neoforge")
		match("1.21.11", "fabric", "neoforge")
		match("26.1.2", "fabric", "neoforge")
		match("26.2", "fabric", "neoforge")
		match("26.3", "fabric", "neoforge")

		vcsVersion = "26.3-fabric"
	}
}

rootProject.name = "UniversalEnchantmentInfo"
