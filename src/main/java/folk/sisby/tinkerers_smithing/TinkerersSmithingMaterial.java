package folk.sisby.tinkerers_smithing;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.List;

public record TinkerersSmithingMaterial(List<Identifier> upgradeableTo, List<Ingredient> repairMaterials, List<Item> items, Identifier sacrificeVia) {
}
