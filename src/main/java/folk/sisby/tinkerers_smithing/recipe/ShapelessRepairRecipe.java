package folk.sisby.tinkerers_smithing.recipe;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShapelessRepairRecipe extends SpecialCraftingRecipe implements CraftingRecipe {
	public ShapelessRepairRecipe(Identifier identifier) {
		super(identifier);
	}

	public static @Nullable ItemStack getSingleEquipmentStack(CraftingInventory craftingInventory) {
		ItemStack equipmentStack = null;

		for(int i = 0; i < craftingInventory.size(); ++i) {
			ItemStack itemStack = craftingInventory.getStack(i);
			if (TinkerersSmithing.getRepairIngredient(itemStack.getItem()) != null) {
				if (equipmentStack == null) {
					equipmentStack = itemStack;
				} else {
					return null; // can't have multiple
				}
			}
		}
		return equipmentStack;
	}

	public static int getRepairMaterials(CraftingInventory craftingInventory, ItemStack equipment) {
		Ingredient repairIngredient = TinkerersSmithing.getRepairIngredient(equipment.getItem());
		int materials = 0;

		if (repairIngredient != null) {
			for(int i = 0; i < craftingInventory.size(); ++i) {
				ItemStack itemStack = craftingInventory.getStack(i);
				if (!itemStack.isOf(equipment.getItem())) {
					if (repairIngredient.test(itemStack)) {
						materials++;
					} else {
						return 0;
					}
				}
			}
		}
		return materials;
	}

	public boolean matches(CraftingInventory craftingInventory, World world) {
		ItemStack equipmentStack = getSingleEquipmentStack(craftingInventory);
		if (equipmentStack != null && !equipmentStack.hasEnchantments()) {
			int repairCount = getRepairMaterials(craftingInventory, equipmentStack);
			if (repairCount > 0 && equipmentStack.isDamageable() && equipmentStack.getItem() instanceof TinkerersSmithingItem tsi) {
				int unitCost = tsi.tinkerersSmithing$getUnitCost();
				if (unitCost != 0) {
					return equipmentStack.getDamage() - ((int) Math.ceil((equipmentStack.getMaxDamage() * (repairCount - 1)) / (double) unitCost)) > 0;
				}
			}
		}
		return false;
	}

	public ItemStack craft(CraftingInventory craftingInventory) {
		ItemStack equipmentStack = getSingleEquipmentStack(craftingInventory);
		if (equipmentStack != null && !equipmentStack.hasEnchantments()) {
			int repairCount = getRepairMaterials(craftingInventory, equipmentStack);
			if (repairCount > 0 && equipmentStack.isDamageable() && equipmentStack.getItem() instanceof TinkerersSmithingItem tsi) {
				int unitCost = tsi.tinkerersSmithing$getUnitCost();
				if (unitCost != 0) {
					ItemStack result = equipmentStack.copy();
					result.setDamage(Math.max(0, equipmentStack.getDamage() - ((int) Math.ceil((equipmentStack.getMaxDamage() * (repairCount)) / (double) unitCost))));
					return result;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SHAPELESS_REPAIR;
	}
}
