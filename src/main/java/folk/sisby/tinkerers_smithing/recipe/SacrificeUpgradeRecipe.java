package folk.sisby.tinkerers_smithing.recipe;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.Map;

public class SacrificeUpgradeRecipe extends SmithingRecipe {
	public SacrificeUpgradeRecipe(Identifier identifier) {
		super(identifier, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
	}

	public ItemStack getValidOutput(Inventory inventory) {
		ItemStack base = inventory.getStack(0);
		ItemStack ingredient = inventory.getStack(1);

		if (!base.isEmpty() && !ingredient.isEmpty() && base.isDamageable() && ingredient.isDamageable() && base.getItem() instanceof TinkerersSmithingItem tsi) {
			for (Map.Entry<Item, Pair<Integer, Map<Item, Integer>>> sacrificePaths : tsi.tinkerersSmithing$getSacrificePaths().entrySet()) {
				Item resultItem = sacrificePaths.getKey();
				Integer resultViaUnits = sacrificePaths.getValue().getLeft();
				Map<Item, Integer> sacrificeItems = sacrificePaths.getValue().getRight();

				for (Map.Entry<Item, Integer> entry : sacrificeItems.entrySet()) {
					Item sacrificeItem = entry.getKey();
					Integer sacrificeUnits = entry.getValue();

					if (ingredient.getItem() == sacrificeItem) {
						ItemStack resultStack = resultItem.getDefaultStack();
						if (base.getNbt() != null) {
							resultStack.setNbt(base.getNbt().copy());
						}
						int damage = (int) Math.ceil(resultItem.getMaxDamage() - ((sacrificeItem.getMaxDamage() - ingredient.getDamage()) * ((double) sacrificeUnits * resultItem.getMaxDamage()) / ((double)sacrificeItem.getMaxDamage() * resultViaUnits)));
						resultStack.setDamage(damage);
						return resultStack;
					}
				}

			}
		}
		return null;
	}

	@Override
	public boolean matches(Inventory craftingInventory, World world) {
		return getValidOutput(craftingInventory) != null;
	}

	@Override
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
		return TinkerersSmithing.SACRIFICE_UPGRADE_SERIALIZER;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}
}
