package folk.sisby.tinkerers_smithing.data;

import com.google.gson.Gson;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingLoader;
import folk.sisby.tinkerers_smithing.TinkerersSmithingMaterial;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SmithingArmorMaterialLoader extends SmithingMaterialLoader implements IdentifiableResourceReloadListener, RecipeDataDependency {
	public static final SmithingArmorMaterialLoader INSTANCE = new SmithingArmorMaterialLoader(new Gson());
	public static final Identifier ID = new Identifier(TinkerersSmithing.ID, "smithing_armor_material_loader");

	public SmithingArmorMaterialLoader(Gson gson) {
		super(gson, "smithing_armor_materials", TinkerersSmithingMaterial.EQUIPMENT_TYPE.ARMOR);
	}

	@Override
	public Ingredient getDefaultRepairIngredient(Item item) {
		if (item.isDamageable() && item instanceof ArmorItem ai) {
			ArmorMaterial material = ai.getMaterial();
			if (material != null) {
				Ingredient repairIngredient = material.getRepairIngredient();
				if (repairIngredient != null && !repairIngredient.isEmpty()) {
					return repairIngredient;
				}
			}
		}
		return null;
	}

	@Override
	public boolean matchingMaterials(Item item1, Item item2) {
		return item1 instanceof ArmorItem ai1 && item2 instanceof ArmorItem ai2 && ai1.getMaterial() == ai2.getMaterial();
	}

	@Override
	public Map<Identifier, TinkerersSmithingMaterial> getOutputMap() {
		return TinkerersSmithingLoader.INSTANCE.ARMOR_MATERIALS;
	}

	@Override
	public @NotNull Identifier getFabricId() {
		return ID;
	}
}
