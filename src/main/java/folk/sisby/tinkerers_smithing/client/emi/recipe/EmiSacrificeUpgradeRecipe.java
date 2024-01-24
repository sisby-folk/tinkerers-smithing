package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import folk.sisby.tinkerers_smithing.recipe.SacrificeUpgradeRecipe;

public class EmiSacrificeUpgradeRecipe extends EmiSmithingRecipe implements EmiRecipe {
	private final int additionUnits;
	private final int resultUnits;

	public EmiSacrificeUpgradeRecipe(SacrificeUpgradeRecipe recipe) {
		super(recipe);
		this.additionUnits = recipe.additionUnits;
		this.resultUnits = recipe.resultUnits;
	}
}
