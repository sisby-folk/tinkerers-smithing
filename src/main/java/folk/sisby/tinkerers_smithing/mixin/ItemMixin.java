package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Item.class)
public class ItemMixin implements TinkerersSmithingItem {
	@Unique Map<Ingredient, Integer> unitCosts = new HashMap<>();

	@Override
	public Map<Ingredient, Integer> tinkerersSmithing$getUnitCosts() {
		return this.unitCosts;
	}

	@Override
	public int tinkerersSmithing$getUnitCost(ItemStack stack) {
		for (Map.Entry<Ingredient, Integer> entry : unitCosts.entrySet()) {
			Ingredient ingredient = entry.getKey();
			Integer cost = entry.getValue();
			if (ingredient.test(stack)) {
				return cost;
			}
		}
		return 0;
	}

	@Inject(method = "canRepair", at = @At(value = "RETURN"), cancellable = true)
	private void mixin(ItemStack stack, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
		if (tinkerersSmithing$getUnitCost(ingredient) > 0) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
}
