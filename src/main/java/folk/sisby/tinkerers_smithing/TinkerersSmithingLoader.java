package folk.sisby.tinkerers_smithing;

import folk.sisby.tinkerers_smithing.data.SmithingUnitCostManager;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TinkerersSmithingLoader {
	public final Map<Identifier, Collection<Item>> SMITHING_TYPES = new HashMap<>();
	public final Map<Identifier, TinkerersSmithingMaterial> TOOL_MATERIALS = new HashMap<>();
	public final Map<Identifier, TinkerersSmithingMaterial> ARMOR_MATERIALS = new HashMap<>();
	public final Map<Item, SmithingUnitCostManager.UnitCostOverride> COST_OVERRIDES = new HashMap<>();
	public final List<Identifier> WARN_NO_RECIPE = new ArrayList<>();
	public final List<Identifier> WARN_NO_MATERIALS = new ArrayList<>();
	public final List<Identifier> WARN_DEFAULT_MATERIAL = new ArrayList<>();
	public int INFO_ADDED_COSTS = 0;
	public int INFO_ADDED_COST_ITEMS = 0;
	public int INFO_ADDED_UPGRADES = 0;
	public int INFO_ADDED_UPGRADE_ITEMS = 0;
	public int INFO_ADDED_SACRIFICES = 0;
	public int INFO_ADDED_SACRIFICE_ITEMS = 0;

	public List<TinkerersSmithingMaterial> getAllMaterials() {
		List<TinkerersSmithingMaterial> outList = new ArrayList<>();
		outList.addAll(TOOL_MATERIALS.values());
		outList.addAll(ARMOR_MATERIALS.values());
		return outList;
	}

	public List<Ingredient> getMaterialRepairIngredients(Consumer<Identifier> defaultConsumer, Item item) {
		List<Ingredient> outList = new ArrayList<>();

		getAllMaterials().forEach(material -> {
			if (material.items.contains(item)) {
				outList.addAll(material.repairMaterials);
			}
		});

		if (outList.isEmpty()) {
			if (item instanceof ArmorItem || item instanceof ToolItem) {
				defaultConsumer.accept(Registry.ITEM.getId(item));
			}
			if (item.isDamageable() && item instanceof ArmorItem ai) {
				ArmorMaterial material = ai.getMaterial();
				if (material != null) {
					Ingredient repairIngredient = material.getRepairIngredient();
					if (repairIngredient != null && !repairIngredient.isEmpty()) {
						outList.add(repairIngredient);
					}
				}
			}
			if (item.isDamageable() && item instanceof ToolItem ti) {
				ToolMaterial material = ti.getMaterial();
				if (material != null) {
					Ingredient repairIngredient = material.getRepairIngredient();
					if (repairIngredient != null && !repairIngredient.isEmpty()) {
						outList.add(repairIngredient);
					}
				}
			}
		}

		return outList;
	}

	public Collection<Item> getUpgradePaths(Item item) {
		Set<Item> outSet = new HashSet<>();

		List<Collection<Item>> types = new ArrayList<>();
		SMITHING_TYPES.forEach((id, items) -> {
			if (items.contains(item)) types.add(items);
		});

		if (!types.isEmpty()) {
			getAllMaterials().forEach(material -> {
				if (material.items.contains(item)) {
					Map<Identifier, TinkerersSmithingMaterial> map = material.type == TinkerersSmithingMaterial.EQUIPMENT_TYPE.ARMOR ? ARMOR_MATERIALS : TOOL_MATERIALS;
					material.upgradeableTo.forEach(id -> {
						TinkerersSmithingMaterial upgradeMaterial = map.get(id);
						if (upgradeMaterial.sacrificeVia == null) {
							upgradeMaterial.items.forEach(upgradeItem -> {
								if (types.stream().anyMatch(type -> type.contains(upgradeItem))) {
									outSet.add(upgradeItem);
								}
							});
						}
					});
				}
			});
		}

		if (!outSet.isEmpty()) {
			INFO_ADDED_UPGRADE_ITEMS++;
			INFO_ADDED_UPGRADES += outSet.size();
		}

		return outSet;
	}

	public Map<Ingredient, Integer> getUnitCosts(Item item, RecipeManager recipeManager) {
		Map<Ingredient, Integer> outMap = new HashMap<>();

		Identifier itemId = Registry.ITEM.getId(item);

		SmithingUnitCostManager.UnitCostOverride override = COST_OVERRIDES.get(item);
		List<Ingredient> repairIngredients = getMaterialRepairIngredients(WARN_DEFAULT_MATERIAL::add, item);

		if ((override == null || !override.replace()) && repairIngredients.isEmpty() && item.isDamageable()) {
			WARN_NO_MATERIALS.add(itemId);
		}

		if ((override == null || !override.replace()) && !repairIngredients.isEmpty()) {

			// Naively calculate unit cost by testing the recipe with the same ID as the item itself (ignoring path directories)
			List<Identifier> recipeIds = recipeManager.keys().filter(id -> id.getNamespace().equals(itemId.getNamespace()) && itemId.getPath().equals(id.getPath().substring(id.getPath().lastIndexOf('/') + 1))).toList();
			if (!recipeIds.isEmpty()) {
				for (Identifier recipeId : recipeIds) {
					Recipe<?> recipe = recipeManager.get(recipeId).orElse(null);
					if (recipe != null) {
						if (recipe.getOutput().isOf(item)) {
							for (Ingredient repairIngredient : repairIngredients) {
								int unitCost = Math.toIntExact(recipe.getIngredients().stream()
									.filter(ingredient -> Arrays.stream(ingredient.getMatchingStacks()).allMatch(repairIngredient))
									.filter(ingredient -> Arrays.stream(repairIngredient.getMatchingStacks()).allMatch(ingredient))
									.count());
								if (unitCost > 0) {
									outMap.put(repairIngredient, unitCost);
								}
							}
						}
					}
				}
			} else {
				WARN_NO_RECIPE.add(itemId);
			}
		}
		if (override != null) {
			outMap.putAll(override.costs());
			if (override.replace()) WARN_DEFAULT_MATERIAL.remove(itemId);
		}

		if (!outMap.isEmpty()) {
			INFO_ADDED_COST_ITEMS++;
			INFO_ADDED_COSTS += outMap.size();
		}

		return outMap;
	}

	public Map<Item, Pair<Integer, Map<Item, Integer>>> getSacrificePaths(Item item) { // This is gonna be ugly. This idea assumes a lot of 1:1ness in a system reworked to not do that.
		Map<Item, Pair<Integer, Map<Item, Integer>>> outMap = new HashMap<>();

		List<Collection<Item>> types = new ArrayList<>();
		SMITHING_TYPES.forEach((id, items) -> {
			if (items.contains(item)) types.add(items);
		});

		if (!types.isEmpty()) {
			for (TinkerersSmithingMaterial material : getAllMaterials()) {
				if (material.items.contains(item)) {
					Map<Identifier, TinkerersSmithingMaterial> map = material.type == TinkerersSmithingMaterial.EQUIPMENT_TYPE.ARMOR ? ARMOR_MATERIALS : TOOL_MATERIALS;
					Map<Identifier, TinkerersSmithingMaterial> otherMap = material.type == TinkerersSmithingMaterial.EQUIPMENT_TYPE.ARMOR ? TOOL_MATERIALS : ARMOR_MATERIALS;
					for (Identifier upgradeId : material.upgradeableTo) {
						TinkerersSmithingMaterial upgradeMaterial = map.get(upgradeId);
						if (upgradeMaterial.sacrificeVia != null) {
							List<Item> sacrificeViaItems = new ArrayList<>(map.get(upgradeMaterial.sacrificeVia).items);
							List<Item> sacrificeItems = new ArrayList<>(map.get(upgradeId).items);

							if (otherMap.containsKey(upgradeId)) {
								TinkerersSmithingMaterial otherUpgradeMaterial = otherMap.get(upgradeId);
								if (otherUpgradeMaterial.sacrificeVia.equals(upgradeMaterial.sacrificeVia)) {
									sacrificeViaItems.addAll(otherMap.get(otherUpgradeMaterial.sacrificeVia).items);
									sacrificeItems.addAll(otherUpgradeMaterial.items);
								}
							}

							for (Item upgradeItem : upgradeMaterial.items) {
								if (types.stream().anyMatch(type -> type.contains(upgradeItem))) {
									Map<Item, Integer> sacrifices = new HashMap<>();
									int upgradeViaCost = 0;
									for (Item viaItem : map.get(upgradeMaterial.sacrificeVia).items) {
										if (viaItem instanceof TinkerersSmithingItem vtsi && !vtsi.tinkerersSmithing$getUnitCosts().isEmpty()) {
											if (types.stream().anyMatch(type -> type.contains(viaItem))) {
												upgradeViaCost = vtsi.tinkerersSmithing$getUnitCosts().values().stream().findFirst().orElse(0);
											}
										}
									}
									if (upgradeViaCost > 0) {
										for (Item sacrificeItem : sacrificeItems) {
											List<Collection<Item>> sacrificeTypes = new ArrayList<>();
											SMITHING_TYPES.forEach((typeId, items) -> {
												if (items.contains(sacrificeItem)) sacrificeTypes.add(items);
											});
											int sacrificeViaCost = 0;
											for (Item viaItem : sacrificeViaItems) {
												if (viaItem instanceof TinkerersSmithingItem vtsi && !vtsi.tinkerersSmithing$getUnitCosts().isEmpty()) {
													if (sacrificeTypes.stream().anyMatch(type -> type.contains(viaItem))) {
														sacrificeViaCost = vtsi.tinkerersSmithing$getUnitCosts().values().stream().findFirst().orElse(0);
													}
												}
											}
											if (sacrificeViaCost > 0) {
												sacrifices.put(sacrificeItem, sacrificeViaCost);
											}
										}
										if (!sacrifices.isEmpty())
											outMap.put(upgradeItem, new Pair<>(upgradeViaCost, sacrifices));
									}
								}
							}
						}
					}
				}
			}
		}

		if (!outMap.isEmpty()) {
			INFO_ADDED_SACRIFICE_ITEMS++;
			INFO_ADDED_SACRIFICES += outMap.size();
		}

		return outMap;
	}

	public void generateItemSmithingData(@NotNull MinecraftServer server) {
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Data Initializing.");
		for (Item item : Registry.ITEM) {
			if (item instanceof TinkerersSmithingItem tsi) {
				Map<Ingredient, Integer> unitCosts = tsi.tinkerersSmithing$getUnitCosts();
				Set<Item> upgradePaths = tsi.tinkerersSmithing$getUpgradePaths();
				Map<Item, Pair<Integer, Map<Item, Integer>>> sacrificePaths = tsi.tinkerersSmithing$getSacrificePaths();
				unitCosts.clear();
				upgradePaths.clear();
				sacrificePaths.clear();
				unitCosts.putAll(getUnitCosts(item, server.getRecipeManager()));
				upgradePaths.addAll(getUpgradePaths(item));
				sacrificePaths.putAll(getSacrificePaths(item));
			}
		}
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Registered {} Tool Materials with {} items: [{}]", TOOL_MATERIALS.size(), TOOL_MATERIALS.values().stream().map(m -> m.items.size()).reduce(Integer::sum).orElse(0), TOOL_MATERIALS.entrySet().stream().map(e -> e.getKey().toString() + "(" + e.getValue().items.size() + ")").collect(Collectors.joining(", ")));
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Registered {} Armor Materials with {} items: [{}].", ARMOR_MATERIALS.size(), ARMOR_MATERIALS.values().stream().map(m -> m.items.size()).reduce(Integer::sum).orElse(0), ARMOR_MATERIALS.entrySet().stream().map(e -> e.getKey().toString() + "(" + e.getValue().items.size() + ")").collect(Collectors.joining(", ")));
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Registered {} Equipment Types with {} items: [{}]", SMITHING_TYPES.size(), SMITHING_TYPES.values().stream().map(Collection::size).reduce(Integer::sum).orElse(0), SMITHING_TYPES.entrySet().stream().map(e -> e.getKey().toString() + "(" + e.getValue().size() + ")").collect(Collectors.joining(", ")));
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Applied {} Unit Costs to {} items", INFO_ADDED_COSTS, INFO_ADDED_COST_ITEMS);
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Applied {} Upgrade Paths to {} items", INFO_ADDED_UPGRADES, INFO_ADDED_UPGRADE_ITEMS);
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Applied {} Sacrifice Paths to {} items", INFO_ADDED_SACRIFICES, INFO_ADDED_SACRIFICE_ITEMS);
		if (!WARN_DEFAULT_MATERIAL.isEmpty()) TinkerersSmithing.LOGGER.warn("[Tinkerer's Smithing] Found {} equipment items without registered repair materials: [{}]", WARN_DEFAULT_MATERIAL.size(), WARN_DEFAULT_MATERIAL.stream().map(Identifier::toString).collect(Collectors.joining(", ")));
		if (!WARN_NO_RECIPE.isEmpty()) TinkerersSmithing.LOGGER.warn("[Tinkerer's Smithing] Found {} equipment items without unit cost recipes: [{}]", WARN_NO_RECIPE.size(), WARN_NO_RECIPE.stream().map(Identifier::toString).collect(Collectors.joining(", ")));
		if (!WARN_NO_MATERIALS.isEmpty()) TinkerersSmithing.LOGGER.warn("[Tinkerer's Smithing] Found {} damageable items without repair materials: [{}]", WARN_NO_MATERIALS.size(), WARN_NO_MATERIALS.stream().map(Identifier::toString).collect(Collectors.joining(", ")));
	}
}
