package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiShapelessRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessUpgradeRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class EmiShapelessUpgradeRecipe extends EmiShapelessRecipe {
	public final Item baseItem;
	public final Ingredient addition;
	public final int additionCount;
	public final Item resultItem;
	public final EmiStack basePreview;
	public final EmiStack resultPreview;

	public EmiShapelessUpgradeRecipe(ShapelessUpgradeRecipe recipe) {
		super(recipe);
		this.baseItem = recipe.baseItem;
		this.addition = recipe.addition;
		this.additionCount = recipe.additionCount;
		this.resultItem = recipe.resultItem;
		ItemStack basePreview = recipe.baseItem.getDefaultStack().copy();
		basePreview.setDamage(1);
		this.basePreview = EmiStack.of(basePreview);
		ItemStack resultPreview = recipe.resultItem.getDefaultStack().copy();
		resultPreview.setDamage(1);
		this.resultPreview = EmiStack.of(resultPreview);
	}

	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
		widgets.addTexture(EmiTexture.SHAPELESS, 97, 0);
		for (int i = 0; i < 9; i++) {
			widgets.add(getInputWidget(i, i % 3 * 18, i / 3 * 18));
		}
		widgets.addSlot(resultPreview, 92, 14).output(true).recipeContext(this);
	}

	public SlotWidget getInputWidget(int slot, int x, int y) {
		if (slot == 0) {
			return new SlotWidget(basePreview, x, y);
		} else if (slot < input.size()) {
			return new SlotWidget(EmiIngredient.of(addition), x, y);
		}
		return new SlotWidget(EmiStack.EMPTY, x, y);
	}
}
