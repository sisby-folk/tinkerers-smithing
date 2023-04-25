package folk.sisby.tinkerers_smithing;

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import com.simibubi.create.AllItems;
import com.terraformersmc.campanion.item.CampanionItems;
import com.unascribed.yttr.init.YItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import vazkii.botania.common.item.BotaniaItems;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TinkerersSmithingRecipeGenerator extends FabricRecipeProvider {
	public static final Map<Item, TinkerersEquipment> equipment = new HashMap<>();
	public static void registerEquipment(TinkerersEquipment te) { equipment.put(te.item(), te); }

	static {
		// Netherite
		registerEquipment(new TinkerersEquipment(Items.NETHERITE_SHOVEL, 1, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));
		registerEquipment(new TinkerersEquipment(Items.NETHERITE_SWORD, 2, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));
		registerEquipment(new TinkerersEquipment(Items.NETHERITE_HOE, 2, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));
		registerEquipment(new TinkerersEquipment(Items.NETHERITE_PICKAXE, 3, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));
		registerEquipment(new TinkerersEquipment(Items.NETHERITE_AXE, 3, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));
		registerEquipment(new TinkerersEquipment(Items.NETHERITE_BOOTS, 4, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));
		registerEquipment(new TinkerersEquipment(Items.NETHERITE_HELMET, 5, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));
		registerEquipment(new TinkerersEquipment(Items.NETHERITE_LEGGINGS, 7, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));
		registerEquipment(new TinkerersEquipment(Items.NETHERITE_CHESTPLATE, 8, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));

		// Diamond
		registerEquipment(new TinkerersEquipment(Items.DIAMOND_SHOVEL, 1, false, Ingredient.ofItems(Items.DIAMOND)));
		registerEquipment(new TinkerersEquipment(Items.DIAMOND_SWORD, 2, false, Ingredient.ofItems(Items.DIAMOND)));
		registerEquipment(new TinkerersEquipment(Items.DIAMOND_HOE, 2, false, Ingredient.ofItems(Items.DIAMOND)));
		registerEquipment(new TinkerersEquipment(Items.DIAMOND_PICKAXE, 3, false, Ingredient.ofItems(Items.DIAMOND)));
		registerEquipment(new TinkerersEquipment(Items.DIAMOND_AXE, 3, false, Ingredient.ofItems(Items.DIAMOND)));
		registerEquipment(new TinkerersEquipment(Items.DIAMOND_BOOTS, 4, false, Ingredient.ofItems(Items.DIAMOND)));
		registerEquipment(new TinkerersEquipment(Items.DIAMOND_HELMET, 5, false, Ingredient.ofItems(Items.DIAMOND)));
		registerEquipment(new TinkerersEquipment(Items.DIAMOND_LEGGINGS, 7, false, Ingredient.ofItems(Items.DIAMOND)));
		registerEquipment(new TinkerersEquipment(Items.DIAMOND_CHESTPLATE, 8, false, Ingredient.ofItems(Items.DIAMOND)));

		// Iron
		registerEquipment(new TinkerersEquipment(Items.IRON_SHOVEL, 1, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_SHOVEL)));
		registerEquipment(new TinkerersEquipment(Items.IRON_SWORD, 2, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_SWORD)));
		registerEquipment(new TinkerersEquipment(Items.IRON_HOE, 2, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_HOE)));
		registerEquipment(new TinkerersEquipment(Items.IRON_PICKAXE, 3, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_PICKAXE)));
		registerEquipment(new TinkerersEquipment(Items.IRON_AXE, 3, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_AXE)));
		registerEquipment(new TinkerersEquipment(Items.IRON_BOOTS, 4, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_BOOTS)));
		registerEquipment(new TinkerersEquipment(Items.IRON_HELMET, 5, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_HELMET)));
		registerEquipment(new TinkerersEquipment(Items.IRON_LEGGINGS, 7, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_LEGGINGS)));
		registerEquipment(new TinkerersEquipment(Items.IRON_CHESTPLATE, 8, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_CHESTPLATE)));

		// Chain
		registerEquipment(new TinkerersEquipment(Items.CHAINMAIL_BOOTS, 4, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_BOOTS)));
		registerEquipment(new TinkerersEquipment(Items.CHAINMAIL_HELMET, 5, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_HELMET)));
		registerEquipment(new TinkerersEquipment(Items.CHAINMAIL_LEGGINGS, 7, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_LEGGINGS)));
		registerEquipment(new TinkerersEquipment(Items.CHAINMAIL_CHESTPLATE, 8, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_CHESTPLATE)));

		// Leather
		registerEquipment(new TinkerersEquipment(Items.LEATHER_BOOTS, 4, true, Ingredient.ofItems(Items.LEATHER), equipment.get(Items.IRON_BOOTS)));
		registerEquipment(new TinkerersEquipment(Items.LEATHER_HELMET, 5, true, Ingredient.ofItems(Items.LEATHER), equipment.get(Items.IRON_HELMET)));
		registerEquipment(new TinkerersEquipment(Items.LEATHER_LEGGINGS, 7, true, Ingredient.ofItems(Items.LEATHER), equipment.get(Items.IRON_LEGGINGS)));
		registerEquipment(new TinkerersEquipment(Items.LEATHER_CHESTPLATE, 8, true, Ingredient.ofItems(Items.LEATHER), equipment.get(Items.IRON_CHESTPLATE)));

		// Stone
		registerEquipment(new TinkerersEquipment(Items.STONE_SHOVEL, 1, true, Ingredient.ofTag(ItemTags.STONE_TOOL_MATERIALS), equipment.get(Items.IRON_SHOVEL)));
		registerEquipment(new TinkerersEquipment(Items.STONE_SWORD, 2, true, Ingredient.ofTag(ItemTags.STONE_TOOL_MATERIALS), equipment.get(Items.IRON_SWORD)));
		registerEquipment(new TinkerersEquipment(Items.STONE_HOE, 2, true, Ingredient.ofTag(ItemTags.STONE_TOOL_MATERIALS), equipment.get(Items.IRON_HOE)));
		registerEquipment(new TinkerersEquipment(Items.STONE_PICKAXE, 3, true, Ingredient.ofTag(ItemTags.STONE_TOOL_MATERIALS), equipment.get(Items.IRON_PICKAXE)));
		registerEquipment(new TinkerersEquipment(Items.STONE_AXE, 3, true, Ingredient.ofTag(ItemTags.STONE_TOOL_MATERIALS), equipment.get(Items.IRON_AXE)));

		// Wooden
		registerEquipment(new TinkerersEquipment(Items.WOODEN_SHOVEL, 1, true, Ingredient.ofTag(ItemTags.PLANKS), equipment.get(Items.STONE_SHOVEL)));
		registerEquipment(new TinkerersEquipment(Items.WOODEN_SWORD, 2, true, Ingredient.ofTag(ItemTags.PLANKS), equipment.get(Items.STONE_SWORD)));
		registerEquipment(new TinkerersEquipment(Items.WOODEN_HOE, 2, true, Ingredient.ofTag(ItemTags.PLANKS), equipment.get(Items.STONE_HOE)));
		registerEquipment(new TinkerersEquipment(Items.WOODEN_PICKAXE, 3, true, Ingredient.ofTag(ItemTags.PLANKS), equipment.get(Items.STONE_PICKAXE)));
		registerEquipment(new TinkerersEquipment(Items.WOODEN_AXE, 3, true, Ingredient.ofTag(ItemTags.PLANKS), equipment.get(Items.STONE_AXE)));

		// Misc
		registerEquipment(new TinkerersEquipment(Items.TURTLE_HELMET, 5, false, Ingredient.ofItems(Items.SCUTE)));
		registerEquipment(new TinkerersEquipment(Items.ELYTRA, 1, false, Ingredient.ofItems(Items.PHANTOM_MEMBRANE)));

		// Misc 2
		registerEquipment(new TinkerersEquipment(Items.FLINT_AND_STEEL, 1, true, Ingredient.ofItems(Items.IRON_INGOT)));
		registerEquipment(new TinkerersEquipment(Items.FISHING_ROD, 2, true, Ingredient.ofItems(Items.STRING)));
		registerEquipment(new TinkerersEquipment(Items.BOW, 3, true, Ingredient.ofItems(Items.STRING)));
		registerEquipment(new TinkerersEquipment(Items.CROSSBOW, 1, true, Ingredient.ofItems(Items.TRIPWIRE_HOOK)));
		registerEquipment(new TinkerersEquipment(Items.CARROT_ON_A_STICK, 1, true, Ingredient.ofItems(Items.CARROT)));
		registerEquipment(new TinkerersEquipment(Items.WARPED_FUNGUS_ON_A_STICK, 1, true, Ingredient.ofItems(Items.WARPED_FUNGUS)));

		// Misc 3
		registerEquipment(new TinkerersEquipment(Items.SHEARS, 2, false, Ingredient.ofItems(Items.IRON_INGOT)));
		registerEquipment(new TinkerersEquipment(Items.TRIDENT, 3, false, Ingredient.ofItems(Items.PRISMARINE_CRYSTALS)));
		registerEquipment(new TinkerersEquipment(Items.SHIELD, 1, false, Ingredient.ofItems(Items.IRON_INGOT)));

		// Farmers Delight
		registerEquipment(new TinkerersEquipment(ItemsRegistry.NETHERITE_KNIFE.get(), List.of("farmersdelight"), 1, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));
		registerEquipment(new TinkerersEquipment(ItemsRegistry.DIAMOND_KNIFE.get(), List.of("farmersdelight"), 1, false, Ingredient.ofItems(Items.DIAMOND)));
		registerEquipment(new TinkerersEquipment(ItemsRegistry.IRON_KNIFE.get(), List.of("farmersdelight"), 1, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(ItemsRegistry.DIAMOND_KNIFE.get())));
		registerEquipment(new TinkerersEquipment(ItemsRegistry.FLINT_KNIFE.get(), List.of("farmersdelight"), 1, true, Ingredient.ofItems(Items.FLINT), equipment.get(ItemsRegistry.IRON_KNIFE.get())));
		registerEquipment(new TinkerersEquipment(ItemsRegistry.SKILLET.get(), List.of("farmersdelight"), 4, false, Ingredient.ofItems(Items.IRON_INGOT)));

		// Campanion
		registerEquipment(new TinkerersEquipment(CampanionItems.NETHERITE_SPEAR, List.of("campanion"), 1, false, Ingredient.ofItems(Items.NETHERITE_INGOT)));
		registerEquipment(new TinkerersEquipment(CampanionItems.DIAMOND_SPEAR, List.of("campanion"), 1, false, Ingredient.ofItems(Items.DIAMOND)));
		registerEquipment(new TinkerersEquipment(CampanionItems.IRON_SPEAR, List.of("campanion"), 1, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(CampanionItems.DIAMOND_SPEAR)));
		registerEquipment(new TinkerersEquipment(CampanionItems.STONE_SPEAR, List.of("campanion"), 1, true, Ingredient.ofTag(ItemTags.STONE_TOOL_MATERIALS), equipment.get(CampanionItems.IRON_SPEAR)));
		registerEquipment(new TinkerersEquipment(CampanionItems.GRAPPLING_HOOK, List.of("campanion"), 1, true, Ingredient.ofItems(CampanionItems.ROPE)));
		registerEquipment(new TinkerersEquipment(CampanionItems.SLEEPING_BAG, List.of("campanion"), 2, true, Ingredient.ofItems(CampanionItems.WOOL_TARP)));

		// Chalk
		registerEquipment(new TinkerersEquipment(Registry.ITEM.get(new Identifier("chalk", "chalk")), List.of("chalk"), 2, true, Ingredient.ofItems(Items.CALCITE)));
		registerEquipment(new TinkerersEquipment(Registry.ITEM.get(new Identifier("chalk", "glow_chalk")), List.of("chalk"), 1, true, Ingredient.ofItems(Items.GLOW_INK_SAC)));

		// Consistency+
