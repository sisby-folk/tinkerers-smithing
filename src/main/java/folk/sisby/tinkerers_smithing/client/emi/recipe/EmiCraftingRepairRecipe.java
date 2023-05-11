package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
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
			return new GeneratedSlotWidget(r -> getTool(r, false), unique, x, y);
		} else if (slot <= cost) {
			return new GeneratedSlotWidget(r -> {
				int d = r.nextInt(tool.getMaxDamage());
				int stackCount = getStackCount(r, d);
				return slot <= stackCount ? repairMaterial : EmiStack.EMPTY;
			}, unique, x, y);
		}
		return new SlotWidget(EmiStack.EMPTY, x, y);
	}

	@Override
	public SlotWidget getOutputWidget(int x, int y) {
		return new GeneratedSlotWidget(r -> getTool(r, true), unique, x, y);
	}

	private int getStackCount(Random r, int damage) {
		return r.nextInt(1, (damage * cost) / tool.getMaxDamage() + 2);
	}

	private EmiStack getTool(Random r, boolean result) {
		ItemStack stack = tool.getDefaultStack();
		int d = r.nextInt(tool.getMaxDamage());
		int stackCount = getStackCount(r, d);
		if (result) {
			d = Math.max(0, d - ((int) Math.ceil((tool.getMaxDamage() * (stackCount)) / (double) cost)));
		}
		if (d == 0) {
			return EmiStack.of(tool);
		}
		stack.setDamage(d);
		return EmiStack.of(stack);
	}
}
