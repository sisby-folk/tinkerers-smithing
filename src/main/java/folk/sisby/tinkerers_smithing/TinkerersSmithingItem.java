package folk.sisby.tinkerers_smithing;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Map;

public interface TinkerersSmithingItem {
	Map<Ingredient, Integer> tinkerersSmithing$getUnitCosts();

	int tinkerersSmithing$getUnitCost(ItemStack stack);
}
