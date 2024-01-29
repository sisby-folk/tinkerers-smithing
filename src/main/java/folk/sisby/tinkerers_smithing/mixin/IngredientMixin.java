package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ingredient.class)
public class IngredientMixin {
	@Unique private boolean isFootgun() {
		if (Registries.ITEM.streamTagsAndEntries().toList().isEmpty()) {
			TinkerersSmithing.LOGGER.error("[Tinkerer's Smithing] Cowardly refusing to access ingredient while tags are unloaded", new IllegalStateException("An ingredient was accessed before item tags are loaded - This would normally break all tag recipes! Please report this to the mod in the trace below."));
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
}
