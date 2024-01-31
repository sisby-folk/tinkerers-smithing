package folk.sisby.tinkerers_smithing;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import folk.sisby.tinkerers_smithing.data.SmithingUnitCostManager;
import folk.sisby.tinkerers_smithing.recipe.SacrificeUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.SmithingUpgradeRecipe;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static folk.sisby.tinkerers_smithing.TinkerersSmithing.ID;
import static folk.sisby.tinkerers_smithing.TinkerersSmithing.LOGGER;

public class TinkerersSmithingLoader {
	public static final TinkerersSmithingLoader INSTANCE = new TinkerersSmithingLoader();

	public final Map<Identifier, Collection<Item>> SMITHING_TYPES = new HashMap<>();
	public final Map<Identifier, TinkerersSmithingMaterial> TOOL_MATERIALS = new HashMap<>();
	public final Map<Identifier, TinkerersSmithingMaterial> ARMOR_MATERIALS = new HashMap<>();
	public final Map<Item, SmithingUnitCostManager.UnitCostOverride> COST_OVERRIDES = new HashMap<>();

	public final List<Recipe<?>> RECIPES = new ArrayList<>();

	public void generateItemSmithingData(Map<Identifier, Recipe<?>> recipes) {
		new LoaderRun().generateItemSmithingData(recipes);
		SMITHING_TYPES.clear();
		TOOL_MATERIALS.clear();
		ARMOR_MATERIALS.clear();
		COST_OVERRIDES.clear();
	}

	public class LoaderRun {
		public final List<Identifier> WARN_NO_RECIPE = new ArrayList<>();
		public final List<Identifier> WARN_NO_MATERIALS = new ArrayList<>();
		public final Map<Identifier, ArmorMaterial> WARN_DEFAULT_MATERIAL_ARMOR = new HashMap<>();
		public final Map<Identifier, ToolMaterial> WARN_DEFAULT_MATERIAL_TOOL = new HashMap<>();
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

