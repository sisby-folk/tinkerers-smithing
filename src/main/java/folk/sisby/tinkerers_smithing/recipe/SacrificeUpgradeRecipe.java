package folk.sisby.tinkerers_smithing.recipe;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.TransformSmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.Map;

public class SacrificeUpgradeRecipe extends TransformSmithingRecipe implements ServerRecipe {
	public SacrificeUpgradeRecipe(Identifier identifier) {
		super(identifier, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
	}

	public ItemStack getValidOutput(Inventory inventory) {
		ItemStack base = inventory.getStack(1);
		ItemStack ingredient = inventory.getStack(2);

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
	public boolean matchesTemplateIngredient(ItemStack stack) {
		return false;
	}

	@Override
	public boolean matchesAdditionIngredient(ItemStack stack) {
		return true;
	}

	@Override
	public boolean matchesBaseIngredient(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack craft(Inventory craftingInventory, DynamicRegistryManager registryManager) {
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
	public ItemStack getResult(DynamicRegistryManager registryManager) {
		return ItemStack.EMPTY;
	}
}
