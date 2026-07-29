<div align="center">

# Universal Enchantment Info (JEI/REI/EMI)

<img align="right" width="100" src="common/src/main/resources/logo.png" alt="Logo">

[![neoforge](https://cdn.jsdelivr.net/gh/Hyperbole-Devs/vectors@neoforge_badges/assets/cozy/supported/neoforge_vector.svg)](https://modrinth.com/mod/uei/versions?l=neoforge)
[![forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg)](https://modrinth.com/mod/uei/versions?l=forge)
[![fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg)](https://modrinth.com/mod/uei/versions?l=fabric)
[![Available on Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/uei)
[![Available on Curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://curseforge.com/minecraft/mc-mods/uei)

[![Modrinth](https://img.shields.io/modrinth/dt/uei?color=00AF5C&label=downloads&logo=modrinth)](https://modrinth.com/mod/uei)
[![CurseForge](https://cf.way2muchnoise.eu/full_1630244_downloads.svg)](https://curseforge.com/minecraft/mc-mods/uei)

</div>

## About

A JEI/REI/EMI plugin that displays detailed enchantment information.

## Usage

- **R key** — view enchantment info for enchanted book
- **U key** — view info of exclusive enchantments for enchanted book

## Features

- **Description** — enchantment effect description (uses `enchantment.namespace.path.desc` translation key)
- **Rarity** — with customizable colors
- **Max Level** — maximum enchantment level
- **Treasure / Tradeable / Curse / Discoverable / Enchanting Table** — enchantment flags
- **Applicable Items** — items that accept this enchantment
- **Exclusive Enchantments** — mutually exclusive enchantments
- **Scrollable Panel** — with mouse wheel and drag support

## Config

Configurable in-game: Fabric requires Mod Menu, while Forge and NeoForge use the native mod list config screen.

| Option                | Default | Description                        |
| --------------------- | ------- | ---------------------------------- |
| Scroll Speed          | `1.0`   | Panel scroll sensitivity           |
| Text Scroll Speed     | `1.0`   | Long text auto-scroll multiplier   |
| Applicable Item Slots | `4`     | Max displayed item slots           |
| Applicable Item Limit | `100`   | Max collected applicable items     |
| Text Boolean Display  | `false` | Use Yes/No instead of symbols      |
| Show Rarity           | `true`  | Toggle rarity line                 |
| Show Max Level        | `true`  | Toggle max level line              |
| Show Treasure         | `true`  | Toggle treasure line               |
| Show Tradeable        | `true`  | Toggle tradeable line              |
| Show Curse            | `true`  | Toggle curse line                  |
| Show Discoverable     | `true`  | Toggle discoverable line           |
| Show Enchanting Table | `true`  | Toggle enchanting table line       |
| Rarity Colors         | default | Four rarity color values (integer) |
