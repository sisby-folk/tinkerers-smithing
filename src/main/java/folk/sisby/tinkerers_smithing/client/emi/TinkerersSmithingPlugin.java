package folk.sisby.tinkerers_smithing.client.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItemData;
import folk.sisby.tinkerers_smithing.client.TinkerersSmithingClient;
import folk.sisby.tinkerers_smithing.client.emi.recipe.EmiAnvilDeworkRecipe;
import net.minecraft.enchantment.Enchantments;

public class TinkerersSmithingPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		for (TinkerersSmithingItemData itemData : TinkerersSmithingClient.SERVER_SMITHING_ITEMS.values()) {
			if (Enchantments.VANISHING_CURSE.isAcceptableItem(itemData.item().getDefaultStack())) {
				registry.addRecipe(new EmiAnvilDeworkRecipe(itemData.item()));
			}
		}
	}
}
