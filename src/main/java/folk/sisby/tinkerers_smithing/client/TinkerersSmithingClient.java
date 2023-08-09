package folk.sisby.tinkerers_smithing.client;

import folk.sisby.tinkerers_smithing.TinkerersSmithingItemData;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class TinkerersSmithingClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("tinkerers_smithing_client");

	public static final Map<Item, TinkerersSmithingItemData> SERVER_SMITHING_ITEMS = new HashMap<>();

	@Override
	public void onInitializeClient() {
		TinkerersSmithingClientNetworking.initializeReceivers();
	}
}
