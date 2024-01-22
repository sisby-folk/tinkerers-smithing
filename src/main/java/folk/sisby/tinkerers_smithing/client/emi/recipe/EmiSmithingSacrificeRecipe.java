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

public class EmiSmithingSacrificeRecipe extends EmiSmithingRecipe implements EmiRecipe {
	private final int additionUnits;
	private final int resultUnits;
	private final int uniq = EmiUtil.RANDOM.nextInt();

	public EmiSmithingSacrificeRecipe(SacrificeUpgradeRecipe recipe) {
		super(recipe);
		this.additionUnits = recipe.additionUnits;
		this.resultUnits = recipe.resultUnits;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.PLUS, 27, 3);
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
		widgets.addSlot(getInputs().get(0), 0, 0);
		widgets.add(new IterativeSlotWidget(this::getSacrificeStack, uniq, 49, 0));
		widgets.add(new IterativeSlotWidget(this::getTool, uniq, 107, 0).recipeContext(this));
	}

	private EmiStack getSacrificeStack(Random r, long i) {
		ItemStack sacrificeStack = getInputs().get(1).getEmiStacks().get(0).getItemStack();
		int sacrificeDamage = r.nextInt(sacrificeStack.getMaxDamage());
		if (sacrificeDamage > 0) {
			sacrificeStack.setDamage(sacrificeDamage);
		}
		return EmiStack.of(sacrificeStack);
	}

	private EmiStack getTool(Random r, long i) {
		ItemStack sacrificeStack = getInputs().get(1).getEmiStacks().get(0).getItemStack();
		int sacrificeDamage = r.nextInt(sacrificeStack.getMaxDamage());
		ItemStack result = getOutputs().get(0).getItemStack();
		int resultDamage = (int) Math.ceil(result.getMaxDamage() - ((sacrificeStack.getMaxDamage() - sacrificeDamage) * ((double) additionUnits * result.getMaxDamage()) / ((double)sacrificeStack.getMaxDamage() * this.resultUnits)));
		result.setDamage(resultDamage);
        return EmiStack.of(result);
	}
}
