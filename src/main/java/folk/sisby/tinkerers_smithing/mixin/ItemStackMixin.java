package folk.sisby.tinkerers_smithing.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.component.TranslatableComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow public abstract boolean isDamageable();
	@Shadow public abstract int getDamage();
	@Shadow public abstract int getMaxDamage();

	@Inject(method = "getMiningSpeedMultiplier", at = @At(value = "HEAD"), cancellable = true)
	private void brokenNoMiningSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) {
			cir.setReturnValue(1.0F);
			cir.cancel();
		}
	}

	@Inject(method = "use", at = @At(value = "HEAD"))
	private void brokenDontUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) cir.cancel();
	}

	@Inject(method = "useOnBlock", at = @At(value = "HEAD"))
	private void brokenDontUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) cir.cancel();
	}

	@Inject(method = "useOnEntity", at = @At(value = "HEAD"))
	private void brokenDontUseOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) cir.cancel();
	}

	@Inject(method = "isSuitableFor", at = @At(value = "HEAD"), cancellable = true)
	private void brokenIsNotSuitable(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}

	@Inject(method = "getAttributeModifiers", at = @At(value = "HEAD"), cancellable = true)
	private void brokenHasNoAttributes(EquipmentSlot slot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) {
			cir.setReturnValue(HashMultimap.create());
			cir.cancel();
		}
	}

	@Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "HEAD"), cancellable = true)
	private <T extends LivingEntity> void brokenNoDamage(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) ci.cancel();
	}

	@Redirect(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
	private void brokenDontDecrement(ItemStack instance, int amount) {
	}

	@ModifyArg(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"))
	private int brokenDontReset(int damage) {
		return this.getMaxDamage();
	}

	@Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/item/TooltipContext;isAdvanced()Z", ordinal = 2), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void brokenShowTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> ci, List<Text> list) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) {
			try {
				list.add(Text.translatable("item.tinkerers_smithing.broken").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
			} catch (NoSuchMethodError e) {
				list.add((Text) new TranslatableComponent("item.tinkerers_smithing.broken", this.getMaxDamage() - this.getDamage()));
			}
		}
	}
}
