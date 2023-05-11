<center><img alt="tinkerer's smithing banner" src="https://user-images.githubusercontent.com/55819817/184476819-6cf95348-93da-4bc2-9582-ba32ee0364bd.png" /></center>

<center>A sentimental and convenient rebalance of gear crafting, repair, enchanting, and upgrading.<br/>
Works well with <a href="https://modrinth.com/mod/emi">EMI</a> and <a href="https://modrinth.com/mod/inspecio">Inspecio</a>.
</center>

---

<center><b>Packs:</b> <a href="https://modrinth.com/modpack/tinkerers-quilt">Tinkerer's Quilt</a> (<a href="https://modrinth.com/modpack/tinkerers-silk">Silk</a>) - <a href="https://modrinth.com/modpack/switchy-pack">Switchy Pack</a></center>
<center><b>Mods:</b> <a href="https://modrinth.com/mod/switchy">Switchy</a> - <a href="https://modrinth.com/mod/origins-minus">Origins Minus</a> (<a href="https://modrinth.com/mod/tinkerers-statures">Statures</a>) - <i>Tinkerer's Smithing</i></center>

---

As of 2.0, the mod has been completely rewritten. It adds six recipe types to rebalance equipment.

All of them are data-driven, and preserve enchantments and names.

1. Anvil Unit Repair: As seen in vanilla, TSmithing removes the XP and prior work cost.
2. Anvil De-Working: Take worked equipment and add netherite scrap to regress repair cost by one stage.
3. Shapeless Unit Repair: Repair unenchanted equipment based on the cost of crafting it fresh.
4. Shapeless Upgrade: Upgrade unenchanted equipment based on the cost of crafting it fresh.
5. Smithing Upgrade: Upgrade equipment in full, or use less units for a partial durability result.
6. Smithing Sacrificial Upgrade: Upgrade straight to 'gilded' tiers (netherite) by sacrificing an existing item of that tier.

Out of the box, TSmithing makes **every damageable vanilla item repairable** and **every vanilla equipment upgradeable** (yes, even chain).

As a result of these changes, it's easier to enchant and name your tools much earlier - keeping them in good condition with repairs and small improvements.

The more limited datapack version is also available for NBT Crafting [2.2.3](https://download-directory.github.io/?url=https://github.com/sisby-folk/tinkerers-smithing/tree/nbtc2/src/main/resources) - see the [old readme](https://github.com/sisby-folk/tinkerers-smithing/blob/nbtc2-experiments/README.md) for a rough idea of what's there.

## Showcase

<iframe width="896" height="504" src="https://www.youtube.com/embed/q7KKN9hn7Uo" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

## Design - Vanilla Recipe Cost

### Repairing
- Shapeless repair from 0 to full costs the same as a fresh craft. 1 unit repairs 1/3 of a pickaxe.
- Anvil repair keeps anvil balancing - One unit repairs 25% no matter what.

### Upgrading
- Shapeless upgrades cost the same units as the fresh tool.
    - As a convenience cost, damage to the tool will be "passed on" to the new tool - as if left in the handle.
- Smithing upgrades cost as much as the full cost, or up to four less, leveraging repair mechanics.
    - An iron chestplate upgraded with 5 diamonds will have 25% durability, requiring 3 more in repairs to reach full.
- Sacrificial upgrades cost one tool of the same tier, and have durability based on the cost of the ungilded equipment.
    - A half durability netherite boots "sacrifices" two diamonds to the recipe, enough for a full sword, or a 1/4 durability chestplate.

## Design - Vanilla Progression and Mending

In vanilla, as you enchant and combine a tool on the anvil, it becomes exponentially more expensive as it accrues "work"/`RepairCost` - Eventually, any tool becomes unworkable.
This means endgame gear must be instead made by repeatedly enchanting and un-enchanting for good RNG, then adding books (like mending).  Even repair inflicts prior work - forcing players to rush a mending villager.

This ruins the idea of having "sentimental" gear you improve and maintain over time.

Instead of nerfing mending, TSmithing buffs everything else, altering balance as follows:

- Adds three new non-RNG methods to obtaining endgame gear
    - Early game gear can be gradually combined with books, other gear, and netherite scrap.
    - Enchanted golden treasure can be combined and upgraded to netherite.
    - Enchanted gear can be bought from villagers and combined, then upgraded.
- Adds one additional RNG-based method to obtain endgame gear: upgrading table-enchanted golden gear.
    - At the point of having netherite, this is less of an issue as its enchantability is higher.
    - Netherite scrap can also be used to continue combining already powerful gear, nerfing this strategy by proxy.
- Obsidian / Ancient Debris can be mined after obtaining only one diamond.
- Netherite gear can technically be exchanged for other netherite gear (removes enchantments).


## Data-driven Recipes

Tinkerer's Smithing 2.0 includes built-in data for vanilla, along with the below mods:

[Yttr](https://modrinth.com/mod/yttr) | [Farmer's Delight](https://modrinth.com/mod/farmers-delight-fabric) | [Botania](https://modrinth.com/mod/botania) | [Create](https://modrinth.com/mod/create-fabric) | [Chalk](https://modrinth.com/mod/chalk) | [Campanion](https://modrinth.com/mod/farmers-delight-fabric) | [Consistency+](https://modrinth.com/mod/consistencyplus)

Recipes are driven by defining **Tool Materials**, **Armor Materials**, **Equipment Types**, and **Unit Cost Overrides**.

 - **Materials** draw from materials in the code, and can inherit them from an item. They define upgrade paths e.g. Iron->Diamond.
   - By default, all vanilla materials are added (wood, stone, leather, chain, iron, gold, diamond, netherite)
 - **Types** define which items are "alike". Items with a material upgrade path and a matching type will create upgrade recipes.
   - By default, the 5 tools (e.g. `c:swords`) and 4 armor slots have defined types.
 - **Unit Cost Overrides** allow you to override how much (and of what) an item costs to repair in-grid or upgrade to.

Further documentation will be made once 2.0 sures up a bit, but feel free to look at the [built-in datapack](https://github.com/sisby-folk/tinkerers-smithing/tree/1.19/src/main/resources/data)

### Afterword

All mods are built on the work of many others.

This mod is included in [Tinkerer's Quilt](https://modrinth.com/modpack/tinkerers-quilt) - our modpack about rediscovering vanilla.

Textures shown include [xali's books](https://www.curseforge.com/minecraft/texture-packs/xalis-enchanted-books), [Sully's Peeves](https://www.curseforge.com/minecraft/texture-packs/sullys-peeves) and [Vanilla Tweaks](https://vanillatweaks.net/picker/resource-packs/)' degrading tools

We're open to suggestions for how to implement stuff better - if you see something wonky and have an idea - let us know.
