package folk.sisby.tinkerers_smithing;

import folk.sisby.tinkerers_smithing.data.SmithingArmorMaterialLoader;
import folk.sisby.tinkerers_smithing.data.SmithingToolMaterialLoader;
import folk.sisby.tinkerers_smithing.data.SmithingTypeLoader;
import folk.sisby.tinkerers_smithing.data.SmithingUnitCostManager;
import folk.sisby.tinkerers_smithing.recipe.SacrificeUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.SmithingUpgradeRecipe;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourceLoaderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TinkerersSmithing implements ModInitializer {
	public static final String ID = "tinkerers_smithing";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final TagKey<Item> DEWORK_INGREDIENTS = TagKey.of(Registry.ITEM_KEY, new Identifier(ID, "dework_ingredients"));
	public static final SpecialRecipeSerializer<ShapelessRepairRecipe> SHAPELESS_REPAIR_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_shapeless_repair"), new SpecialRecipeSerializer<>(ShapelessRepairRecipe::new));
	public static final SpecialRecipeSerializer<ShapelessUpgradeRecipe> SHAPELESS_UPGRADE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_shapeless_upgrade"), new SpecialRecipeSerializer<>(ShapelessUpgradeRecipe::new));
	public static final SpecialRecipeSerializer<SmithingUpgradeRecipe> SMITHING_UPGRADE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_smithing_upgrade"), new SpecialRecipeSerializer<>(SmithingUpgradeRecipe::new));
	public static final SpecialRecipeSerializer<SacrificeUpgradeRecipe> SACRIFICE_UPGRADE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_sacrifice_upgrade"), new SpecialRecipeSerializer<>(SacrificeUpgradeRecipe::new));

	// Data
	public static final Map<Identifier, Collection<Item>> SMITHING_TYPES = new HashMap<>();
	public static final Map<Identifier, TinkerersSmithingMaterial> TOOL_MATERIALS = new HashMap<>();
	public static final Map<Identifier, TinkerersSmithingMaterial> ARMOR_MATERIALS = new HashMap<>();

	public static List<TinkerersSmithingMaterial> getAllMaterials() {
		List<TinkerersSmithingMaterial> outList = new ArrayList<>();
		outList.addAll(TOOL_MATERIALS.values());
		outList.addAll(ARMOR_MATERIALS.values());
		return outList;
	}

	public static List<Ingredient> getMaterialRepairIngredients(Item item) {
		List<Ingredient> outList = new ArrayList<>();

		getAllMaterials().forEach(material -> {
			if (material.items.contains(item)) {
				outList.addAll(material.repairMaterials);
			}
		});

		return outList;
	}


	public static Collection<Item> getUpgradePaths(Item item) {
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
						upgradeMaterial.items.forEach(upgradeItem -> {
							if (types.stream().anyMatch(type -> type.contains(upgradeItem))) {
								outSet.add(upgradeItem);
							}
						});
					});
				}
			});
		}

		return outSet;
	}

	public static Map<Item, Pair<Integer, Map<Item, Integer>>> getSacrificePaths(Item item) { // This is gonna be ugly. This idea assumes a lot of 1:1ness in a system reworked to not do that.
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
												upgradeViaCost = vtsi.tinkerersSmithing$getUnitCosts().values().stream().findFirst().get();
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
														sacrificeViaCost = vtsi.tinkerersSmithing$getUnitCosts().values().stream().findFirst().get();
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

		return outMap;
	}

	public static void generateUnitCosts(MinecraftServer server) {
		if (server != null) {
			AtomicInteger costsAdded = new AtomicInteger();
			AtomicInteger costItemsAdded = new AtomicInteger();
			Registry.ITEM.forEach(item -> {
				Identifier itemId = Registry.ITEM.getId(item);
				List<Ingredient> repairIngredients = getMaterialRepairIngredients(item);
				if (item instanceof TinkerersSmithingItem tsi && !repairIngredients.isEmpty()) {
					Map<Ingredient, Integer> costs = tsi.tinkerersSmithing$getUnitCosts();
					costs.clear();

					SmithingUnitCostManager.UnitCostOverride override = SmithingUnitCostManager.INSTANCE.costOverrides.get(item);

					if (override == null || !override.replace()) {
						// Naively calculate unit cost by testing the recipe with the same ID as the item itself
						Recipe<?> recipe = server.getRecipeManager().get(itemId).orElse(null);
						if (recipe == null)
							recipe = server.getRecipeManager().get(new Identifier(itemId.getNamespace(), "crafting/" + itemId.getPath())).orElse(null);
						if (recipe != null) {
							if (recipe.getOutput().isOf(item)) {
								for (Ingredient repairIngredient : repairIngredients) {
									int unitCost = Math.toIntExact(recipe.getIngredients().stream()
										.filter(ingredient -> Arrays.stream(ingredient.getMatchingStacks()).allMatch(repairIngredient))
										.filter(ingredient -> Arrays.stream(repairIngredient.getMatchingStacks()).allMatch(ingredient))
										.count());
									if (unitCost > 0) {
										costsAdded.getAndIncrement();
										costs.put(repairIngredient, unitCost);
									}
								}
							}
						} else {
							LOGGER.warn("[Tinkerer's Smithing] No unit cost recipe for {}", itemId);
						}
					}
					if (override != null) {
						costs.putAll(override.costs());
					}
					if (!costs.isEmpty()) costItemsAdded.getAndIncrement();
				} else if (item instanceof ToolItem || item instanceof ArmorItem) {
					LOGGER.warn("[Tinkerer's Smithing] No material registered for {}", itemId);
				}
			});
			LOGGER.info("[Tinkerer's Smithing] Data Initialized.");
			LOGGER.info("[Tinkerer's Smithing] Loaded {} Tool Materials with {} Items: {}", TOOL_MATERIALS.size(), TOOL_MATERIALS.values().stream().map(m -> m.items.size()).reduce(Integer::sum).get(), TOOL_MATERIALS.entrySet().stream().map(e -> e.getKey().toString() + "(" + e.getValue().items.size() + ")").collect(Collectors.joining(", ")));
			LOGGER.info("[Tinkerer's Smithing] Loaded {} Armor Materials with {} Items: {}.", ARMOR_MATERIALS.size(), ARMOR_MATERIALS.values().stream().map(m -> m.items.size()).reduce(Integer::sum).get(), ARMOR_MATERIALS.entrySet().stream().map(e -> e.getKey().toString() + "(" + e.getValue().items.size() + ")").collect(Collectors.joining(", ")));
			LOGGER.info("[Tinkerer's Smithing] Loaded {} Equipment Types with {} Items: {}", SMITHING_TYPES.size(), SMITHING_TYPES.values().stream().map(Collection::size).reduce(Integer::sum).get(), SMITHING_TYPES.entrySet().stream().map(e -> e.getKey().toString() + "(" + e.getValue().size() + ")").collect(Collectors.joining(", ")));
			LOGGER.info("[Tinkerer's Smithing] Loaded {} Unit costs over {} Items.", costsAdded, costItemsAdded);
		}
	}

	@Override
	public void onInitialize(ModContainer mod) {
		ServerLifecycleEvents.READY.register(TinkerersSmithing::generateUnitCosts);
		ResourceLoaderEvents.END_DATA_PACK_RELOAD.register(((server, resourceManager, error) -> generateUnitCosts(server)));
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(SmithingToolMaterialLoader.INSTANCE);
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(SmithingArmorMaterialLoader.INSTANCE);
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(SmithingUnitCostManager.INSTANCE);
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(SmithingTypeLoader.INSTANCE);

		LOGGER.info("[Tinkerer's Smithing] Initialized.");
	}
}
