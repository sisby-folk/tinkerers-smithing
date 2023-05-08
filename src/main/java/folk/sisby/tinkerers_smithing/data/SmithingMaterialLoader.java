package folk.sisby.tinkerers_smithing.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import folk.sisby.tinkerers_smithing.TinkerersSmithingMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SmithingMaterialLoader extends JsonDataLoader {
	public final Map<Identifier, TinkerersSmithingMaterial> outputMap;

	public static final String KEY_INHERIT_FROM_ITEM = "inheritFromItem";
	public static final String KEY_REPAIR_MATERIALS = "repairMaterials";
	public static final String KEY_ADD_ITEM = "addItem";
	public static final String KEY_REMOVE_ITEM = "removeItem";
	public static final String KEY_UPGRADES_FROM = "upgradesFrom";
	public static final String KEY_UPGRADES_TO = "upgradesTo";
	public static final String KEY_SACRIFICE_VIA = "sacrificesVia";

	public SmithingMaterialLoader(Gson gson, String dataType, Map<Identifier, TinkerersSmithingMaterial> outputMap) {
		super(gson, dataType);
		this.outputMap = outputMap;
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		this.outputMap.clear();
		Map<Identifier, List<Identifier>> upgradeFromMap = new HashMap<>();
		prepared.forEach((id, json) -> {
			List<Identifier> upgradeableTo = new ArrayList<>();
			List<Ingredient> repairMaterials = new ArrayList<>();
			List<Item> items = new ArrayList<>();
			Identifier sacrificeVia = null;

			JsonObject baseObject = json.getAsJsonObject();
			if (baseObject.has(KEY_INHERIT_FROM_ITEM)) {
				Identifier inheritItemId = Identifier.tryParse(baseObject.get(KEY_INHERIT_FROM_ITEM).getAsString());
				if (inheritItemId != null) {
					Registry.ITEM.getOrEmpty(inheritItemId).ifPresent(item -> repairMaterials.add(getDefaultRepairIngredient(item)));
				}
				// Can we get other items of the same tool material or not? e.g. for chainmail
			}
			if (baseObject.has(KEY_REPAIR_MATERIALS)) {
				baseObject.get(KEY_REPAIR_MATERIALS).getAsJsonArray().forEach(jsonIngredient -> repairMaterials.add(Ingredient.fromJson(jsonIngredient)));
			}
			if (baseObject.has(KEY_ADD_ITEM)) {
				baseObject.get(KEY_ADD_ITEM).getAsJsonArray().forEach(jsonItemId -> items.add(Registry.ITEM.getOrEmpty(new Identifier(jsonItemId.getAsString())).get()));
			}
			if (baseObject.has(KEY_REMOVE_ITEM)) {
				baseObject.get(KEY_REMOVE_ITEM).getAsJsonArray().forEach(jsonItemId -> items.remove(Registry.ITEM.getOrEmpty(new Identifier(jsonItemId.getAsString())).get()));
			}
			if (baseObject.has(KEY_UPGRADES_FROM)) {
				baseObject.get(KEY_UPGRADES_FROM).getAsJsonArray().forEach(jsonMaterialId -> {
					upgradeFromMap.computeIfAbsent(new Identifier(jsonMaterialId.getAsString()), k -> new ArrayList<>()).add(id);
				});
			}
			if (baseObject.has(KEY_UPGRADES_TO)) {
				baseObject.get(KEY_UPGRADES_TO).getAsJsonArray().forEach(jsonMaterialId -> upgradeableTo.add(new Identifier(jsonMaterialId.getAsString())));
			}
			if (baseObject.has(KEY_SACRIFICE_VIA)) {
				sacrificeVia = new Identifier(baseObject.get(KEY_SACRIFICE_VIA).getAsString());
			}

			this.outputMap.put(id, new TinkerersSmithingMaterial(upgradeableTo, repairMaterials, items, sacrificeVia));
		});
		upgradeFromMap.forEach((identifier, identifiers) -> {
			if (this.outputMap.containsKey(identifier)) {
				this.outputMap.get(identifier).upgradeableTo().addAll(identifiers);
			}
		});
	}

	public abstract Ingredient getDefaultRepairIngredient(Item item);
}
