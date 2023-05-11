package folk.sisby.tinkerers_smithing;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Pair;

import java.util.Map;
import java.util.Set;

public interface TinkerersSmithingItem {
	Map<Ingredient, Integer> tinkerersSmithing$getUnitCosts();

	Set<Item> tinkerersSmithing$getUpgradePaths();

	Map<Item, Pair<Integer, Map<Item, Integer>>> tinkerersSmithing$getSacrificePaths();

	int tinkerersSmithing$getUnitCost(ItemStack stack);
}
