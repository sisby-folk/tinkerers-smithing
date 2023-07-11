package folk.sisby.tinkerers_smithing.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow public abstract boolean isDamageable();
	@Shadow public abstract int getDamage();
	@Shadow public abstract int getMaxDamage();
	@Shadow public abstract boolean hasCustomName();
	@Shadow public abstract boolean hasEnchantments();

	@Inject(method = "getMiningSpeedMultiplier", at = @At(value = "HEAD"), cancellable = true)
	private void brokenNoMiningSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) {
			cir.setReturnValue(1.0F);
			cir.cancel();
		}
	}

	@Inject(method = "use", at = @At(value = "HEAD"), cancellable = true)
	private void brokenDontUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) {
			cir.setReturnValue(TypedActionResult.fail((ItemStack) (Object) this));
			cir.cancel();
		}
	}

	@Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true)
	private void brokenDontUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) {
			cir.setReturnValue(ActionResult.FAIL);
			cir.cancel();
		}
	}

	@Inject(method = "useOnEntity", at = @At(value = "HEAD"), cancellable = true)
	private void brokenDontUseOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) {
			cir.setReturnValue(ActionResult.FAIL);
			cir.cancel();
		}
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
		if ((this.hasCustomName() || this.hasEnchantments()) && this.isDamageable() && this.getDamage() == this.getMaxDamage()) ci.cancel();
	}

	@Redirect(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
	private void dontBreakDecrementKeepers(ItemStack instance, int amount) {
		if (!instance.hasCustomName() && !instance.hasEnchantments()) {
			instance.decrement(1);
		}
	}

	@ModifyArg(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"))
	private int dontBreakResetKeepers(int damage) {
		return this.hasCustomName() || this.hasEnchantments() ? this.getMaxDamage() : damage;
	}

	@ModifyVariable(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/item/TooltipContext;shouldShowAdvancedDetails()Z", ordinal = 2), ordinal = 0)
	public List<Text> brokenShowTooltip(List<Text> list) {
		if (this.isDamageable() && this.getDamage() == this.getMaxDamage()) {
			list.add(Text.translatable("item.tinkerers_smithing.broken").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
		}
		return list;
	}
}
