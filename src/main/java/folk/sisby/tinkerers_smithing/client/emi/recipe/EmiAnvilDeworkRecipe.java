package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiAnvilRecipe;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class EmiAnvilDeworkRecipe extends EmiAnvilRecipe implements EmiRecipe {
	public EmiAnvilDeworkRecipe(EmiStack tool, EmiIngredient resource, Identifier id) {
		super(tool, resource, id);
	}

	@Override
	public boolean hideCraftable() {
		return true;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.PLUS, 27, 3);
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
		widgets.add(new IterativeSlotWidget(i -> getTool(i, false),0, 0).appendTooltip(ingredient -> new OrderedTextTooltipComponent(new LiteralText("Repair Cost: " + ingredient.getEmiStacks().get(0).getItemStack().getRepairCost()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText())));
		widgets.add(new IterativeSlotWidget(this::getRepairStack, 49, 0));
		widgets.add(new IterativeSlotWidget(i -> getTool(i, true), 107, 0).appendTooltip(ingredient -> new OrderedTextTooltipComponent(new LiteralText("Repair Cost: " + ingredient.getEmiStacks().get(0).getItemStack().getRepairCost()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText())).recipeContext(this));
	}

	private int getStackCount(long i) {
		return Math.floorMod(i, 5) + 1;
	}

	private EmiIngredient getTool(long i, boolean applied) {
		ItemStack stack = getOutputs().get(0).getItemStack().copy();
		int stackCount = getStackCount(i);
		int work = (int) Math.pow(2, 5 - (applied ? stackCount : 0)) - 1;
		if (work <= 0) {
			return EmiStack.of(stack);
		}
		stack.setRepairCost(work);
		return EmiStack.of(stack);
	}

	private EmiIngredient getRepairStack(long i) {
		return getInputs().get(1).copy().setAmount(getStackCount(i));
	}
}
