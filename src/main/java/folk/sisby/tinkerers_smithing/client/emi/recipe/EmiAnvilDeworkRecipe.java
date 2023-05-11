package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class EmiAnvilDeworkRecipe implements EmiRecipe {
	private final Item tool;
	private final int uniq = EmiUtil.RANDOM.nextInt();

	public EmiAnvilDeworkRecipe(Item tool) {
		this.tool = tool;
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return VanillaEmiRecipeCategories.ANVIL_REPAIRING;
	}

	@Override
	public @Nullable Identifier getId() {
		return null;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(EmiStack.of(tool), EmiIngredient.of(TinkerersSmithing.DEWORK_INGREDIENTS));
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(EmiStack.of(tool));
	}

	@Override
	public boolean supportsRecipeTree() {
		return false;
	}

	@Override
	public int getDisplayWidth() {
		return 125;
	}

	@Override
	public int getDisplayHeight() {
		return 18;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.PLUS, 27, 3);
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
		widgets.addGeneratedSlot(r -> getTool(r, false), uniq, 0, 0).appendTooltip(ingredient -> new OrderedTextTooltipComponent(Text.literal("Repair Cost: " + ingredient.getEmiStacks().get(0).getItemStack().getRepairCost()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText()));
		widgets.addSlot(EmiIngredient.of(TinkerersSmithing.DEWORK_INGREDIENTS), 49, 0);
		widgets.addGeneratedSlot(r -> getTool(r, true), uniq, 107, 0).appendTooltip(ingredient -> new OrderedTextTooltipComponent(Text.literal("Repair Cost: " + ingredient.getEmiStacks().get(0).getItemStack().getRepairCost()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText())).recipeContext(this);
	}

	private int getWork(Random r, int modifyUses) {
		//return (int) Math.pow(2, r.nextInt(5) + 1 + modifyUses) - 1;
		return 15; // moves too fast.
	}

	private EmiIngredient getTool(Random r, boolean applied) {
		ItemStack stack = tool.getDefaultStack();
		int work = getWork(r, applied ? -1 : 0);
		if (work <= 0) {
			return EmiStack.of(tool);
		}
		stack.setRepairCost(work);
		return EmiStack.of(stack);
	}
}
