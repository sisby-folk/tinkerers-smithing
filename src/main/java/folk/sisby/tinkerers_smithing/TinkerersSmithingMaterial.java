package folk.sisby.tinkerers_smithing;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;

public final class TinkerersSmithingMaterial {
	public final EQUIPMENT_TYPE type;
	public final Set<Identifier> upgradeableTo;
	public final List<Ingredient> repairMaterials;
	public final Set<Item> items;
	public Identifier sacrificeVia;

	public TinkerersSmithingMaterial(EQUIPMENT_TYPE type, Set<Identifier> upgradeableTo, List<Ingredient> repairMaterials, Set<Item> items, Identifier sacrificeVia) {
		this.type = type;
		this.upgradeableTo = upgradeableTo;
		this.repairMaterials = repairMaterials;
		this.items = items;
		this.sacrificeVia = sacrificeVia;
	}

	public enum EQUIPMENT_TYPE {
		TOOL,
		ARMOR
	}
}
