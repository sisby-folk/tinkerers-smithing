package folk.sisby.tinkerers_smithing;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;

import java.util.*;
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
//		registerEquipment(new TinkerersEquipment(Items.CHAINMAIL_BOOTS, 4, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_BOOTS)));
//		registerEquipment(new TinkerersEquipment(Items.CHAINMAIL_HELMET, 5, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_HELMET)));
//		registerEquipment(new TinkerersEquipment(Items.CHAINMAIL_LEGGINGS, 7, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_LEGGINGS)));
//		registerEquipment(new TinkerersEquipment(Items.CHAINMAIL_CHESTPLATE, 8, false, Ingredient.ofItems(Items.IRON_INGOT), equipment.get(Items.DIAMOND_CHESTPLATE)));

		// Leather
//		registerEquipment(new TinkerersEquipment(Items.LEATHER_BOOTS, 4, true, Ingredient.ofItems(Items.LEATHER), equipment.get(Items.IRON_BOOTS)));
//		registerEquipment(new TinkerersEquipment(Items.LEATHER_HELMET, 5, true, Ingredient.ofItems(Items.LEATHER), equipment.get(Items.IRON_HELMET)));
//		registerEquipment(new TinkerersEquipment(Items.LEATHER_LEGGINGS, 7, true, Ingredient.ofItems(Items.LEATHER), equipment.get(Items.IRON_LEGGINGS)));
//		registerEquipment(new TinkerersEquipment(Items.LEATHER_CHESTPLATE, 8, true, Ingredient.ofItems(Items.LEATHER), equipment.get(Items.IRON_CHESTPLATE)));

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
	}

	@Override
	protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
		equipment.values().forEach(te -> {
			if (te.useGrid()) {
				if (te.repairMaterial() != null) te.generateShapelessRepairRecipes(exporter);
				if (te.upgradeTo() != null) te.generateShapelessUpgradeRecipe(exporter);
			} else {
				if (te.upgradeTo() != null && te.unitCost() <= 5) te.generateUpgradeRecipe(exporter);
				if (te.repairMaterial() != null) te.generateRepairRecipe(exporter);
				te.generateDeWorkRecipe(exporter);
				if (te.sacrifices() != null) te.generateSacrificeRecipe(exporter);
			}
		});
	}

	public TinkerersSmithingRecipeGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}
}
