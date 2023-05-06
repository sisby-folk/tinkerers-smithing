package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
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

	@Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 1), cancellable = true)
	private void applyDeworkMaterial(CallbackInfo ci) {
		ItemStack base = this.input.getStack(0);
		ItemStack ingredient = this.input.getStack(1);
		if (ingredient.isIn(TinkerersSmithing.DEWORK_INGREDIENTS) && base.getRepairCost() > 0) {
			ItemStack result = base.copy();
			this.repairItemUsage = 0;
			do  {
				result.setRepairCost(((result.getRepairCost() + 1)/2)-1);
				this.repairItemUsage++;
			} while (result.getRepairCost() > 0 && this.repairItemUsage < ingredient.getCount());
			this.output.setStack(0, result);
			this.levelCost.set(0);
			this.sendContentUpdates();
			ci.cancel();
		}
	}
}
