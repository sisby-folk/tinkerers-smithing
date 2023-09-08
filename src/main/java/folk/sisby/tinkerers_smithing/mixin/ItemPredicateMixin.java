package folk.sisby.tinkerers_smithing.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(ItemPredicate.class)
public class ItemPredicateMixin {
	@ModifyVariable(method = "test", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;fromNbt(Lnet/minecraft/nbt/NbtList;)Ljava/util/Map;"), ordinal = 0)
	Map<Enchantment, Integer> brokenFailEnchantmentPredicates(Map<Enchantment, Integer> enchantments, ItemStack stack) {
		if (stack.isDamageable() && stack.getDamage() == stack.getMaxDamage()) {
			return Map.of();
		}
		return enchantments;
	}
}
