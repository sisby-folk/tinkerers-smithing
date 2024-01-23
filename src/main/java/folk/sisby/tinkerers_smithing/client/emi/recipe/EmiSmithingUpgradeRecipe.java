package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import folk.sisby.tinkerers_smithing.recipe.SmithingUpgradeRecipe;
import net.minecraft.item.ItemStack;

public class EmiSmithingUpgradeRecipe extends EmiSmithingRecipe implements EmiRecipe {
	private final int additionCount;

	public EmiSmithingUpgradeRecipe(SmithingUpgradeRecipe recipe) {
		super(recipe);
		this.additionCount = recipe.additionCount;
	}

	@Override
	public boolean supportsRecipeTree() {
		return additionCount <= 5;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.PLUS, 27, 3);
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
		widgets.addSlot(getInputs().get(0), 0, 0);
		widgets.add(new IterativeSlotWidget(this::getRepairStack, 49, 0));
		widgets.add(new IterativeSlotWidget(this::getTool, 107, 0).recipeContext(this));
	}

	private int getStackCount(long i) {
		int minStack = Math.max(additionCount - 4, 1);
		return Math.floorMod(i, additionCount + 1 - minStack) + minStack;
	}

	private EmiStack getTool(long i) {
		ItemStack stack = getOutputs().get(0).getItemStack();
		int usedCount = getStackCount(i);
		if (usedCount == additionCount) {
			return EmiStack.of(stack.getItem());
		}
		stack.setDamage(SmithingUpgradeRecipe.resultDamage(stack.getItem(), additionCount, usedCount));
		return EmiStack.of(stack);
	}

	private EmiIngredient getRepairStack(long i) {
		return getInputs().get(1).copy().setAmount(getStackCount(i));
	}
}
