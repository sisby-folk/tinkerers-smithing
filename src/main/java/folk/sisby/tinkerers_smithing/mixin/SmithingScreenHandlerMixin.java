package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.recipe.SmithingUpgradeRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.LegacySmithingRecipe;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.LegacySmithingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LegacySmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
	@Shadow @Nullable private LegacySmithingRecipe field_41920;
	@Unique private int ingredientsUsed = 0;

	public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Inject(method = "updateResult", at = @At(value = "TAIL"))
	public void cacheIngredientsUsed(CallbackInfo ci) {
		int additionCount = -1;
		if (this.field_41920 instanceof SmithingUpgradeRecipe sur) {
			additionCount = sur.additionCount;
		}
		ingredientsUsed = additionCount == -1 ? 1 : Math.min(additionCount, this.input.getStack(1).getCount());
	}

	@Inject(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/LegacySmithingScreenHandler;method_48383(I)V", ordinal = 0))
	public void specialDecrementIngredient(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
		this.input.getStack(1).decrement(ingredientsUsed - 1);
	}
}
