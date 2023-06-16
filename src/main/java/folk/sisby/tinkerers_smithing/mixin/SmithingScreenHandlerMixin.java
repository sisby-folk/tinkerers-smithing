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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
	@Shadow @Nullable private SmithingRecipe currentRecipe;

	@Shadow
	protected abstract void decrementStack(int slot);

	public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Redirect(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/SmithingScreenHandler;decrementStack(I)V"))
	public void specialDecrement(SmithingScreenHandler instance, int slot, PlayerEntity player, ItemStack resultStack) {
		if (slot == 2 && this.currentRecipe instanceof SmithingUpgradeRecipe sur) {
			ItemStack ingredientStack =  this.ingredientInventory.getStack(2);
			Pair<Integer, Integer> stacksAndCost = sur.getUsedRepairStacksAndCost(resultStack.getItem(), ingredientStack);
			if (stacksAndCost != null) {
				ingredientStack.decrement(stacksAndCost.getLeft());
				this.ingredientInventory.setStack(2, ingredientStack);
				return;
			}
		}
		this.decrementStack(slot);
	}
}
