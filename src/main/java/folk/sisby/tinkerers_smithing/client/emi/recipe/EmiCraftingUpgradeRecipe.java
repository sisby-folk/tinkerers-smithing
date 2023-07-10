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

public class EmiCraftingUpgradeRecipe extends EmiPatternCraftingRecipe {
	private final Item tool;
	private final EmiIngredient upgradeMaterial;
	private final Item resultTool;
	private final int cost;

	public EmiCraftingUpgradeRecipe(Item tool, List<Ingredient> ingredients, Item resultTool, int cost) {
		super(List.of(EmiStack.of(tool), EmiIngredient.of(ingredients.stream().map(EmiIngredient::of).toList())), EmiStack.of(resultTool), null);
		this.tool = tool;
		this.upgradeMaterial = EmiIngredient.of(ingredients.stream().map(EmiIngredient::of).toList());
		this.resultTool = resultTool;
		this.cost = cost;
	}

	@Override
	public SlotWidget getInputWidget(int slot, int x, int y) {
		if (slot == 0) {
			return new IterativeSlotWidget((r) -> EmiStack.of(getTool(r, false)), unique, x, y);
		} else if (slot <= cost) {
			return new SlotWidget(upgradeMaterial, x, y);
		}
		return new SlotWidget(EmiStack.EMPTY, x, y);
	}

	@Override
	public SlotWidget getOutputWidget(int x, int y) {
		return new IterativeSlotWidget((r) -> EmiStack.of(getTool(r, true)), unique, x, y);
	}

	private ItemStack getTool(Random r, boolean result) {
		ItemStack stack = result ? resultTool.getDefaultStack() : tool.getDefaultStack();
		if (stack.getMaxDamage() <= 0) {
			return stack;
		}
		int d = r.nextInt(tool.getMaxDamage());
		stack.setDamage(d);
		return stack;
	}
}
