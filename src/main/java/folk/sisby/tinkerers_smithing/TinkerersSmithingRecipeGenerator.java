package folk.sisby.tinkerers_smithing;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class TinkerersSmithingRecipeGenerator extends FabricRecipeProvider {
	public static final List<TinkerersEquipment> equipment = new ArrayList<>(List.of(
		new TinkerersEquipment(Items.NETHERITE_SHOVEL, 1, false, Ingredient.ofItems(Items.NETHERITE_INGOT)),
		new TinkerersEquipment(Items.NETHERITE_SWORD, 2, false, Ingredient.ofItems(Items.NETHERITE_INGOT))
	));

	static {
		// Add golden equipment (not sure how else to do this)
		Collection<TinkerersEquipment> netheriteEquipment = equipment.stream().filter(te -> te.repairMaterial().test(Items.NETHERITE_INGOT.getDefaultStack())).toList();
		equipment.addAll(List.of(
			new TinkerersEquipment(Items.GOLDEN_SHOVEL, 1, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_SHOVEL),
			new TinkerersEquipment(Items.GOLDEN_SWORD, 2, false, Ingredient.ofItems(Items.GOLD_INGOT), netheriteEquipment, Items.NETHERITE_SWORD)
		));
	}

	@Override
	protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
		equipment.forEach(te -> {
			if (te.useGrid()) {
				if (te.repairMaterial() != null) te.generateShapelessRepairRecipes(exporter);
				if (te.upgradeTo() != null) te.generateShapelessUpgradeRecipe(exporter);
			} else {
				if (te.upgradeTo() != null) te.generateUpgradeRecipe(exporter);
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
