package folk.sisby.tinkerers_smithing.client;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinkerersSmithingClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("tinkerers_smithing_client");

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(TinkerersSmithing.S2C_PING, (client, handler, buf, sender) -> {});
	}
}
