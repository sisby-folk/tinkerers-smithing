package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.recipe.SmithingUpgradeRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
	@Shadow @Nullable private SmithingRecipe currentRecipe;
	@Unique private int ingredientsUsed = 0;

	public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Inject(method = "updateResult", at = @At(value = "TAIL"))
	public void cacheIngredientsUsed(CallbackInfo ci) {
		Pair<Integer, Integer> stacksAndCost = null;
		if (this.currentRecipe instanceof SmithingUpgradeRecipe sur) {
			stacksAndCost = sur.getUsedRepairStacksAndCost(this.output.getStack(1).getItem(), this.input.getStack(2));
		}
		ingredientsUsed = stacksAndCost == null ? 1 : stacksAndCost.getLeft();
	}

	@Inject(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/SmithingScreenHandler;decrementStack(I)V", ordinal = 0))
	public void specialDecrementIngredient(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
		this.input.getStack(2).decrement(ingredientsUsed - 1);
	}
}
