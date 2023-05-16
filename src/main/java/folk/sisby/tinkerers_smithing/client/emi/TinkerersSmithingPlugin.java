package folk.sisby.tinkerers_smithing.client.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.recipe.EmiAnvilRecipe;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItemData;
import folk.sisby.tinkerers_smithing.client.TinkerersSmithingClient;
import folk.sisby.tinkerers_smithing.client.emi.recipe.*;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TinkerersSmithingPlugin implements EmiPlugin {
	private static Map<Integer, List<Ingredient>> invertCosts(Map<Ingredient, Integer> map) {
		return map.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
	}

	@Override
	public void register(EmiRegistry registry) {
		for (TinkerersSmithingItemData itemData : TinkerersSmithingClient.SERVER_SMITHING_ITEMS.values()) {
			if (Enchantments.VANISHING_CURSE.isAcceptableItem(itemData.item().getDefaultStack())) {
				registry.addRecipe(new EmiAnvilDeworkRecipe(itemData.item()));
			}
			if (!itemData.unitCosts().isEmpty()) {
				Map<Integer, List<Ingredient>> invertedCosts = invertCosts(itemData.unitCosts());
				invertedCosts.forEach((cost, ingredients) -> {
					registry.removeRecipes(r -> r instanceof EmiAnvilRecipe ear && ear.getOutputs().stream().allMatch(es -> es.getItemStack().isOf(itemData.item())));
					registry.addRecipe(new EmiAnvilRepairRecipe(itemData.item(), ingredients));
					registry.addRecipe(new EmiCraftingRepairRecipe(itemData.item(), ingredients, cost));
				});
			}
			itemData.upgradePaths().forEach(upgradeItem -> {
				TinkerersSmithingItemData upgradeData = TinkerersSmithingClient.SERVER_SMITHING_ITEMS.get(upgradeItem);
				if (upgradeData != null) {
					Map<Integer, List<Ingredient>> invertedCosts = invertCosts(upgradeData.unitCosts());
					invertedCosts.forEach((cost, ingredients) -> {
						registry.addRecipe(new EmiSmithingUpgradeRecipe(itemData.item(), ingredients, upgradeItem, cost));
						registry.addRecipe(new EmiCraftingUpgradeRecipe(itemData.item(), ingredients, upgradeItem, cost));
					});
				}
			});
			itemData.sacrificePaths().forEach((upgradeItem, sacrificeData) -> {
				int resultCost = sacrificeData.getLeft();
				Map<Item, Integer> sacrificeTools = sacrificeData.getRight();
				registry.addRecipe(new EmiSmithingSacrificeRecipe(itemData.item(), sacrificeTools, upgradeItem, resultCost));
			});
		}
	}
}
