<div align="center">

# Universal Enchantment Info (JEI/REI/EMI/RRV)

<img align="right" width="100" src="src/main/resources/assets/uei/icon.png" alt="Logo">

[![neoforge](https://cdn.jsdelivr.net/gh/Hyperbole-Devs/vectors@neoforge_badges/assets/cozy/supported/neoforge_vector.svg)](https://modrinth.com/mod/uei/versions?l=neoforge)
[![forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg)](https://modrinth.com/mod/uei/versions?l=forge)
[![fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg)](https://modrinth.com/mod/uei/versions?l=fabric)
[![Available on Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/uei)
[![Available on Curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://curseforge.com/minecraft/mc-mods/uei)

[![Modrinth](https://img.shields.io/modrinth/dt/uei?color=00AF5C&label=downloads&logo=modrinth)](https://modrinth.com/mod/uei)
[![CurseForge](https://cf.way2muchnoise.eu/full_1630244_downloads.svg)](https://curseforge.com/minecraft/mc-mods/uei)
![Available](https://cf.way2muchnoise.eu/versions/1630244.svg)

</div>

## About

A JEI/REI/EMI/RRV plugin that displays detailed enchantment information.

## Usage

- **R key** — view enchantment info for enchanted book
- **U key** — view info of exclusive enchantments for enchanted book
- **U key on an item** — view all enchantments applicable to it
- **Shift+V on an item slot** — open the info pages of the enchantments on that item

## Features

- **Description** — enchantment effect description (uses `enchantment.namespace.path.desc` translation key)
- **Rarity** — with customizable colors
- **Max Level** — maximum enchantment level
- **Treasure / Tradeable / Curse / Discoverable / Enchanting Table** — enchantment flags
- **Enchanting Power** — enchanting power window needed to roll the highest level
- **Hover tooltips** — every info line explains itself on hover
- **Applicable Items** — items that accept this enchantment
- **Exclusive Enchantments** — mutually exclusive enchantments
- **Scrollable Panel** — with mouse wheel and drag support

## Config

Configurable in-game: Fabric requires Mod Menu, while Forge and NeoForge use the native mod list config screen.
The file lives in `config/uei.json` and can also be edited by hand.

### Appearance

| Option | Default | Description |
| --- | --- | --- |
| Scroll Speed | `1.0` | Panel scroll sensitivity, `0.1`–`10` |
| Text Scroll Speed | `1.0` | Long description auto-scroll multiplier, `0.1`–`10` |
| Applicable Item Slots | `4` | Item slots shown per page in the applicable items list |
| Use Text For Booleans | `false` | Show Yes/No instead of check/cross glyphs |
| Rarity Colors | per tier | Common, Uncommon, Rare and Very Rare/Epic colors as `0xRRGGBB` integers |

### Info Lines

Each line can be toggled independently; `Info Order` and `Section Order` control their order in the panel.

| Option | Default | Description |
| --- | --- | --- |
| Show Rarity | `true` | Rarity line |
| Show Max Level | `true` | Maximum level line |
| Show Treasure | `true` | Treasure-only line |
| Show Tradeable | `true` | Villager tradeable line |
| Show Curse | `true` | Curse line |
| Show Discoverable | `true` | Loot discoverable line |
| Show Enchanting Table | `true` | Whether the enchantment can come from an enchanting table |
| Show Enchanting Power | `true` | Enchanting power window in which the highest level can be rolled (hidden for treasure and curse enchantments) |
| Show Info Tooltips | `true` | Hover hints explaining each info line |
| Info Order | all lines | Order of the info lines |
| Section Order | description, info, items, exclusives | Order of the panel sections |

### Behaviour

| Option | Default | Description |
| --- | --- | --- |
| Lookup Enchantments By Item | `true` | Press U on an item to list every enchantment it accepts |
| Lookup Item Enchantments | `true` | Press Shift+V on an item slot to open the info pages of the enchantments on that item |
| Applicable Item Sort | `REGISTRY` | Order of applicable items: `REGISTRY`, `NAME` or `MOD` |
| Apotheosis / Apothic Compat | `true` | Use the real max level from Apotheosis or Apothic Enchanting (only shown when installed) |

### Filtering

Hidden enchantments disappear from every recipe viewer.

| Option | Default | Description |
| --- | --- | --- |
| Blacklisted Mods | empty | Mod IDs (namespaces); every enchantment of that mod is hidden |
| Blacklisted Enchantments | empty | Enchantment IDs such as `minecraft:sharpness` |

Descriptions are read from `enchantment.<namespace>.<path>.desc`, falling back to `enchantment.<namespace>.<path>.description`
(the key used by Item Descriptions), then to a "no description" placeholder.
