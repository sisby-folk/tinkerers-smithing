package folk.sisby.tinkerers_smithing.client.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.recipe.EmiAnvilRecipe;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItemData;
import folk.sisby.tinkerers_smithing.client.TinkerersSmithingClient;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiAnvilDeworkRecipe;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiAnvilRepairRecipe;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.recipe.Ingredient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TinkerersSmithingPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		for (TinkerersSmithingItemData itemData : TinkerersSmithingClient.SERVER_SMITHING_ITEMS.values()) {
			if (Enchantments.VANISHING_CURSE.isAcceptableItem(itemData.item().getDefaultStack())) {
				registry.addRecipe(new EmiAnvilDeworkRecipe(itemData.item()));
			}
			if (!itemData.unitCosts().isEmpty()) {
				Map<Integer, List<Ingredient>> invertedCosts = itemData.unitCosts().entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
				invertedCosts.forEach((cost, ingredients) -> {
					registry.removeRecipes(r -> r instanceof EmiAnvilRecipe ear && ear.getOutputs().stream().allMatch(es -> es.getItemStack().isOf(itemData.item())));
					registry.addRecipe(new EmiAnvilRepairRecipe(itemData.item(), ingredients));
				});
			}
		}
	}
}
