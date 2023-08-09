package folk.sisby.tinkerers_smithing.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class SmithingUnitCostManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
	public static final SmithingUnitCostManager INSTANCE = new SmithingUnitCostManager(new Gson());
	public static final Identifier ID = new Identifier(TinkerersSmithing.ID, "smithing_unit_cost_loader");

	public static final String KEY_REPLACE = "replace";
	public static final String KEY_VALUES = "values";
	public static final String KEY_VALUE_COST = "cost";

	public SmithingUnitCostManager(Gson gson) {
		super(gson, "smithing_unit_costs");
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Loading Unit Costs!");
		prepared.forEach((id, json) -> Registry.ITEM.getOrEmpty(id).ifPresentOrElse(item -> {
			boolean replace = json.getAsJsonObject().get(KEY_REPLACE).getAsBoolean();
			Map<Ingredient, Integer> costs = new HashMap<>();
			json.getAsJsonObject().get(KEY_VALUES).getAsJsonArray().forEach(jsonValue -> {
				Ingredient ingredient = Ingredient.fromJson(jsonValue.getAsJsonObject());
				int cost = jsonValue.getAsJsonObject().get(KEY_VALUE_COST).getAsInt();
				costs.put(ingredient, cost);
			});
			TinkerersSmithing.getLoaderInstance().COST_OVERRIDES.put(item, new UnitCostOverride(replace, costs));
		}, () -> TinkerersSmithing.LOGGER.warn("[Tinkerer's Smithing] Failed to override cost for invalid item {}", id)));
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Reloaded unit cost overrides");
	}

	@Override
	public @NotNull Identifier getFabricId() {
		return ID;
	}

	public record UnitCostOverride(boolean replace, Map<Ingredient, Integer> costs) {
	}


}
