package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(Item.class)
public class ItemMixin implements TinkerersSmithingItem {
	@Unique final Map<Ingredient, Integer> unitCosts = new HashMap<>();
	@Unique final Set<Item> upgradePaths = new HashSet<>();
	@Unique final Map<Item, Pair<Integer, Map<Item, Integer>>> sacrificePaths = new HashMap<>();

	@Override
	public Map<Ingredient, Integer> tinkerersSmithing$getUnitCosts() {
		return this.unitCosts;
	}

	@Override
	public Set<Item> tinkerersSmithing$getUpgradePaths() {
		return this.upgradePaths;
	}

	@Override
	public Map<Item, Pair<Integer, Map<Item, Integer>>> tinkerersSmithing$getSacrificePaths() {
		return this.sacrificePaths;
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
	private void overrideAnvilRepairIngredients(ItemStack stack, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
		TinkerersSmithing.LOGGER.info("OVERRIDE CHECK");
		if (!tinkerersSmithing$getUnitCosts().isEmpty()) {
			TinkerersSmithing.LOGGER.info("OVERRIDE {}", tinkerersSmithing$getUnitCost(ingredient) > 0);
			cir.setReturnValue(tinkerersSmithing$getUnitCost(ingredient) > 0);
			cir.cancel();
		}
	}
}
