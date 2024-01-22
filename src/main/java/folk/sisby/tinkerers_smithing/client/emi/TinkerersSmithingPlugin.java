package folk.sisby.tinkerers_smithing.client.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiAnvilRecipe;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiAnvilDeworkRecipe;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiCraftingRepairRecipe;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiCraftingUpgradeRecipe;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiSmithingSacrificeRecipe;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiSmithingUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.SacrificeUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.SmithingUpgradeRecipe;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TinkerersSmithingPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		for (CraftingRecipe recipe : registry.getRecipeManager().listAllOfType(RecipeType.CRAFTING)) {
			if (recipe instanceof ShapelessUpgradeRecipe sur) {
//				registry.removeRecipes(recipe.getId());
				registry.addRecipe(new EmiCraftingUpgradeRecipe(sur));
			}
			if (recipe instanceof ShapelessRepairRecipe srr) {
//				registry.removeRecipes(recipe.getId());
				registry.addRecipe(new EmiCraftingRepairRecipe(srr));

//				registry.removeRecipes(r -> r instanceof EmiAnvilRecipe ear && ear.getOutputs().stream().allMatch(es -> srr.getBaseIngredient().test(es.getItemStack())));
				registry.addRecipe(new EmiAnvilRecipe(EmiStack.of(srr.getBaseIngredient().getMatchingStacks()[0]), EmiIngredient.of(srr.getUnit()), srr.getId()));
			}
		}
		for (SmithingRecipe recipe : registry.getRecipeManager().listAllOfType(RecipeType.SMITHING)) {
			if (recipe instanceof SmithingUpgradeRecipe sur) {
//				registry.removeRecipes(recipe.getId());
				registry.addRecipe(new EmiSmithingUpgradeRecipe(sur));
			}
			if (recipe instanceof SacrificeUpgradeRecipe sur) {
//				registry.removeRecipes(recipe.getId());
				registry.addRecipe(new EmiSmithingSacrificeRecipe(sur));
			}
		}
		for (Item item : Registry.ITEM) {
			if (Enchantments.VANISHING_CURSE.isAcceptableItem(item.getDefaultStack())) {
				registry.addRecipe(new EmiAnvilDeworkRecipe(item, new Identifier(TinkerersSmithing.ID, "dework/" + Registry.ITEM.getId(item).toString().replace(":", "_"))));
			}
		}
	}
}
