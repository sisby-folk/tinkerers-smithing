package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.EmiUtil;
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
import java.util.Random;

public class EmiAnvilRepairRecipe implements EmiRecipe {
	private final Item tool;
	private final EmiIngredient repairMaterial;
	private final int uniq = EmiUtil.RANDOM.nextInt();

	public EmiAnvilRepairRecipe(Item tool, List<Ingredient> ingredients) {
		this.tool = tool;
		this.repairMaterial = EmiIngredient.of(ingredients.stream().map(EmiIngredient::of).toList());
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
		return List.of(EmiStack.of(tool), repairMaterial);
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
		widgets.add(new IterativeSlotWidget((r) -> getTool(r, false), uniq, 0, 0));
		widgets.addSlot(repairMaterial, 49, 0);
		widgets.add(new IterativeSlotWidget((r) -> getTool(r, true), uniq, 107, 0).recipeContext(this));
	}

	private EmiStack getTool(Random r, boolean repaired) {
		ItemStack stack = tool.getDefaultStack().copy();
		if (stack.getMaxDamage() <= 0) {
			return EmiStack.of(tool);
		}
		int d = r.nextInt(stack.getMaxDamage());
		if (repaired) {
			d -= stack.getMaxDamage() / 4;
			if (d <= 0) {
				return EmiStack.of(tool);
			}
		}
		stack.setDamage(d);
		return EmiStack.of(stack);
	}
}
