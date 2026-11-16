plugins {
	id("me.modmuss50.mod-publish-plugin")
	id("dev.kikugie.loom-back-compat") apply false
	alias(libs.plugins.neoforged.moddev).apply(false)
	alias(libs.plugins.legacyforge.moddev).apply(false)
	alias(libs.plugins.kotlin.jvm).apply(false)
	id("dev.kikugie.fletching-table") apply false
	id("mod-root")
}

stonecutter active file(".sc_active_version")

stonecutter parameters {
	constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
	swaps["mod_version"] = "\"${properties.getAs<String>("mod.version")}\";"
	swaps["mod_id"] = "\"${properties.getAs<String>("mod.id")}\";"
	swaps["mod_name"] = "\"${properties.getAs<String>("mod.name")}\";"
	swaps["mod_group"] = "\"${properties.getAs<String>("mod.group")}\";"
	swaps["minecraft"] = "\"${current.version}\";"
	constants["release"] = properties.getAs<String>("mod.id") != "modtemplate"
}

