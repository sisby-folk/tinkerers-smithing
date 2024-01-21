package folk.sisby.tinkerers_smithing;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class TinkerersSmithingNetworking {
	public static final Identifier S2C_SMITHING_RELOAD = new Identifier(TinkerersSmithing.ID, "s2c_smithing_reload");

	public static PacketByteBuf createSmithingReloadBuf() {
		PacketByteBuf buf = PacketByteBufs.create();
		Map<Item, TinkerersSmithingItemData> outMap = new HashMap<>();
		for (Item item : Registry.ITEM) {
			if (item instanceof TinkerersSmithingItem tsi && (!tsi.tinkerersSmithing$getUnitCosts().isEmpty() || !tsi.tinkerersSmithing$getUpgradePaths().isEmpty() || !tsi.tinkerersSmithing$getSacrificePaths().isEmpty())) {
				outMap.put(item, new TinkerersSmithingItemData(item, tsi.tinkerersSmithing$getUnitCosts(), tsi.tinkerersSmithing$getUpgradePaths(), tsi.tinkerersSmithing$getSacrificePaths()));
			}
		}
		outMap.forEach((item, tsiData) -> tsiData.write(buf));

		return buf;
	}

	public static void smithingReload(MinecraftServer server, PacketByteBuf buf) {
		server.getPlayerManager().getPlayerList().forEach(player -> {
			if (ServerPlayNetworking.canSend(player, S2C_SMITHING_RELOAD)) {
				ServerPlayNetworking.send(player, S2C_SMITHING_RELOAD, buf);
			}
		});
	}

	@SuppressWarnings("unused")
	public static void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		if (ServerPlayNetworking.canSend(handler, S2C_SMITHING_RELOAD) && TinkerersSmithing.SMITHING_RELOAD_BUF != null) {
			ServerPlayNetworking.send(handler.getPlayer(), S2C_SMITHING_RELOAD, TinkerersSmithing.SMITHING_RELOAD_BUF);
		}
	}
}
