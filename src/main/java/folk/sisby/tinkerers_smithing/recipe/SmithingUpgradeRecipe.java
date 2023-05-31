package folk.sisby.tinkerers_smithing.recipe;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.Map;

public class SmithingUpgradeRecipe extends SmithingRecipe implements Recipe<Inventory> {
	public SmithingUpgradeRecipe(Identifier identifier) {
		super(identifier, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
	}

	public Pair<Integer, Integer> getUsedRepairStacksAndCost(Item result, ItemStack ingredient) {
		if (result instanceof TinkerersSmithingItem tsi) {
			for (Map.Entry<Ingredient, Integer> entry : tsi.tinkerersSmithing$getUnitCosts().entrySet()) {
				Ingredient upgradeIngredient = entry.getKey();
				Integer cost = entry.getValue();
				if (upgradeIngredient.test(ingredient) && ingredient.getCount() >= cost - 4) {
					return new Pair<>(Math.min(ingredient.getCount(), cost), cost);
				}
			}
		}
		return null;
	}

	public ItemStack getValidOutput(Inventory inventory) {
		ItemStack base = inventory.getStack(0);
		ItemStack ingredient = inventory.getStack(1);

		if (!base.isEmpty() && !ingredient.isEmpty() && base.getItem() instanceof TinkerersSmithingItem tsi) {
			for (Item upgradeItem : tsi.tinkerersSmithing$getUpgradePaths()) {
				Pair<Integer, Integer> usedAndCost = getUsedRepairStacksAndCost(upgradeItem, ingredient);
				if (usedAndCost != null) {
					ItemStack resultStack = upgradeItem.getDefaultStack();
					resultStack.setNbt(base.getOrCreateNbt().copy());
					resultStack.setDamage(Math.min(upgradeItem.getMaxDamage() - 1, (int) Math.floor(upgradeItem.getMaxDamage() * ((usedAndCost.getRight() - usedAndCost.getLeft()) / 4.0))));
					return resultStack;
				}
			}
		}

		return null;
	}

	public boolean matches(Inventory craftingInventory, World world) {
		return getValidOutput(craftingInventory) != null;
	}

	public ItemStack craft(Inventory craftingInventory) {
		ItemStack result = getValidOutput(craftingInventory);
		return result != null ? result : ItemStack.EMPTY;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SMITHING_UPGRADE_SERIALIZER;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}
}
