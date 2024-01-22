package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiShapelessRecipe;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class EmiCraftingRepairRecipe extends EmiShapelessRecipe {
	protected final int unique = EmiUtil.RANDOM.nextInt();

	public EmiCraftingRepairRecipe(ShapelessRepairRecipe recipe) {
		super(recipe);
	}

	private int getCost() {
		return getInputs().size() - 1;
	}

	@Override
	public boolean supportsRecipeTree() {
		return false;
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
			return new IterativeSlotWidget((r, i) -> getTool(r, i,false), unique, x, y);
		} else if (slot <= getCost()) {
			return new IterativeSlotWidget((r, i) -> slot <= getStackCount(i) ? getInputs().get(slot) : EmiStack.EMPTY, unique, x, y);
		}
		return new SlotWidget(EmiStack.EMPTY, x, y);
	}

	public SlotWidget getOutputWidget(int x, int y) {
		return new IterativeSlotWidget((r, i) -> getTool(r, i,true), unique, x, y);
	}

	private int getStackCount(long i) {
		return Math.floorMod(i, getCost()) + 1;
	}

	private int getStackDamage(Random r, int units) {
		ItemStack tool = getInputs().get(0).getEmiStacks().get(0).getItemStack();
		return r.nextInt(((int) Math.ceil((tool.getMaxDamage() * (units - 1)) / (double) getCost())), tool.getMaxDamage());
	}

	private EmiStack getTool(Random r, long i, boolean result) {
		ItemStack tool = getInputs().get(0).getEmiStacks().get(0).getItemStack();
		int units = getStackCount(i);
		int damage = getStackDamage(r, units);
		if (result) {
			damage = Math.max(0, damage - ((int) Math.ceil((tool.getMaxDamage() * (units)) / (double) getCost())));
		}
		if (damage == 0) {
			return EmiStack.of(tool.getItem());
		}
		tool.setDamage(damage);
		return EmiStack.of(tool);
	}
}
