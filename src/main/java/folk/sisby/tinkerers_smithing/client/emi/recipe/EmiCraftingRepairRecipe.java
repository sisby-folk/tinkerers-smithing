package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.List;
import java.util.Random;

public class EmiCraftingRepairRecipe extends EmiPatternCraftingRecipe {
	private final Item tool;
	private final EmiIngredient repairMaterial;
	private final int cost;

	public EmiCraftingRepairRecipe(Item tool, List<Ingredient> ingredients, int cost) {
		super(List.of(EmiStack.of(tool), EmiIngredient.of(ingredients.stream().map(EmiIngredient::of).toList())), EmiStack.of(tool), null);
		this.tool = tool;
		this.repairMaterial = EmiIngredient.of(ingredients.stream().map(EmiIngredient::of).toList());
		this.cost = cost;
	}

	@Override
	public SlotWidget getInputWidget(int slot, int x, int y) {
		if (slot == 0) {
			return new IterativeSlotWidget((r, i) -> getTool(r, i,false), unique, x, y);
		} else if (slot <= cost) {
			return new IterativeSlotWidget((r, i) -> slot <= getStackCount(i) ? repairMaterial : EmiStack.EMPTY, unique, x, y);
		}
		return new SlotWidget(EmiStack.EMPTY, x, y);
	}

	@Override
	public SlotWidget getOutputWidget(int x, int y) {
		return new IterativeSlotWidget((r, i) -> getTool(r, i,true), unique, x, y);
	}

	private int getStackCount(long i) {
		return Math.floorMod(i, cost) + 1;
	}

	private int getStackDamage(Random r, int units) {
		return r.nextInt(((int) Math.ceil((tool.getMaxDamage() * (units - 1)) / (double) cost)), tool.getMaxDamage());
	}

	private EmiStack getTool(Random r, long i, boolean result) {
		ItemStack stack = tool.getDefaultStack();
		int units = getStackCount(i);
		int damage = getStackDamage(r, units);
		if (result) {
			damage = Math.max(0, damage - ((int) Math.ceil((tool.getMaxDamage() * (units)) / (double) cost)));
		}
		if (damage == 0) {
			return EmiStack.of(tool);
		}
		stack.setDamage(damage);
		return EmiStack.of(stack);
	}
}