		public List<Ingredient> getMaterialRepairIngredients(BiConsumer<Identifier, ArmorMaterial> armorDefaults, BiConsumer<Identifier, ToolMaterial> toolDefaults, Item item) {
			List<Ingredient> outList = new ArrayList<>();
			boolean noneMatch = true;

			for (TinkerersSmithingMaterial material : getAllMaterials()) {
				if (material.items.contains(item)) {
					noneMatch = false;
					outList.addAll(material.repairMaterials.stream().filter(i -> !i.isEmpty()).toList());
				}
			}

			if (noneMatch && item instanceof ArmorItem ai && ai.getMaterial() != null) {
				armorDefaults.accept(Registry.ITEM.getId(item), ai.getMaterial());
			}
			if (noneMatch && item instanceof ToolItem ti && ti.getMaterial() != null) {
				toolDefaults.accept(Registry.ITEM.getId(item), ti.getMaterial());
			}

			if (outList.isEmpty()) {
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
						material.upgradesTo.forEach(id -> {
							TinkerersSmithingMaterial upgradeMaterial = map.get(id);
							if (upgradeMaterial.sacrificesVia == null) {
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

		public Map<Ingredient, Integer> getUnitCosts(Item item, Map<Identifier, Recipe<?>> recipes) {
			Map<Ingredient, Integer> outMap = new HashMap<>();

			Identifier itemId = Registry.ITEM.getId(item);

			SmithingUnitCostManager.UnitCostOverride override = COST_OVERRIDES.get(item);
			List<Ingredient> repairIngredients = getMaterialRepairIngredients(WARN_DEFAULT_MATERIAL_ARMOR::put, WARN_DEFAULT_MATERIAL_TOOL::put, item);

			if ((override == null || !override.replace()) && repairIngredients.isEmpty() && item.isDamageable()) {
				WARN_NO_MATERIALS.add(itemId);
			}

			if ((override == null || !override.replace()) && !repairIngredients.isEmpty()) {

				// Naively calculate unit cost by testing the recipe with the same ID as the item itself (ignoring path directories)
				List<Identifier> recipeIds = recipes.keySet().stream().filter(id -> id.getNamespace().equals(itemId.getNamespace()) && itemId.getPath().equals(id.getPath().substring(id.getPath().lastIndexOf('/') + 1))).toList();
				if (!recipeIds.isEmpty()) {
					for (Identifier recipeId : recipeIds) {
						Recipe<?> recipe = recipes.get(recipeId);
						if (recipe != null) {
							if (recipe.getOutput().isOf(item)) {
								for (Ingredient repairIngredient : repairIngredients) {
									int unitCost = Math.toIntExact(recipe.getIngredients().stream()
										.filter(craftingIngredient -> TinkerersSmithing.CONFIG.matchesOrEquivalent(repairIngredient, craftingIngredient))
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
				if (override.replace()) {
					WARN_DEFAULT_MATERIAL_TOOL.remove(itemId);
					WARN_DEFAULT_MATERIAL_ARMOR.remove(itemId);
				}
			}

			if (!outMap.isEmpty()) {
				INFO_ADDED_COST_ITEMS++;
				INFO_ADDED_COSTS += outMap.size();
			}

			return outMap;
		}

		public Map<Item, Pair<Integer, Multimap<Integer, Item>>> getSacrificePaths(Item item, Map<Item, Map<Ingredient, Integer>> unitCosts) { // This is gonna be ugly. This idea assumes a lot of 1:1ness in a system reworked to not do that.
			Map<Item, Pair<Integer, Multimap<Integer, Item>>> outMap = new HashMap<>();

			List<Collection<Item>> types = new ArrayList<>();
			SMITHING_TYPES.forEach((id, items) -> {
				if (items.contains(item)) types.add(items);
			});

			if (!types.isEmpty()) {
				for (TinkerersSmithingMaterial material : getAllMaterials()) {
					if (material.items.contains(item)) {
						Map<Identifier, TinkerersSmithingMaterial> map = material.type == TinkerersSmithingMaterial.EQUIPMENT_TYPE.ARMOR ? ARMOR_MATERIALS : TOOL_MATERIALS;
						Map<Identifier, TinkerersSmithingMaterial> otherMap = material.type == TinkerersSmithingMaterial.EQUIPMENT_TYPE.ARMOR ? TOOL_MATERIALS : ARMOR_MATERIALS;
						for (Identifier upgradeId : material.upgradesTo) {
							TinkerersSmithingMaterial upgradeMaterial = map.get(upgradeId);
							if (upgradeMaterial.sacrificesVia != null) {
								List<Item> sacrificesViaItems = new ArrayList<>(map.get(upgradeMaterial.sacrificesVia).items);
								List<Item> sacrificeItems = new ArrayList<>(map.get(upgradeId).items);

								if (otherMap.containsKey(upgradeId)) {
									TinkerersSmithingMaterial otherUpgradeMaterial = otherMap.get(upgradeId);
									if (otherUpgradeMaterial.sacrificesVia.equals(upgradeMaterial.sacrificesVia)) {
										sacrificesViaItems.addAll(otherMap.get(otherUpgradeMaterial.sacrificesVia).items);
										sacrificeItems.addAll(otherUpgradeMaterial.items);
									}
								}

								for (Item upgradeItem : upgradeMaterial.items.stream().filter(upgradeItem -> types.stream().anyMatch(type -> type.contains(upgradeItem))).toList()) {
									Multimap<Integer, Item> sacrifices = HashMultimap.create();
									int upgradeViaCost = 0;
									for (Item viaItem : map.get(upgradeMaterial.sacrificesVia).items.stream().filter(viaItem -> types.stream().anyMatch(type -> type.contains(viaItem))).toList()) {
										upgradeViaCost = unitCosts.get(viaItem).values().stream().findFirst().orElse(0);
									}
									if (upgradeViaCost > 0) {
										for (Item sacrificeItem : sacrificeItems) {
											List<Collection<Item>> sacrificeTypes = new ArrayList<>();
											SMITHING_TYPES.forEach((typeId, items) -> {
												if (items.contains(sacrificeItem)) sacrificeTypes.add(items);
											});
											int sacrificesViaCost = 0;
											for (Item viaItem : sacrificesViaItems.stream().filter(viaItem -> sacrificeTypes.stream().anyMatch(type -> type.contains(viaItem))).toList()) {
												sacrificesViaCost = unitCosts.get(viaItem).values().stream().findFirst().orElse(0);
											}
											if (sacrificesViaCost > 0) {
												sacrifices.put(sacrificesViaCost, sacrificeItem);
											}
										}
										if (!sacrifices.isEmpty()) {
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

		public void generateItemSmithingData(Map<Identifier, Recipe<?>> recipes) {
			LOGGER.info("[Tinkerer's Smithing] Data Initializing.");
			RECIPES.clear();
			Map<Item, Map<Ingredient, Integer>> unitCosts = new HashMap<>();
			for (Item item : Registry.ITEM) {
				Map<Ingredient, Integer> unitCost = getUnitCosts(item, recipes);
				unitCost.forEach((unit, count) -> RECIPES.add(new ShapelessRepairRecipe(item, unit, count)));
				unitCosts.put(item, unitCost);
			}
			for (Item base : Registry.ITEM) {
				for (Item result : getUpgradePaths(base)) {
					unitCosts.get(result).forEach((addition, additionCount) -> {
						RECIPES.add(new SmithingUpgradeRecipe(base, addition, additionCount, result));
						RECIPES.add(new ShapelessUpgradeRecipe(base, addition, additionCount, result));
					});
				}
				getSacrificePaths(base, unitCosts).forEach((result, sacrifice) -> sacrifice.getRight().keySet().forEach(additionUnits -> {
					Collection<Item> additions = sacrifice.getRight().get(additionUnits);
					RECIPES.add(new SacrificeUpgradeRecipe(base, Ingredient.ofItems(additions.toArray(Item[]::new)), additionUnits, result, sacrifice.getLeft()));
				}));
			}
			LOGGER.info("[Tinkerer's Smithing] Registered {} Tool Materials with {} items: [{}]", TOOL_MATERIALS.size(), TOOL_MATERIALS.values().stream().map(m -> m.items.size()).reduce(Integer::sum).orElse(0), TOOL_MATERIALS.entrySet().stream().map(e -> e.getKey().toString() + "(" + e.getValue().items.size() + ")").collect(Collectors.joining(", ")));
			LOGGER.info("[Tinkerer's Smithing] Registered {} Armor Materials with {} items: [{}].", ARMOR_MATERIALS.size(), ARMOR_MATERIALS.values().stream().map(m -> m.items.size()).reduce(Integer::sum).orElse(0), ARMOR_MATERIALS.entrySet().stream().map(e -> e.getKey().toString() + "(" + e.getValue().items.size() + ")").collect(Collectors.joining(", ")));
			LOGGER.info("[Tinkerer's Smithing] Registered {} Equipment Types with {} items: [{}]", SMITHING_TYPES.size(), SMITHING_TYPES.values().stream().map(Collection::size).reduce(Integer::sum).orElse(0), SMITHING_TYPES.entrySet().stream().map(e -> e.getKey().toString() + "(" + e.getValue().size() + ")").collect(Collectors.joining(", ")));
			LOGGER.info("[Tinkerer's Smithing] Applied {} Unit Costs to {} items", INFO_ADDED_COSTS, INFO_ADDED_COST_ITEMS);
			LOGGER.info("[Tinkerer's Smithing] Applied {} Upgrade Paths to {} items", INFO_ADDED_UPGRADES, INFO_ADDED_UPGRADE_ITEMS);
			LOGGER.info("[Tinkerer's Smithing] Applied {} Sacrifice Paths to {} items", INFO_ADDED_SACRIFICES, INFO_ADDED_SACRIFICE_ITEMS);
			if (!WARN_DEFAULT_MATERIAL_ARMOR.isEmpty()) {
				Set<ArmorMaterial> armorMats = new HashSet<>(WARN_DEFAULT_MATERIAL_ARMOR.values());
				LOGGER.warn("[Tinkerer's Smithing] Found {} unregistered armor materials with {} armor items: [{}] items: [{}]", armorMats.size(), WARN_DEFAULT_MATERIAL_ARMOR.size(), armorMats.stream().map(Object::toString).collect(Collectors.joining(", ")), WARN_DEFAULT_MATERIAL_ARMOR.entrySet().stream()
					.collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList()))).entrySet().stream() // Invert Map
					.map(e -> "%s[%s]".formatted(e.getKey().getName(), e.getValue().stream().map(Identifier::toString).collect(Collectors.joining(", ")))).collect(Collectors.joining(", "))); // Stringify
			}
			if (!WARN_DEFAULT_MATERIAL_TOOL.isEmpty()) {
				Set<ToolMaterial> toolMats = new HashSet<>(WARN_DEFAULT_MATERIAL_TOOL.values());
				LOGGER.warn("[Tinkerer's Smithing] Found {} unregistered tool materials with {} tool items: [{}] - items: [{}]", toolMats.size(), WARN_DEFAULT_MATERIAL_TOOL.size(), toolMats.stream().map(Object::toString).collect(Collectors.joining(", ")), WARN_DEFAULT_MATERIAL_TOOL.entrySet().stream()
					.collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList()))).entrySet().stream() // Invert Map
					.map(e -> "%s[%s]".formatted(e.getKey().toString(), e.getValue().stream().map(Identifier::toString).collect(Collectors.joining(", ")))).collect(Collectors.joining(", "))); // Stringify
			}
			if (!WARN_NO_RECIPE.isEmpty())
				LOGGER.warn("[Tinkerer's Smithing] Found {} equipment items without unit cost recipes: [{}]", WARN_NO_RECIPE.size(), WARN_NO_RECIPE.stream().map(Identifier::toString).collect(Collectors.joining(", ")));
			if (!WARN_NO_MATERIALS.isEmpty())
				LOGGER.warn("[Tinkerer's Smithing] Found {} damageable items without repair materials: [{}]", WARN_NO_MATERIALS.size(), WARN_NO_MATERIALS.stream().map(Identifier::toString).collect(Collectors.joining(", ")));
			LOGGER.info("[Tinkerer's Smithing] Data Initialized!");
		}
	}

	public static Identifier recipeId(String recipeType, String... names) {
		return new Identifier(ID, recipeType + "/" + String.join("/", names));
	}

	public static Identifier recipeId(String recipeType, Identifier... ids) {
		return recipeId(recipeType, Arrays.stream(ids).map(id -> id.getNamespace().equals("minecraft") ? id.getPath() : id.getNamespace() + "/" + id.getPath()).toArray(String[]::new));
	}

	public static Identifier recipeId(String recipeType, Item... items) {
		return recipeId(recipeType, Arrays.stream(items).map(Registry.ITEM::getId).toArray(Identifier[]::new));
	}

	public static Identifier appendId(Identifier id, String name) {
		return new Identifier(id.toString() + "/" + name);
	}

	public static Identifier repairRecipeId(Item baseItem, Ingredient ingredient) {
		if (ingredient.entries.length == 0) {
			throw new IllegalArgumentException("Ingredients for Tinkerer's Smithing recipes can't be empty! When repairing item %s".formatted(Registry.ITEM.getId(baseItem)));
		}
		Identifier ingredientId = ingredient.entries[0] instanceof Ingredient.StackEntry se ? Registry.ITEM.getId(se.stack.getItem()) : ingredient.entries[0] instanceof Ingredient.TagEntry te ? te.tag.id() : new Identifier("ERROR");
		return recipeId("repair", Registry.ITEM.getId(baseItem), ingredientId);
	}
}
