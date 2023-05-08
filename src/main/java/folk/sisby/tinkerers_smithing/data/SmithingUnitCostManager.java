package folk.sisby.tinkerers_smithing.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

import java.util.HashMap;
import java.util.Map;

public class SmithingUnitCostManager extends JsonDataLoader implements IdentifiableResourceReloader {
	public static final SmithingUnitCostManager INSTANCE = new SmithingUnitCostManager(new Gson());
	public static final Identifier ID = new Identifier(TinkerersSmithing.ID, "smithing_unit_cost_loader");

	public static final String KEY_REPLACE = "replace";
	public static final String KEY_VALUES = "values";
	public static final String KEY_VALUE_COST = "cost";

	public final Map<Item, UnitCostOverride> costOverrides = new HashMap<>();

	public SmithingUnitCostManager(Gson gson) {
		super(gson, "smithing_unit_costs");
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		prepared.forEach((id, json) -> {
			Registry.ITEM.getOrEmpty(id).ifPresentOrElse(item -> {
				boolean replace = json.getAsJsonObject().get(KEY_REPLACE).getAsBoolean();
				Map<Ingredient, Integer> costs = new HashMap<>();
				json.getAsJsonObject().get(KEY_VALUES).getAsJsonArray().forEach(jsonValue -> {
					Ingredient ingredient = Ingredient.fromJson(jsonValue.getAsJsonObject());
					int cost = jsonValue.getAsJsonObject().get(KEY_VALUE_COST).getAsInt();
					costs.put(ingredient, cost);
				});
				costOverrides.put(item, new UnitCostOverride(replace, costs));
			}, () -> {
				TinkerersSmithing.LOGGER.warn("[Tinkerer's Smithing] Failed to override cost for invalid item {}", id);
			});
		});
	}

	@Override
	public @NotNull Identifier getQuiltId() {
		return ID;
	}

	public record UnitCostOverride(boolean replace, Map<Ingredient, Integer> costs) {
	}
}
