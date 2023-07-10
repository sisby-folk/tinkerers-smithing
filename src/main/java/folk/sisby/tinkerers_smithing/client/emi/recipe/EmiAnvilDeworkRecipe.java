package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiAnvilDeworkRecipe implements EmiRecipe {
	private final Item tool;

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
		widgets.add(new IterativeSlotWidget(i -> getTool(i, false),0, 0).appendTooltip(ingredient -> new OrderedTextTooltipComponent(Text.literal("Repair Cost: " + ingredient.getEmiStacks().get(0).getItemStack().getRepairCost()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText())));
		widgets.add(new IterativeSlotWidget(this::getRepairStack, 49, 0));
		widgets.add(new IterativeSlotWidget(i -> getTool(i, true), 107, 0).appendTooltip(ingredient -> new OrderedTextTooltipComponent(Text.literal("Repair Cost: " + ingredient.getEmiStacks().get(0).getItemStack().getRepairCost()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText())).recipeContext(this));
	}

	private int getStackCount(long i) {
		return Math.floorMod(i, 5) + 1;
	}

	private EmiIngredient getTool(long i, boolean applied) {
		ItemStack stack = tool.getDefaultStack();
		int stackCount = getStackCount(i);
		int work = (int) Math.pow(2, 5 - (applied ? stackCount : 0)) - 1;
		if (work <= 0) {
			return EmiStack.of(tool);
		}
		stack.setRepairCost(work);
		return EmiStack.of(stack);
	}

	private EmiIngredient getRepairStack(long i) {
		return EmiIngredient.of(TinkerersSmithing.DEWORK_INGREDIENTS).copy().setAmount(getStackCount(i));
	}
}
