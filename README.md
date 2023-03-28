<center><img alt="tinkerer's smithing banner" src="https://user-images.githubusercontent.com/55819817/184476819-6cf95348-93da-4bc2-9582-ba32ee0364bd.png" /></center>

<center>A sentimental and convenient rebalance of gear crafting, repair, enchanting, and upgrading.<br/>
Satisfying to use, but fits nicely with vanilla.<br/>
Requires <a href="https://modrinth.com/mod/nbt-crafting">NBT Crafting</a>, and works well with <a href="https://modrinth.com/mod/emi">EMI</a> and <a href="https://modrinth.com/mod/inspecio">Inspecio</a>.
</center>

---

This mod:
- Adds repair recipes for all equipment with no XP or prior work cost.
  - For basic tools, add up to 3 material in the crafting grid to repair based on tool cost.
  - For other equipment, add one material at a smithing table to repair 25% durability, like an anvil.
- Adds upgrade recipes for all equipment.
  - For iron, just add a diamond at a smithing table. Excludes chestplates and leggings.
  - For wood and stone tools, add the full amount of new material to the tool in the crafting grid.
  - For golden gear, add any piece of netherite gear to it at the smithing table.
- Adds a recipe to "de-work" equipment, lowering the cost of future anvil operations.
  - For iron and above, just add netherite scrap at the smithing table.

All added recipes keep tools as-is - with enchantments, custom names, and any modded data.

Tinkerer's Smithing encourages enchanting and naming your tools much earlier - and keeping them in good condition with repairs and small improvements.

If you'd prefer the datapack version for any reason, you can [download it from the repo](https://download-directory.github.io/?url=https://github.com/sisby-folk/tinkerers-smithing/tree/main/src/main/resources) (Direct Download Link)

## Showcase

<iframe width="896" height="504" src="https://www.youtube.com/embed/q7KKN9hn7Uo" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

## Design - Progression and Mending

In vanilla, endgame gear is made by repeatedly enchanting and un-enchanting for good RNG, then adding books (like mending).
Any other method gives an immensely worse result due to `repairCost`/'Prior Work'.
This punishes the player substantially for slowly improving gear.

Even repair inflicts prior work - rushing a mending villager is the only solution.
This ruins the idea of having "sentimental" gear you improve and maintain over time.
Finding mending villagers is also boring - but the XP cost is too punishing otherwise.

Tinkerer's Smithing doesn't remove prior work, and it doesn't nerf or remove mending. <br/>
Its recipes instead buff the rest of the game, altering balance as follows:

- Adds three new non-RNG methods to obtaining endgame gear
    - Early game gear can be gradually combined with books, other gear, and netherite scrap.
    - Enchanted golden treasure can be combined and upgraded to netherite.
    - Enchanted gear can be bought from villagers and combined, then upgraded.
- Adds one additional RNG-based method to obtain endgame gear: upgrading table-enchanted golden gear.
    - At the point of having netherite, this is less of an issue as its enchantability is higher.
    - Netherite scrap can also be used to continue combining already powerful gear, nerfing this strategy by proxy.
- Obsidian / Ancient Debris can be mined after obtaining only one diamond.
- Netherite gear can technically be exchanged for other netherite gear (removes enchantments).

## Design - Recipe Cost

### Repairing
- Repairing **wooden**/**stone** tools from 0 to full costs the same as a fresh tool. 1 unit repairs 1/3 of a pickaxe.
- Repairing other gear uses anvil balancing - One unit repairs 25% no matter what.
  - If 4 iron to repair an iron shovel seems steep - make fresh shovels or add enchantments as soon as you can!

### Upgrading
- Upgrading **wooden**/**stone** tools costs the same units as the fresh tool.
    - As a convenience cost, damage to the tool will be "passed on" to the new tool - as if left in the handle.
- Upgrading other tiers of gear costs only one unit. However, it leverages above repair mechanics:
    - Gear other than shovels need unit repairs afterwards - Swords need one, axes need two, etc.
    - This is why leggings and chestplates aren't upgradeable: -50% durability is not a thing.

### Sacrificial Upgrading
Upgrading to **netherite** from **gold** utilizes specially-designed 'sacrificial diamond' logic.

- Upgrades cost one piece of netherite gear.
- The netherite gear is considered to have a full netherite ingot inside for the upgrade.
- The netherite gear is broken into 'sacrificial diamonds' based on its unit cost and durability.
    - A full durability chestplate is worth 8 diamonds, a 1/5 durability helmet is worth 1 diamond.
- The resulting gear's durability is relative to its own unit cost.
    - 1 sacrificial diamond will give a 1/2 durability sword, or a 1/3 durability pickaxe.
    - all gear types can be used as sacrifice, but some are very wasteful.

### Afterword

All mods are built on the work of many others.

This mod is included in [Tinkerer's Quilt](https://modrinth.com/modpack/tinkerers-quilt) - our modpack about rediscovering vanilla.

Textures shown include [xali's books](https://www.curseforge.com/minecraft/texture-packs/xalis-enchanted-books), [Sully's Peeves](https://www.curseforge.com/minecraft/texture-packs/sullys-peeves) and [Vanilla Tweaks](https://vanillatweaks.net/picker/resource-packs/)' degrading tools

We're open to suggestions for how to implement stuff better - if you see something wonky and have an idea - let us know.
