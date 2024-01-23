package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import folk.sisby.tinkerers_smithing.recipe.SmithingUpgradeRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EmiSmithingUpgradeRecipe extends EmiSmithingRecipe implements EmiRecipe {
	public final Item baseItem;
	public final int additionCount;
	public final Item resultItem;

	public EmiSmithingUpgradeRecipe(SmithingUpgradeRecipe recipe) {
		super(recipe);
		this.baseItem = recipe.baseItem;
		this.additionCount = recipe.additionCount;
		this.resultItem = recipe.resultItem;
	}

	@Override
	public boolean supportsRecipeTree() {
		return additionCount <= 5;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.PLUS, 27, 3);
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
		widgets.addSlot(EmiStack.of(baseItem), 0, 0);
		widgets.add(new IterativeSlotWidget(this::getRepairStack, 49, 0));
		widgets.add(new IterativeSlotWidget(this::getTool, 107, 0).recipeContext(this));
	}

	private int getStackCount(long i) {
		int minStack = Math.max(additionCount - 4, 1);
		return Math.floorMod(i, additionCount + 1 - minStack) + minStack;
	}

	private EmiStack getTool(long i) {
		ItemStack result = resultItem.getDefaultStack().copy();
		int usedCount = getStackCount(i);
		if (usedCount == additionCount) {
			return EmiStack.of(result.getItem());
		}
		result.setDamage(SmithingUpgradeRecipe.resultDamage(result.getItem(), additionCount, usedCount));
		return EmiStack.of(result);
	}

	private EmiIngredient getRepairStack(long i) {
		return getInputs().get(1).copy().setAmount(getStackCount(i));
	}
}
