package folk.sisby.tinkerers_smithing.recipe;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class SacrificeUpgradeRecipe implements Recipe<Inventory> {
	private final Identifier identifier;

	public SacrificeUpgradeRecipe(Identifier identifier) {
		this.identifier = identifier;
	}

	public ItemStack getValidOutput(Inventory inventory) {
		ItemStack base = inventory.getStack(0);
		ItemStack ingredient = inventory.getStack(1);

		if (!base.isEmpty() && !ingredient.isEmpty()) {
			for (Map.Entry<Item, Map<Item, Integer>> sacrificePaths : TinkerersSmithing.getSacrificePaths(base.getItem()).entrySet()) {
				Item resultItem = sacrificePaths.getKey();
				Map<Item, Integer> sacrificeItems = sacrificePaths.getValue();

				for (Map.Entry<Item, Integer> entry : sacrificeItems.entrySet()) {
					Item sacrificeItem = entry.getKey();
					Integer sacrificeUnits = entry.getValue();

					if (ingredient.getItem() == sacrificeItem) {
						ItemStack resultStack = resultItem.getDefaultStack();
						resultStack.setNbt(base.getOrCreateNbt().copy());
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
	public ItemStack createIcon() {
		return new ItemStack(Blocks.SMITHING_TABLE);
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeType.SMITHING;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SACRIFICE_UPGRADE_SERIALIZER;
	}

	@Override
	public Identifier getId() {
		return identifier;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}
}
