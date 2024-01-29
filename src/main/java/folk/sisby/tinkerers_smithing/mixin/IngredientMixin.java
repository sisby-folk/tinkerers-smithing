package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Ingredient.class)
public class IngredientMixin {
	@Unique private boolean isFootgun() {
		Ingredient self = (Ingredient) (Object) this;
		if (Arrays.stream(self.entries).anyMatch(e -> e instanceof Ingredient.TagEntry) && (Registries.ITEM.getEntryList(ItemTags.PLANKS).isEmpty() || Registries.ITEM.getEntryList(ItemTags.PLANKS).get().size() == 0)) {
			TinkerersSmithing.LOGGER.error("[Tinkerer's Smithing] Cowardly refusing to access an unloaded tag ingredient: {}", self.toJson().toString(), new IllegalStateException("A tag ingredient was accessed before tags are loaded - This can break recipes! Report this to the mod in the trace below."));
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
