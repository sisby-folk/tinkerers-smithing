<!--suppress HtmlDeprecatedTag, XmlDeprecatedElement -->
<center><img alt="tinkerer's smithing banner" src="https://cdn.modrinth.com/data/RhVpNN5O/images/a6122977eb9e1e1113a567f0e422c16960f8feaa.png" /></center><br/>

<center>A sentimental and convenient rebalance of gear crafting, repair, enchanting, and upgrading.<br/>
Server-side, but improves UI and <a href="https://modrinth.com/mod/emi">EMI</a> displays when installed on the client.<br/>
Requires <a href="https://modrinth.com/mod/connector">Connector</a> and <a href="https://modrinth.com/mod/forgified-fabric-api">FFAPI</a> on forge.<br/>
<i>Why nerf mending when you could buff everything else?</i><br/>
</center>

---

Tinkerer's Smithing is a data-driven equipment rebalance that:
 - Allows repairing every vanilla item with durability for no level cost (even fishing rods)
 - Allows upgrading all tools and armor between material tiers (even chainmail)
 - Encourages slowly improving your first set of tools and armor with materials and enchantments - all the way to the endgame.
 - Makes all the enchanted treasure you find lying around actually useful.
 - Balances all of its recipe types on the vanilla cost of equipment.

---

### Tweaked Mechanics

**Anvil Repair**<br/>
![Anvil Repair](https://cdn.modrinth.com/data/RhVpNN5O/images/f7ff03dbd4891e48f3fc31fcf8f2d013e802a34e.png)<br/>
No level cost, no work penalty applied to the result.<br/>
Netherite is rebalanced to use diamonds for repair.<br/>

**Anvil Combine**<br/>
![Anvil Combine](https://cdn.modrinth.com/data/RhVpNN5O/images/e399470dd0d196aa877e7ff824620a2fed7d347d.png)<br/>
No level cost for repairing, and ordering doesn't matter.<br/>

**Keepers (Broken Equipment)**<br/>
![Broken Equipment](https://cdn.modrinth.com/data/RhVpNN5O/images/5a0f3230a213433f2bf8d53e4833aaea8d6bcac1.png)<br/>
Named or enchanted equipment won't break.<br/>
Broken equipment is ineffective until repaired.<br/>

### New Recipe Types

**Anvil De-Working**<br/>
![Anvil De-Working](https://cdn.modrinth.com/data/RhVpNN5O/images/d51392e925b080a728e3fa49aaaf195cc1c55644.png)<br/>
Reduces the equipment's cost multiplier for future anvil crafts.<br/>
(Usually called "Prior Work Penalty" or "RepairCost".)<br/>

**Shapeless Repair**<br/>
![Shapeless Repair](https://cdn.modrinth.com/data/RhVpNN5O/images/1473dac04d7165f42200c1d14c4e9dbe146084f3.gif)<br/>
Costs the same as crafting, but only works when unenchanted.<br/>

**Shapeless Upgrade**<br/>
![Shapeless Upgrade](https://cdn.modrinth.com/data/RhVpNN5O/images/35b23288618b4196a2c163de9d961779707f96f0.png)<br/>
Costs the same as crafting, but keeps existing damage.<br/>

**Smithing Upgrade**<br/>
![Smithing Upgrade](https://cdn.modrinth.com/data/RhVpNN5O/images/0b3eb56aad09e5ec7bac7017541b8d244e534449.gif)<br/>
Costs the same as crafting, or use less material for a damaged result.<br/>

**Sacrificial Upgrade**<br/>
![Sacrificial Upgrade](https://cdn.modrinth.com/data/RhVpNN5O/images/c280850dbf642ca662523b5aa3fa0a58b7424566.png)<br/>
Any type of netherite equipment can be used.<br/>
Resulting damage is based on the sacrificed item's type and damage.<br/>
For "gilded" tiers (like netherite) only.<br/>

### Emergent Gameplay

**Useful Treasure**<br/>
![Treasure Upgrading](https://cdn.modrinth.com/data/RhVpNN5O/images/aed8e9f96c645eb5dc2a608f624ca8742e4545fc.png)<br/>
All vanilla materials are linked to eachother via the upgrade tree.<br/>
It doesn't matter if a pair of Protection IV leggings are made of gold, chain, or leather - just upgrade them to the right material, then you can combine them onto your own equipment at an anvil.<br/>

**Useful Scraps**<br/>
![Wooden Upgrades](https://cdn.modrinth.com/data/RhVpNN5O/images/638a8984edb015b886303688c0d539de0aab37e5.png)<br/>
Like leather armor, forgotten wooden tools can be upgraded to gold enchanting bases or plain old stone, then iron, then diamond.

**Miscellaneous Repair**<br/>
![Miscellaneous Repair](https://cdn.modrinth.com/data/RhVpNN5O/images/cbaab6458d13eb934ab237855af8c6a99c063c71.png)<br/>
All previously unrepairable items now have repair recipes - including utility tools.

---

### Modpack Configuration

Recipes are driven by defining **Tool Materials**, **Armor Materials**, **Equipment Types**, and **Unit Cost Overrides**.

- **Materials** draw from materials in the code, and can inherit them from an item. They define upgrade paths e.g. Iron->Diamond.
    - By default, all vanilla materials are added (wood, stone, leather, chain, iron, gold, diamond, netherite)
- **Types** define which items are "alike". Items with a material upgrade path and a matching type will create upgrade recipes.
    - By default, the 5 tools (e.g. `c:swords`) and 4 armor slots have defined types.
- **Unit Cost Overrides** allow you to override how much (and of what) an item costs to upgrade to or repair.

Check out the the [built-in datapack](https://github.com/sisby-folk/tinkerers-smithing/tree/1.19/src/main/resources/data) for json structure examples for vanilla and a few sample mods.

---

### Older Versions

For minecraft 1.15-1.17, you can download version 1.0 as a [datapack](https://download-directory.github.io/?url=https://github.com/sisby-folk/tinkerers-smithing/tree/nbtc2/src/main/resources) for [NBT Crafting](https://modrinth.com/mod/nbt-crafting) - the [old readme](https://github.com/sisby-folk/tinkerers-smithing/blob/nbtc2-experiments/README.md) lists available features.

---

<center><b>Packs:</b> <a href="https://modrinth.com/modpack/tinkerers-quilt">Tinkerer's Quilt</a> (<a href="https://modrinth.com/modpack/tinkerers-silk">Silk</a>) - <a href="https://modrinth.com/modpack/switchy-pack">Switchy Pack</a></center>
<center><b>Mods:</b> <a href="https://modrinth.com/mod/switchy">Switchy</a> - <a href="https://modrinth.com/mod/origins-minus">Origins Minus</a> (<a href="https://modrinth.com/mod/tinkerers-statures">Statures</a>) - <i>Tinkerer's Smithing</i></center>

---

### Afterword

All mods are built on the work of many others.

This mod is included in [Tinkerer's Quilt](https://modrinth.com/modpack/tinkerers-quilt) - our modpack about rediscovering vanilla.

We're open to suggestions for how to implement stuff better - if you see something wonky and have an idea - let us know.
