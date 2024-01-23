package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiShapelessRecipe;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import folk.sisby.tinkerers_smithing.recipe.ShapelessUpgradeRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Random;

public class EmiShapelessUpgradeRecipe extends EmiShapelessRecipe {
	protected final int unique = EmiUtil.RANDOM.nextInt();

	public EmiShapelessUpgradeRecipe(ShapelessUpgradeRecipe recipe) {
		super(recipe);
	}

	@Override
	public Identifier getId() {
		return super.getId();
	}

	private int getCost() {
		return getInputs().size() - 1;
	}

	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
		if (shapeless) {
			widgets.addTexture(EmiTexture.SHAPELESS, 97, 0);
		}
		for (int i = 0; i < 9; i++) {
			widgets.add(getInputWidget(i, i % 3 * 18, i / 3 * 18));
		}
		widgets.add(getOutputWidget(92, 14).large(true).recipeContext(this));
	}

	public SlotWidget getInputWidget(int slot, int x, int y) {
		if (slot == 0) {
			return new IterativeSlotWidget((r) -> EmiStack.of(getTool(r, false)), unique, x, y);
		} else if (slot <= getCost()) {
			return new SlotWidget(getInputs().get(slot), x, y);
		}
		return new SlotWidget(EmiStack.EMPTY, x, y);
	}

	public SlotWidget getOutputWidget(int x, int y) {
		return new IterativeSlotWidget((r) -> EmiStack.of(getTool(r, true)), unique, x, y);
	}

	private ItemStack getTool(Random r, boolean result) {
		ItemStack tool = result ? getOutputs().get(0).getItemStack() : getInputs().get(0).getEmiStacks().get(0).getItemStack();
		if (tool.getMaxDamage() <= 0) {
			return tool;
		}
		int d = r.nextInt(tool.getMaxDamage());
		tool.setDamage(d);
		return tool;
	}
}
