package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithingItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements TinkerersSmithingItem {
	@Unique int unitCost = 0;

	@Override
	public void tinkerersSmithing$setUnitCost(int unitCost) {
		this.unitCost = unitCost;
	}

	@Override
	public int tinkerersSmithing$getUnitCost() {
		return this.unitCost;
	}
}
