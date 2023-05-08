package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(Item.class)
public class ItemMixin implements TinkerersSmithingItem {
	@Unique Map<Ingredient, Integer> unitCosts;

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
}
