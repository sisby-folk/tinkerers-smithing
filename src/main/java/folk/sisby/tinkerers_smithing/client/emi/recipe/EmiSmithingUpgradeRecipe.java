package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiSmithingUpgradeRecipe implements EmiRecipe {
	private final Item tool;
	private final EmiIngredient upgradeMaterial;
	private final Item resultTool;
	private final int cost;

	public EmiSmithingUpgradeRecipe(Item tool, List<Ingredient> ingredients, Item resultTool, int cost) {
		this.tool = tool;
		this.upgradeMaterial =  EmiIngredient.of(ingredients.stream().map(EmiIngredient::of).toList());
		this.resultTool = resultTool;
		this.cost = cost;
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return VanillaEmiRecipeCategories.SMITHING;
	}

	@Override
	public @Nullable Identifier getId() {
		return null;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(EmiStack.of(tool), upgradeMaterial);
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(EmiStack.of(resultTool));
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
		widgets.addSlot(EmiStack.of(tool), 0, 0);
		widgets.add(new IterativeSlotWidget(this::getRepairStack, 49, 0));
		widgets.add(new IterativeSlotWidget(this::getTool, 107, 0).recipeContext(this));
	}

	private int getStackCount(long i) {
		int minStack = Math.max(cost - 4, 1);
		return Math.floorMod(i, cost + 1 - minStack) + minStack;
	}

	private EmiStack getTool(long i) {
		ItemStack stack = resultTool.getDefaultStack();
		int stackCount = getStackCount(i);
		if (stackCount == cost) {
			return EmiStack.of(resultTool);
		}
		stack.setDamage((int) Math.floor(resultTool.getMaxDamage() * ((cost - stackCount) / 4.0)));
		return EmiStack.of(stack);
	}

	private EmiIngredient getRepairStack(long i) {
		return upgradeMaterial.copy().setAmount(getStackCount(i));
	}
}
