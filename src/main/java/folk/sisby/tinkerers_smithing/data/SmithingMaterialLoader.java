package folk.sisby.tinkerers_smithing.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

import java.util.*;

public abstract class SmithingMaterialLoader extends MultiJsonDataLoader {
	public final TinkerersSmithingMaterial.EQUIPMENT_TYPE type;

	public static final String KEY_INHERIT_FROM_ITEM = "inheritFromItem";
	public static final String KEY_REPAIR_MATERIALS = "repairMaterials";
	public static final String KEY_ADD_ITEM = "addItem";
	public static final String KEY_REMOVE_ITEM = "removeItem";
	public static final String KEY_UPGRADES_FROM = "upgradesFrom";
	public static final String KEY_UPGRADES_TO = "upgradesTo";
	public static final String KEY_SACRIFICE_VIA = "sacrificesVia";

	public SmithingMaterialLoader(Gson gson, String dataType, TinkerersSmithingMaterial.EQUIPMENT_TYPE type) {
		super(gson, dataType);
		this.type = type;
	}

	public static Item getOrWarnItem(Identifier id, Identifier resourceId) {
		Item item = Registry.ITEM.getOrEmpty(id).orElse(null);
		if (item == null) {
			TinkerersSmithing.LOGGER.warn("[Tinkerer's Smithing] Couldn't load item {} from {} as it wasn't registered!", id, resourceId);
		}
		return item;
	}

	public static void addOrWarnItem(Collection<Item> collection, Identifier id, Identifier resourceId) {
		Item item = getOrWarnItem(id, resourceId);
		if (item != null) collection.add(item);
	}

	public static void removeOrWarnItem(Collection<Item> collection, Identifier id, Identifier resourceId) {
		Item item = getOrWarnItem(id, resourceId);
		if (item != null) collection.remove(item);
	}

	@Override
	protected void apply(Map<Identifier, Collection<Pair<JsonElement, String>>> prepared, ResourceManager manager, Profiler profiler) {
		Map<Identifier, TinkerersSmithingMaterial> outputMap = this.getOutputMap();
		Map<Identifier, List<Identifier>> upgradeFromMap = new HashMap<>();
		prepared.forEach((id, jsons) -> {
			Set<Identifier> upgradesTo = new HashSet<>();
			List<Ingredient> repairMaterials = new ArrayList<>();
			Set<Item> items = new HashSet<>();
			Identifier sacrificesVia = null;

			for (Pair<JsonElement, String> entry : jsons) {
				JsonObject baseObject = entry.getLeft().getAsJsonObject();
				if (baseObject.has(KEY_INHERIT_FROM_ITEM)) {
					Identifier inheritItemId = Identifier.tryParse(baseObject.get(KEY_INHERIT_FROM_ITEM).getAsString());
					if (inheritItemId != null) {
						Item inheritItem = getOrWarnItem(inheritItemId, id);
						if (inheritItem != null) {
							repairMaterials.add(getDefaultRepairIngredient(inheritItem));
							Registry.ITEM.forEach(matchingItem -> {
								if (matchingMaterials(inheritItem, matchingItem)) items.add(matchingItem);
							});
						}
					}
				}
				if (baseObject.has(KEY_REPAIR_MATERIALS)) {
					baseObject.get(KEY_REPAIR_MATERIALS).getAsJsonArray().forEach(jsonIngredient -> repairMaterials.add(Ingredient.fromJson(jsonIngredient)));
				}
				if (baseObject.has(KEY_ADD_ITEM)) {
					baseObject.get(KEY_ADD_ITEM).getAsJsonArray().forEach(jsonItemId -> addOrWarnItem(items, new Identifier(jsonItemId.getAsString()), id));
				}
				if (baseObject.has(KEY_REMOVE_ITEM)) {
					baseObject.get(KEY_REMOVE_ITEM).getAsJsonArray().forEach(jsonItemId -> removeOrWarnItem(items, new Identifier(jsonItemId.getAsString()), id));
				}
				if (baseObject.has(KEY_UPGRADES_FROM)) {
					baseObject.get(KEY_UPGRADES_FROM).getAsJsonArray().forEach(jsonMaterialId -> {
						upgradeFromMap.computeIfAbsent(new Identifier(jsonMaterialId.getAsString()), k -> new ArrayList<>()).add(id);
					});
				}
				if (baseObject.has(KEY_UPGRADES_TO)) {
					baseObject.get(KEY_UPGRADES_TO).getAsJsonArray().forEach(jsonMaterialId -> upgradesTo.add(new Identifier(jsonMaterialId.getAsString())));
				}
				if (baseObject.has(KEY_SACRIFICE_VIA)) {
					sacrificesVia = new Identifier(baseObject.get(KEY_SACRIFICE_VIA).getAsString());
				}
			}

			outputMap.put(id, new TinkerersSmithingMaterial(this.type, upgradesTo, repairMaterials, items, sacrificesVia));
		});
		upgradeFromMap.forEach((identifier, identifiers) -> {
			if (outputMap.containsKey(identifier)) {
				outputMap.get(identifier).upgradesTo.addAll(identifiers);
			}
		});
		// Remove and warn invalid material references
		outputMap.forEach((id, material) -> {
			material.upgradesTo.stream().filter(i -> !outputMap.containsKey(i)).toList().forEach(removeId -> {
				TinkerersSmithing.LOGGER.warn("[Tinkerer's Smithing] Invalid upgrade {} in {}", removeId, id);
				material.upgradesTo.remove(removeId);
			});
			if (material.sacrificesVia != null && !outputMap.containsKey(material.sacrificesVia)) {
				TinkerersSmithing.LOGGER.warn("[Tinkerer's Smithing] Invalid via {} in {}", material.sacrificesVia, id);
				material.sacrificesVia = null;
			}
		});
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Reloaded a set of materials");
	}

	public abstract Ingredient getDefaultRepairIngredient(Item item);

	public abstract boolean matchingMaterials(Item item1, Item item2);

	public abstract Map<Identifier, TinkerersSmithingMaterial> getOutputMap();
}
