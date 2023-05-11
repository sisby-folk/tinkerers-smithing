package folk.sisby.tinkerers_smithing;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;

public final class TinkerersSmithingMaterial {
	public final EQUIPMENT_TYPE type;
	public final Set<Identifier> upgradesTo;
	public final List<Ingredient> repairMaterials;
	public final Set<Item> items;
	public Identifier sacrificesVia;

	public TinkerersSmithingMaterial(EQUIPMENT_TYPE type, Set<Identifier> upgradesTo, List<Ingredient> repairMaterials, Set<Item> items, Identifier sacrificesVia) {
		this.type = type;
		this.upgradesTo = upgradesTo;
		this.repairMaterials = repairMaterials;
		this.items = items;
		this.sacrificesVia = sacrificesVia;
	}

	public enum EQUIPMENT_TYPE {
		TOOL,
		ARMOR
	}
}
