package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiAnvilRecipe;
import net.minecraft.util.Identifier;

public class EmiIdentifiedAnvilRecipe extends EmiAnvilRecipe implements EmiRecipe {
	private final Identifier id;

	public EmiIdentifiedAnvilRecipe(EmiStack tool, EmiIngredient resource, Identifier id) {
		super(tool, resource);
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return id;
	}
}
