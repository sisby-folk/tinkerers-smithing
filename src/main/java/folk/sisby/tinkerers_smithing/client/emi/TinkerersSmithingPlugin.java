package folk.sisby.tinkerers_smithing.client.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiAnvilRecipe;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItemData;
import folk.sisby.tinkerers_smithing.client.TinkerersSmithingClient;
import net.minecraft.item.ItemStack;

public class TinkerersSmithingPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		for (TinkerersSmithingItemData itemData : TinkerersSmithingClient.SERVER_SMITHING_ITEMS.values()) {
			ItemStack deworkBase = itemData.item().getDefaultStack();
			deworkBase.setRepairCost(15);
			registry.addRecipe(new EmiAnvilRecipe(EmiStack.of(deworkBase), EmiIngredient.of(TinkerersSmithing.DEWORK_INGREDIENTS)));
		}
	}
}
