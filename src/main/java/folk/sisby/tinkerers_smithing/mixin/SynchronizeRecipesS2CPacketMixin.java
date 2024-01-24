package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.recipe.ServerRecipe;
import folk.sisby.tinkerers_smithing.recipe.ServerRecipePacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(SynchronizeRecipesS2CPacket.class)
public class SynchronizeRecipesS2CPacketMixin implements ServerRecipePacket<SynchronizeRecipesS2CPacket> {
	@Unique private boolean tinkerersSmithing$fallback = false;

	@ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeCollection(Ljava/util/Collection;Lnet/minecraft/network/PacketByteBuf$PacketWriter;)V"), index = 0)
	private Collection<Recipe<?>> writeSafeRecipes(Collection<Recipe<?>> original) {
		if (tinkerersSmithing$fallback) {
			Collection<Recipe<?>> safeRecipes = new ArrayList<>(original);
			safeRecipes.removeIf(r -> r instanceof ServerRecipe<?> sr && sr.getFallbackSerializer() == null);
			return safeRecipes;
		}
		return original;
	}

	@ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeCollection(Ljava/util/Collection;Lnet/minecraft/network/PacketByteBuf$PacketWriter;)V"), index = 1)
	private PacketByteBuf.PacketWriter<Recipe<?>> write(PacketByteBuf.PacketWriter<Recipe<?>> original) {
		if (tinkerersSmithing$fallback) {
			return ServerRecipePacket::writeRecipeWithFallback;
		}
		return original;
	}

	@Override
	public SynchronizeRecipesS2CPacket tinkerersSmithing$withFallback() {
		tinkerersSmithing$fallback = true;
		return (SynchronizeRecipesS2CPacket) (Object) this;
	}
}
