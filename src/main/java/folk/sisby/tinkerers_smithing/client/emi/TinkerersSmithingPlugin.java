package folk.sisby.tinkerers_smithing.client.emi;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
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
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class TinkerersSmithingPlugin implements EmiPlugin {
	private final Set<Identifier> replacedIds = new LinkedHashSet<>();
	private final Set<Identifier> replacedIdPrefixes = new LinkedHashSet<>();
	private final Set<EmiRecipe> addedRecipes = new LinkedHashSet<>();

	@Override
	public void register(EmiRegistry registry) {
		VanillaEmiRecipeCategories.SMITHING.sorter = EmiRecipeSorting.identifier(); // Be a huge bitch bastard

		replacedIds.clear();
		replacedIdPrefixes.clear();
		addedRecipes.clear();

        registry.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream().filter(ShapelessUpgradeRecipe.class::isInstance).map(r -> new EmiShapelessUpgradeRecipe((ShapelessUpgradeRecipe) r)).forEach(this::replaceRecipe);
        registry.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream().filter(ShapelessRepairRecipe.class::isInstance).map(r -> new EmiShapelessRepairRecipe((ShapelessRepairRecipe) r)).forEach(this::replaceRecipe);
        registry.getRecipeManager().listAllOfType(RecipeType.SMITHING).stream().filter(SmithingUpgradeRecipe.class::isInstance).map(r -> new EmiSmithingUpgradeRecipe((SmithingUpgradeRecipe) r)).forEach(this::replaceRecipe);
        registry.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream().filter(recipe -> recipe instanceof ShapelessRepairRecipe).forEach(r -> replaceAnvilRecipe((ShapelessRepairRecipe) r));
		Multimap<ItemPair, SacrificeUpgradeRecipe> cappedRecipes = HashMultimap.create();
		for (SmithingRecipe recipe : registry.getRecipeManager().listAllOfType(RecipeType.SMITHING)) {
			if (recipe instanceof SacrificeUpgradeRecipe sur) {
				if (sur.additionUnits >= sur.resultUnits) {
					replacedIds.add(sur.getId());
					cappedRecipes.put(new ItemPair(sur.baseItem, sur.resultItem), sur);
					continue;
				}
				replaceRecipe(new EmiSacrificeUpgradeRecipe(sur));
			}
		}
		for (ItemPair key : cappedRecipes.keySet()) {
			SacrificeUpgradeRecipe sample = cappedRecipes.get(key).stream().findFirst().get();
			addedRecipes.add(new EmiSacrificeUpgradeRecipe(new SacrificeUpgradeRecipe(
				sample.baseItem,
				cappedRecipes.get(key).stream().map(s -> s.addition).reduce(Ingredient.empty(), (i, i2) -> Ingredient.ofEntries(Arrays.stream(ArrayUtils.addAll(i.entries, i2.entries)))),
				sample.resultUnits,
				sample.resultItem,
				sample.resultUnits
			)));
		}

		registry.removeRecipes(r -> replacedIdPrefixes.stream().anyMatch(id -> r.getId() != null && r.getId().toString().startsWith(id.toString())));
		registry.removeRecipes(r -> replacedIds.contains(r.getId()) && !addedRecipes.contains(r));
		addedRecipes.forEach(registry::addRecipe);

		for (Item item : Registries.ITEM) {
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
		replacedIdPrefixes.add(new Identifier("emi", "/" + "anvil/repairing/material" + "/" + Registries.ITEM.getId(recipe.baseItem).getNamespace() + "/" + Registries.ITEM.getId(recipe.baseItem).getPath()));
		addedRecipes.add(new EmiAnvilRecipe(EmiStack.of(recipe.baseItem), EmiIngredient.of(recipe.addition), new Identifier(recipe.getId().toString().replace(":repair/", ":anvil/"))));
	}

	record ItemPair(Item i1, Item i2) {
	}
}
