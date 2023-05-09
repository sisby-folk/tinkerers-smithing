package folk.sisby.tinkerers_smithing.recipe;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class SmithingUpgradeRecipe implements Recipe<Inventory> {
	private final Identifier identifier;

	public SmithingUpgradeRecipe(Identifier identifier) {
		this.identifier = identifier;
	}

	public ItemStack getValidOutput(Inventory inventory) {
		ItemStack base = inventory.getStack(0);
		ItemStack ingredient = inventory.getStack(1);

		if (!base.isEmpty() && !ingredient.isEmpty()) {
			for (Item upgradeItem : TinkerersSmithing.getUpgradePaths(base.getItem())) {
				if (upgradeItem instanceof TinkerersSmithingItem tsi) {
					for (Map.Entry<Ingredient, Integer> entry : tsi.tinkerersSmithing$getUnitCosts().entrySet()) {
						Ingredient upgradeIngredient = entry.getKey();
						Integer cost = entry.getValue();
						if (upgradeIngredient.test(ingredient) && cost <= 5) {
							ItemStack resultStack = upgradeItem.getDefaultStack();
							resultStack.setNbt(base.getOrCreateNbt().copy());
							resultStack.setDamage((int) Math.floor(upgradeItem.getMaxDamage() * ((cost - 1) / 4.0)));
							return resultStack;
						}
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
		return TinkerersSmithing.SMITHING_UPGRADE_SERIALIZER;
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
