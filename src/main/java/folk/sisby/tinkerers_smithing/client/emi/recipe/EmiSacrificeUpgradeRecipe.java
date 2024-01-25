package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import folk.sisby.tinkerers_smithing.recipe.SacrificeUpgradeRecipe;
import net.minecraft.recipe.Ingredient;

public class EmiSacrificeUpgradeRecipe extends EmiSmithingRecipe implements EmiRecipe {
	private final int additionUnits;
	private final int resultUnits;

	public EmiSacrificeUpgradeRecipe(SacrificeUpgradeRecipe recipe) {
		super(EmiIngredient.of(Ingredient.empty()), EmiStack.of(recipe.baseItem), EmiIngredient.of(recipe.addition), EmiStack.of(recipe.result), recipe.getId());
		this.additionUnits = recipe.additionUnits;
		this.resultUnits = recipe.resultUnits;
	}
}
