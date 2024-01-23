package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import folk.sisby.tinkerers_smithing.recipe.SacrificeUpgradeRecipe;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class EmiSacrificeUpgradeRecipe extends EmiSmithingRecipe implements EmiRecipe {
	private final int additionUnits;
	private final int resultUnits;
	private final int uniq = EmiUtil.RANDOM.nextInt();

	public EmiSacrificeUpgradeRecipe(SacrificeUpgradeRecipe recipe) {
		super(recipe);
		this.additionUnits = recipe.additionUnits;
		this.resultUnits = recipe.resultUnits;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.PLUS, 27, 3);
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
		widgets.addSlot(getInputs().get(0), 0, 0);
		widgets.add(new IterativeSlotWidget(this::getAdditionStack, uniq, 49, 0));
		widgets.add(new IterativeSlotWidget(this::getTool, uniq, 107, 0).recipeContext(this));
	}

	private EmiStack getAdditionStack(Random r, long i) {
		ItemStack additionStack = getInputs().get(1).getEmiStacks().get(0).getItemStack();
		int sacrificeDamage = r.nextInt(additionStack.getMaxDamage());
		if (sacrificeDamage > 0) {
			additionStack.setDamage(sacrificeDamage);
		}
		return EmiStack.of(additionStack);
	}

	private EmiStack getTool(Random r, long i) {
		ItemStack additionStack = getInputs().get(1).getEmiStacks().get(0).getItemStack();
		int additionDamage = r.nextInt(additionStack.getMaxDamage());
		ItemStack result = getOutputs().get(0).getItemStack();
		result.setDamage(SacrificeUpgradeRecipe.resultDamage(result.getItem(), additionUnits, resultUnits, additionDamage, additionStack.getMaxDamage()));
        return EmiStack.of(result);
	}
}
