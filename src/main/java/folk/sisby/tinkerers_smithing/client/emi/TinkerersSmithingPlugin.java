package folk.sisby.tinkerers_smithing.client.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiAnvilRecipe;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingLoader;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiAnvilDeworkRecipe;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiSacrificeUpgradeRecipe;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiShapelessRepairRecipe;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiShapelessUpgradeRecipe;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiSmithingUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.SacrificeUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.SmithingUpgradeRecipe;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashSet;
import java.util.Set;

public class TinkerersSmithingPlugin implements EmiPlugin {
	private final Set<Identifier> replacedIds = new LinkedHashSet<>();
	private final Set<Identifier> replacedIdPrefixes = new LinkedHashSet<>();
	private final Set<EmiRecipe> addedRecipes = new LinkedHashSet<>();

	@Override
	public void register(EmiRegistry registry) {
		replacedIds.clear();
		replacedIdPrefixes.clear();
		addedRecipes.clear();

        registry.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream().filter(ShapelessUpgradeRecipe.class::isInstance).map(r -> new EmiShapelessUpgradeRecipe((ShapelessUpgradeRecipe) r)).forEach(this::replaceRecipe);
        registry.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream().filter(ShapelessRepairRecipe.class::isInstance).map(r -> new EmiShapelessRepairRecipe((ShapelessRepairRecipe) r)).forEach(this::replaceRecipe);
        registry.getRecipeManager().listAllOfType(RecipeType.SMITHING).stream().filter(SmithingUpgradeRecipe.class::isInstance).map(r -> new EmiSmithingUpgradeRecipe((SmithingUpgradeRecipe) r)).forEach(this::replaceRecipe);
        registry.getRecipeManager().listAllOfType(RecipeType.SMITHING).stream().filter(SacrificeUpgradeRecipe.class::isInstance).map(r -> new EmiSacrificeUpgradeRecipe((SacrificeUpgradeRecipe) r)).forEach(this::replaceRecipe);
        registry.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream().filter(recipe -> recipe instanceof ShapelessRepairRecipe).forEach(r -> replaceAnvilRecipe((ShapelessRepairRecipe) r));

		registry.removeRecipes(r -> replacedIdPrefixes.stream().anyMatch(id -> r.getId() != null && r.getId().toString().startsWith(id.toString())));
		registry.removeRecipes(r -> replacedIds.contains(r.getId()) && !addedRecipes.contains(r));
		addedRecipes.forEach(registry::addRecipe);

		for (Item item : Registry.ITEM) {
			if (Enchantments.VANISHING_CURSE.isAcceptableItem(item.getDefaultStack())) {
				registry.addRecipe(new EmiAnvilDeworkRecipe(EmiStack.of(item), EmiIngredient.of(TinkerersSmithing.DEWORK_INGREDIENTS), TinkerersSmithingLoader.recipeId("dework", item)));
			}
		}
	}

	private void replaceRecipe(EmiRecipe recipe) {
		replacedIds.add(recipe.getId());
		addedRecipes.add(recipe);
	}

	private void replaceAnvilRecipe(ShapelessRepairRecipe recipe) {
		replacedIdPrefixes.add(new Identifier("emi", "/" + "anvil/repairing/material" + "/" + Registry.ITEM.getId(recipe.baseItem).getNamespace() + "/" + Registry.ITEM.getId(recipe.baseItem).getPath()));
		addedRecipes.add(new EmiAnvilRecipe(EmiStack.of(recipe.baseItem), EmiIngredient.of(recipe.addition), new Identifier(recipe.getId().toString().replace(":repair/", ":anvil/"))));
	}
}
