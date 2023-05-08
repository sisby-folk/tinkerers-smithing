package folk.sisby.tinkerers_smithing.data;

import com.google.gson.Gson;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

public class SmithingToolMaterialLoader extends SmithingMaterialLoader implements IdentifiableResourceReloader {
	public static final SmithingToolMaterialLoader INSTANCE = new SmithingToolMaterialLoader(new Gson());
	public static final Identifier ID = new Identifier(TinkerersSmithing.ID, "smithing_tool_material_loader");

	public SmithingToolMaterialLoader(Gson gson) {
		super(gson, "smithing_tool_materials", TinkerersSmithing.TOOL_MATERIALS);
	}

	@Override
	public @NotNull Identifier getQuiltId() {
		return ID;
	}

	@Override
	public Ingredient getDefaultRepairIngredient(Item item) {
		if (item.isDamageable() && item instanceof ToolItem ti) {
			ToolMaterial material = ti.getMaterial();
			if (material != null) {
				Ingredient repairIngredient = material.getRepairIngredient();
				if (repairIngredient != null && !repairIngredient.isEmpty()) {
					return repairIngredient;
				}
			}
		}
		return null;
	}
}
