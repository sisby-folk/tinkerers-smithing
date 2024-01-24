package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiShapelessRecipe;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class EmiShapelessRepairRecipe extends EmiShapelessRecipe {
	public final Item baseItem;
	public final Ingredient addition;
	public final int additionCount;
	public final EmiStack resultPreview;

	public EmiShapelessRepairRecipe(ShapelessRepairRecipe recipe) {
		super(recipe);
		this.baseItem = recipe.baseItem;
		this.addition = recipe.addition;
		this.additionCount = recipe.additionCount;
		this.resultPreview = EmiStack.of(recipe.baseItem.getDefaultStack());
	}

	@Override
	public boolean supportsRecipeTree() {
		return false;
	}

	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
		widgets.addTexture(EmiTexture.SHAPELESS, 97, 0);
		for (int i = 0; i < 9; i++) {
			widgets.add(getInputWidget(i, i % 3 * 18, i / 3 * 18));
		}
		widgets.addSlot(resultPreview, 92, 14).large(true).recipeContext(this);
	}

	public SlotWidget getInputWidget(int slot, int x, int y) {
		if (slot == 0) {
			return new IterativeSlotWidget(this::getBase, x, y);
		} else if (slot < input.size()) {
			return new IterativeSlotWidget(i -> slot <= getStackCount(i) ? EmiIngredient.of(addition) : EmiStack.EMPTY, x, y);
		}
		return new SlotWidget(EmiStack.EMPTY, x, y);
	}

	private int getStackCount(long i) {
		return Math.floorMod(i, additionCount) + 1;
	}

	private EmiStack getBase(long i) {
		ItemStack tool = baseItem.getDefaultStack().copy();
		int units = getStackCount(i);
		int damage = (int) Math.ceil((baseItem.getMaxDamage() * units) / (double) additionCount);
		tool.setDamage(damage);
		return EmiStack.of(tool);
	}
}
