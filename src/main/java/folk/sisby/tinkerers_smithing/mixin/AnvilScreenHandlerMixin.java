package folk.sisby.tinkerers_smithing.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
	@Shadow private int repairItemUsage;
	@Final @Shadow private Property levelCost;
	@Shadow private String newItemName;

	public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getNextCost(I)I"))
	private int repairNoWork(int i) {
		return this.repairItemUsage > 0 ? i : AnvilScreenHandler.getNextCost(i);
	}

	@Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;sendContentUpdates()V"))
	private void repairNoLevels(CallbackInfo ci) {
		if (this.repairItemUsage > 0) {
			this.levelCost.set((StringUtils.isBlank(this.newItemName) && !this.input.getStack(0).hasCustomName()) || Objects.equals(this.input.getStack(0).getName().getString(), this.newItemName) ? 0 : 1);
		}
	}
}
