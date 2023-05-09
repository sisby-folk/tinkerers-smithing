package folk.sisby.tinkerers_smithing.recipe;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class SacrificeUpgradeRecipe extends SmithingRecipe implements Recipe<Inventory> {
	public SacrificeUpgradeRecipe(Identifier identifier) {
		super(identifier, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
	}

	public ItemStack getValidOutput(Inventory inventory) {
		ItemStack base = inventory.getStack(0);
		ItemStack ingredient = inventory.getStack(1);

		if (!base.isEmpty() && !ingredient.isEmpty() && base.isDamageable() && ingredient.isDamageable()) {
			for (Map.Entry<Item, Map<Item, Integer>> sacrificePaths : TinkerersSmithing.getSacrificePaths(base.getItem()).entrySet()) {
				Item resultItem = sacrificePaths.getKey();
				Map<Item, Integer> sacrificeItems = sacrificePaths.getValue();

				for (Map.Entry<Item, Integer> entry : sacrificeItems.entrySet()) {
					Item sacrificeItem = entry.getKey();
					Integer sacrificeUnits = entry.getValue();

					if (ingredient.getItem() == sacrificeItem) {
						ItemStack resultStack = resultItem.getDefaultStack();
						if (base.getNbt() != null) {
							resultStack.setNbt(base.getNbt().copy());
						}
						resultStack.setDamage((int) Math.floor(resultItem.getMaxDamage()-((double) ((sacrificeItem.getMaxDamage() - ingredient.getDamage()) * (sacrificeUnits * resultItem.getMaxDamage())) /(sacrificeItem.getMaxDamage() * sacrificeUnits))));
						return resultStack;
					}
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
		return TinkerersSmithing.SACRIFICE_UPGRADE_SERIALIZER;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}
}
