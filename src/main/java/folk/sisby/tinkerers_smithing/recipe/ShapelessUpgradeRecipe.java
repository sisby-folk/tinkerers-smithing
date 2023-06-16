package folk.sisby.tinkerers_smithing.recipe;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingCategory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShapelessUpgradeRecipe extends SpecialCraftingRecipe {
	public ShapelessUpgradeRecipe(Identifier identifier, CraftingCategory craftingCategory) {
		super(identifier, craftingCategory);
	}

	public List<ItemStack> getInventoryStacks(RecipeInputInventory craftingInventory) {
		List<ItemStack> outList = new ArrayList<>();
		for(int i = 0; i < craftingInventory.size(); ++i) {
			ItemStack itemStack = craftingInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				outList.add(itemStack);
			}
		}
		return outList;
	}

	public ItemStack getValidOutput(RecipeInputInventory craftingInventory) {
		ItemStack equipmentStack = null;

		List<ItemStack> gridItems = getInventoryStacks(craftingInventory);

		for (ItemStack gridItem : gridItems) {
			if (gridItem.getItem() instanceof TinkerersSmithingItem tsi && !tsi.tinkerersSmithing$getUnitCosts().isEmpty()) { // fix - won't work for upgrade-only items
				if (equipmentStack == null) {
					equipmentStack = gridItem;
				} else {
					return null; // can't have multiple
				}
			}
		}

		if (equipmentStack == null || equipmentStack.hasEnchantments()) return null;
		gridItems.remove(equipmentStack);

		if (equipmentStack.getItem() instanceof TinkerersSmithingItem tsi) {
			for (Item upgradeItem : tsi.tinkerersSmithing$getUpgradePaths()) {
				if (upgradeItem instanceof TinkerersSmithingItem utsi) {
					for (Map.Entry<Ingredient, Integer> entry : utsi.tinkerersSmithing$getUnitCosts().entrySet()) {
						Ingredient ingredient = entry.getKey();
						Integer cost = entry.getValue();
						if (gridItems.stream().allMatch(ingredient) && gridItems.size() == cost) {
							ItemStack resultStack = upgradeItem.getDefaultStack();
							resultStack.setNbt(equipmentStack.getOrCreateNbt().copy());
							return resultStack;
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public boolean matches(RecipeInputInventory craftingInventory, World world) {
		return getValidOutput(craftingInventory) != null;
	}

	@Override
	public ItemStack craft(RecipeInputInventory craftingInventory, DynamicRegistryManager registryManager) {
		ItemStack result = getValidOutput(craftingInventory);
		return result != null ? result : ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SHAPELESS_UPGRADE_SERIALIZER;
	}
}
