package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.recipe.ServerRecipePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@ModifyArg(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V"))
	private Packet<?> skipServerRecipeSync(Packet<?> packet) {
		if (packet instanceof SynchronizeRecipesS2CPacket srp && !ServerPlayNetworking.canSend((ServerPlayNetworkHandler) (Object) this, TinkerersSmithing.S2C_PING)) {
			return ((ServerRecipePacket<?>) new SynchronizeRecipesS2CPacket(srp.getRecipes())).tinkerersSmithing$withFallback();
		}
		return packet;
	}
}