//		registerEquipment(new TinkerersEquipment(Registry.ITEM.get(new Identifier(ConsistencyPlusMain.ID, "turtle_boots")), List.of("consistency_plus"), 4, false, Ingredient.ofItems(Items.SCUTE)));
//		registerEquipment(new TinkerersEquipment(Registry.ITEM.get(new Identifier(ConsistencyPlusMain.ID, "turtle_leggings")), List.of("consistency_plus"), 7, false, Ingredient.ofItems(Items.SCUTE)));
//		registerEquipment(new TinkerersEquipment(Registry.ITEM.get(new Identifier(ConsistencyPlusMain.ID, "turtle_chestplate")), List.of("consistency_plus"), 8, false, Ingredient.ofItems(Items.SCUTE)));

		// Yttr
		registerEquipment(new TinkerersEquipment(YItems.REINFORCED_CLEAVER, List.of("yttr"), 2, false, Ingredient.ofItems(YItems.GLASSY_VOID)));
		registerEquipment(new TinkerersEquipment(YItems.CLEAVER, List.of("yttr"), 2, false, Ingredient.ofItems(YItems.GLASSY_VOID)));
		// registerEquipment(new TinkerersEquipment(YItems.SHEARS, List.of("yttr"), 3, false, Ingredient.ofItems(YItems.YTTRIUM_INGOT)));
		registerEquipment(new TinkerersEquipment(YItems.BROOKITE_SHOVEL, List.of("yttr"), 1, false, Ingredient.ofItems(YItems.BROOKITE), equipment.get(Items.DIAMOND_SHOVEL)));
		registerEquipment(new TinkerersEquipment(YItems.BROOKITE_SWORD, List.of("yttr"), 2, false, Ingredient.ofItems(YItems.BROOKITE), equipment.get(Items.DIAMOND_SWORD)));
		registerEquipment(new TinkerersEquipment(YItems.BROOKITE_HOE, List.of("yttr"), 2, false, Ingredient.ofItems(YItems.BROOKITE), equipment.get(Items.DIAMOND_HOE)));
		registerEquipment(new TinkerersEquipment(YItems.BROOKITE_PICKAXE, List.of("yttr"), 3, false, Ingredient.ofItems(YItems.BROOKITE), equipment.get(Items.DIAMOND_PICKAXE)));
		registerEquipment(new TinkerersEquipment(YItems.BROOKITE_AXE, List.of("yttr"), 3, false, Ingredient.ofItems(YItems.BROOKITE), equipment.get(Items.DIAMOND_AXE)));
		registerEquipment(new TinkerersEquipment(YItems.SUIT_BOOTS, List.of("yttr"), 4, false, Ingredient.ofItems(YItems.ARMOR_PLATING)));
		registerEquipment(new TinkerersEquipment(YItems.SUIT_HELMET, List.of("yttr"), 5, false, Ingredient.ofItems(YItems.ARMOR_PLATING)));
		registerEquipment(new TinkerersEquipment(YItems.SUIT_LEGGINGS, List.of("yttr"), 7, false, Ingredient.ofItems(YItems.ARMOR_PLATING)));
		registerEquipment(new TinkerersEquipment(YItems.SUIT_CHESTPLATE, List.of("yttr"), 8, false, Ingredient.ofItems(YItems.ARMOR_PLATING)));
		registerEquipment(new TinkerersEquipment(YItems.SPATULA, List.of("yttr"), 12, false, Ingredient.ofItems(Items.IRON_INGOT)));
		registerEquipment(new TinkerersEquipment(YItems.CUPROSTEEL_COIL.get(), List.of("yttr"), 5, false, Ingredient.ofItems(YItems.CUPROSTEEL_INGOT)));

		// Botania
		registerEquipment(new TinkerersEquipment(BotaniaItems.spellCloth, List.of("botania"), 4, true, Ingredient.ofItems(BotaniaItems.manaweaveCloth)));
		registerEquipment(new TinkerersEquipment(BotaniaItems.enderDagger, List.of("botania"), 1, false, Ingredient.ofItems(BotaniaItems.manaSteel)));

		// Create
		registerEquipment(new TinkerersEquipment(AllItems.SUPER_GLUE.get(), List.of("create"), 2, true, Ingredient.ofItems(Items.SLIME_BALL)));
		registerEquipment(new TinkerersEquipment(AllItems.SAND_PAPER.get(), List.of("create"), 1, true, Ingredient.ofItems(Items.SAND)));
		registerEquipment(new TinkerersEquipment(AllItems.RED_SAND_PAPER.get(), List.of("create"), 1, true, Ingredient.ofItems(Items.RED_SAND)));
		registerEquipment(new TinkerersEquipment(AllItems.POTATO_CANNON.get(), List.of("create"), 3, false, Ingredient.ofItems(AllItems.ANDESITE_ALLOY.get())));
		registerEquipment(new TinkerersEquipment(AllItems.EXTENDO_GRIP.get(), List.of("create"), 5, false, Ingredient.ofItems(AllItems.BRASS_INGOT.get())));
		registerEquipment(new TinkerersEquipment(AllItems.COPPER_BACKTANK.get(), List.of("create"), 12, false, Ingredient.ofItems(Items.COPPER_INGOT)));
		registerEquipment(new TinkerersEquipment(AllItems.DIVING_BOOTS.get(), List.of("create"), 4, false, Ingredient.ofItems(Items.COPPER_INGOT)));
		registerEquipment(new TinkerersEquipment(AllItems.DIVING_HELMET.get(), List.of("create"), 5, false, Ingredient.ofItems(Items.COPPER_INGOT)));

		// Golden (maybe when this is data driven this could work via pseudo-tags - split files by material)
		Collection<TinkerersEquipment> netheriteEquipment = equipment.values().stream().filter(te -> te.repairMaterial().test(Items.NETHERITE_INGOT.getDefaultStack())).toList();
		registerEquipment(new TinkerersEquipment(Items.GOLDEN_SHOVEL, 1, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_SHOVEL));
		registerEquipment(new TinkerersEquipment(Items.GOLDEN_SWORD, 2, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_SWORD));
		registerEquipment(new TinkerersEquipment(Items.GOLDEN_HOE, 2, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_HOE));
		registerEquipment(new TinkerersEquipment(Items.GOLDEN_PICKAXE, 3, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_PICKAXE));
		registerEquipment(new TinkerersEquipment(Items.GOLDEN_AXE, 3, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_AXE));
		registerEquipment(new TinkerersEquipment(Items.GOLDEN_BOOTS, 4, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_BOOTS));
		registerEquipment(new TinkerersEquipment(Items.GOLDEN_HELMET, 5, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_HELMET));
		registerEquipment(new TinkerersEquipment(Items.GOLDEN_LEGGINGS, 7, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_LEGGINGS));
		registerEquipment(new TinkerersEquipment(Items.GOLDEN_CHESTPLATE, 8, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_CHESTPLATE));
		// Mods
		registerEquipment(new TinkerersEquipment(ItemsRegistry.GOLDEN_KNIFE.get(), List.of("farmersdelight"), 1, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, ItemsRegistry.NETHERITE_KNIFE.get()));
		registerEquipment(new TinkerersEquipment(CampanionItems.GOLDEN_SPEAR, List.of("campanion"), 1, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, CampanionItems.NETHERITE_SPEAR));
	}

	@Override
	protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
		equipment.values().forEach(te -> {
			te.generateDeWorkRecipe(exporter);
			if (te.useGrid()) {
				if (te.repairMaterial() != null) te.generateShapelessRepairRecipes(exporter);
				if (te.upgradeTo() != null) te.generateShapelessUpgradeRecipe(exporter);
			} else {
				if (te.upgradeTo() != null && te.unitCost() <= 5) te.generateUpgradeRecipe(exporter);
				if (te.repairMaterial() != null) te.generateRepairRecipe(exporter);
				if (te.sacrifices() != null) te.generateSacrificeRecipe(exporter);
			}
		});
	}

	public TinkerersSmithingRecipeGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}
}
