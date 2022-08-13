# Tinkerer's Smithing

A sentimental and convenient rebalance of gear crafting, repair, enchanting, combining, and smithing.
Satisfying to use, but tucks away nicely next to vanilla.

This mod:
- Removes the XP and work cost of (enchantment-safe) unit repair for gear - Simply use a **smithing table**.
    - Also adds convenient unit repair to stone and wooden tools in any crafting grid.
- Adds enchantment-safe upgrade paths for gear to encourage more experimentation, and "Sentimental Gear".
    - Upgrade iron gear (ex. chestplates/leggings) to diamond gear at the smithing table.
    - For convenience, upgrade wood and stone tools right from the crafting table.
    - Sacrifice any piece of netherite gear at a smithing table to upgrade gold gear to netherite.
- Adds a way to recover "overworked"/"too expensive" enchanted gear using netherite scrap at a smtihing table.
    - Lowers the cost of future anvil operations - as if you'd worked with it one less time.


It requires [NBT Crafting](https://modrinth.com/mod/nbt-crafting), and we recommend [EMI](https://modrinth.com/mod/emi), [Inventory Tabs](https://modrinth.com/mod/inventory-tabs-updated), and [Inspecio](https://modrinth.com/mod/inspecio) to improve the experience.

If you'd prefer the datapack version for any reason, you can [download it from the repo](https://download-directory.github.io/?url=https://github.com/sisby-folk/tinkerers-smithing/tree/main/src/main/resources/data/tinkerers_smithing) (Direct Download Link)

## Showcase

![screenshot recipe summary](https://cdn.modrinth.com/data/RhVpNN5O/images/a245f761515190d5864524ea9c20fe621103df09.png)

Screenshots include [xali's books](https://www.curseforge.com/minecraft/texture-packs/xalis-enchanted-books), [Sully's Peeves](https://www.curseforge.com/minecraft/texture-packs/sullys-peeves) and [Vanilla Tweaks](https://vanillatweaks.net/picker/resource-packs/)' degrading tools

## Design - The Mending Problem

The best gear in vanilla is made by repeatedly un-enchanting gear and enchanting it until the player gets gear RNG, then finishing it off with a couple of books (especially mending).
Any other method gives an immensely worse result due to `repairCost`/'Prior Work'.
This punishes the player substantially for slowly improving gear.

Even repair inflicts prior work - rushing a mending villager is the only solution.
This ruins the idea of having "sentimental" gear you improve and maintain over time.
Finding mending villagers is also immensely boring - but the XP cost is too punishing otherwise.

To combat this, TSmithing introduces work-free unit repair, "de-working" with netherite, material-upgrading.

## Design - Cost in Minecraft

### Repairing
- Repairing **wooden**/**stone** tools from 0 to full costs the same as a fresh tool. 1 unit repairs 1/3 of a pickaxe.
- Repairing other gear uses anvil balancing - One unit repairs 25% no matter what.

### Upgrading
- Upgrading **wooden**/**stone** tools costs the same units as the fresh tool.
    - As a convenience cost, damage to the tool will be "passed on" to the new tool - as if left in the handle.
- Upgrading other tiers of gear costs only one unit. However, it leverages above repair mechanics:
    - Gear other than shovels need unit repairs afterwards - Swords need one, axes need two, etc.

### Sacrificial Upgrading
Upgrading to **netherite** from **gold** utilizes specially-designed 'sacrificial diamond' logic.

- Upgrades cost one piece of netherite gear as 'sacrificial gear'.
- Any sacrificial gear is considered to have the full netherite ingot inside for the upgrade from diamond.
- The sacrifice is broken into 'sacrificial diamonds' based on unit cost and durability.
    - A full durability chestplate is worth 8 diamonds, a 1/5 durability helmet is worth 1 diamond.
- The resulting gear's durability is relative to its own unit cost.
    - 1 sacrificial diamond will give a 1/2 durability sword, or a 1/3 durability pickaxe.
    - all gear types can be used as sacrifice, but some are very wasteful.

### Enchantability
As gold and iron have higher enchantability than their upgrade tiers, it's possible to use the better RNG to enchant fresh gear
This is slightly harder to abuse as gold goes straight to netherite -
the gap is smaller, and the netherite used could have been spent de-working.

## Further Info

All modding projects are built on the work of many others.

We're primarily modpack developers - not mod developers! If you want to port this mod, do it yourself!
