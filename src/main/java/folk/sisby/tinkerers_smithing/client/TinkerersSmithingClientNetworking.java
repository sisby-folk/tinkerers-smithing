package folk.sisby.tinkerers_smithing.client;

import folk.sisby.tinkerers_smithing.TinkerersSmithingItemData;
import folk.sisby.tinkerers_smithing.TinkerersSmithingNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class TinkerersSmithingClientNetworking {
	public static void initializeReceivers() {
		if (QuiltLoader.isModLoaded("emi")) {
			ClientPlayNetworking.registerGlobalReceiver(TinkerersSmithingNetworking.S2C_SMITHING_RELOAD, TinkerersSmithingClientNetworking::smithingReload);
		}
	}

	private static void smithingReload(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		TinkerersSmithingClient.SERVER_SMITHING_ITEMS.clear();
		while (buf.isReadable()) {
			TinkerersSmithingItemData itemData = TinkerersSmithingItemData.read(buf);
			TinkerersSmithingClient.SERVER_SMITHING_ITEMS.put(itemData.item(), itemData);
		}
		TinkerersSmithingClient.LOGGER.info("[Tinkerer's Smithing Client] Received {} smithing items.", TinkerersSmithingClient.SERVER_SMITHING_ITEMS.size());
	}
}
