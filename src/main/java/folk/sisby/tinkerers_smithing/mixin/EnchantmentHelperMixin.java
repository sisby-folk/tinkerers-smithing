package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	@Inject(method = "getLevel", at = @At("HEAD"), cancellable = true)
	private static void brokenNoEnchantments(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (!(enchantment instanceof MendingEnchantment) && TinkerersSmithing.isBroken(stack)) {
			cir.setReturnValue(0); // Breaks XP from grinding broken enchanted gear, but hopefully nothing else.
			cir.cancel();
		}
	}
}
