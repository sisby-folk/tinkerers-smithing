package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Ingredient.class)
public class IngredientMixin {
	@Shadow @Final public Ingredient.Entry[] entries;

	@Unique private boolean isFootgun() {
		if (Arrays.stream(entries).anyMatch(e -> e instanceof Ingredient.TagEntry) && Registry.ITEM.streamTagsAndEntries().toList().isEmpty()) {
			TinkerersSmithing.LOGGER.error("[Tinkerer's Smithing] Cowardly refusing to access an unloaded tag ingredient: {}", this, new IllegalStateException("A tag ingredient was accessed before tags are loaded - This usually breaks recipes! Please report this to the mod in the trace below."));
			return true;
		}
		return false;
	}

	@Inject(method = "getMatchingStacks", at = @At("HEAD"), cancellable = true)
	public void getMatchingStacks(CallbackInfoReturnable<ItemStack[]> cir) {
		if (isFootgun()) {
			cir.setReturnValue(new ItemStack[]{});
			cir.cancel();
		}
	}

	@Inject(method = "test(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
	public void test(CallbackInfoReturnable<Boolean> cir) {
		if (isFootgun()) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}

	@Inject(method = "getMatchingItemIds", at = @At("HEAD"), cancellable = true)
	public void getMatchingItemIds(CallbackInfoReturnable<IntList> cir) {
		if (isFootgun()) {
			cir.setReturnValue(new IntArrayList());
			cir.cancel();
		}
	}

	@Inject(method = "write", at = @At("HEAD"), cancellable = true)
	public void write(PacketByteBuf buf, CallbackInfo ci) {
		if (isFootgun()) {
			buf.writeVarInt(0);
			ci.cancel();
		}
	}
}
