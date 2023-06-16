package folk.sisby.tinkerers_smithing.recipe;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRepairRecipe extends SpecialCraftingRecipe implements CraftingRecipe {
	public ShapelessRepairRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	public static @Nullable ItemStack getSingleEquipmentStack(CraftingInventory craftingInventory) {
		ItemStack equipmentStack = null;

		for(int i = 0; i < craftingInventory.size(); ++i) {
			ItemStack itemStack = craftingInventory.getStack(i);
			if (itemStack.getItem() instanceof TinkerersSmithingItem tsi && !tsi.tinkerersSmithing$getUnitCosts().isEmpty()) {
				if (equipmentStack == null) {
					equipmentStack = itemStack;
				} else {
					return null; // can't have multiple
				}
			}
		}
		return equipmentStack;
	}

	public static List<ItemStack> getRepairMaterials(CraftingInventory craftingInventory, ItemStack equipment) {
		List<ItemStack> outList = new ArrayList<>();

		if (equipment.getItem() instanceof TinkerersSmithingItem tsi) {
			for(int i = 0; i < craftingInventory.size(); ++i) {
				ItemStack itemStack = craftingInventory.getStack(i);
				if (!itemStack.isOf(equipment.getItem()) && !itemStack.isEmpty()) {
					if (tsi.tinkerersSmithing$getUnitCost(itemStack) > 0 && (outList.isEmpty() || outList.get(0).getItem() == itemStack.getItem())) {
						outList.add(itemStack);
					} else {
						return null;
					}
				}
			}
		}
		return outList;
	}

	public boolean matches(CraftingInventory craftingInventory, World world) {
		ItemStack equipmentStack = getSingleEquipmentStack(craftingInventory);
		if (equipmentStack != null && !equipmentStack.hasEnchantments()) {
			List<ItemStack> repairMaterials = getRepairMaterials(craftingInventory, equipmentStack);
			if (repairMaterials != null && !repairMaterials.isEmpty() && equipmentStack.isDamageable() && equipmentStack.getItem() instanceof TinkerersSmithingItem tsi) {
				int unitCost = tsi.tinkerersSmithing$getUnitCost(repairMaterials.get(0));
				if (unitCost != 0) {
					return equipmentStack.getDamage() - ((int) Math.ceil((equipmentStack.getMaxDamage() * (repairMaterials.size() - 1)) / (double) unitCost)) > 0;
				}
			}
		}
		return false;
	}

	public ItemStack craft(CraftingInventory craftingInventory) {
		ItemStack equipmentStack = getSingleEquipmentStack(craftingInventory);
		if (equipmentStack != null && !equipmentStack.hasEnchantments()) {
			List<ItemStack> repairMaterials = getRepairMaterials(craftingInventory, equipmentStack);
			if (repairMaterials != null && !repairMaterials.isEmpty() && equipmentStack.isDamageable() && equipmentStack.getItem() instanceof TinkerersSmithingItem tsi) {
				int unitCost = tsi.tinkerersSmithing$getUnitCost(repairMaterials.get(0));
				if (unitCost != 0) {
					ItemStack result = equipmentStack.copy();
					result.setDamage(Math.max(0, equipmentStack.getDamage() - ((int) Math.ceil((equipmentStack.getMaxDamage() * (repairMaterials.size())) / (double) unitCost))));
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
		return TinkerersSmithing.SHAPELESS_REPAIR_SERIALIZER;
	}
}
