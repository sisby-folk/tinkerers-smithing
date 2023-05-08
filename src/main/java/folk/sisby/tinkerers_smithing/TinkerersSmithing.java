package folk.sisby.tinkerers_smithing;

import folk.sisby.tinkerers_smithing.data.SmithingArmorMaterialLoader;
import folk.sisby.tinkerers_smithing.data.SmithingToolMaterialLoader;
import folk.sisby.tinkerers_smithing.data.SmithingTypeLoader;
import folk.sisby.tinkerers_smithing.data.SmithingUnitCostManager;
import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourceLoaderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TinkerersSmithing implements ModInitializer {
	public static final String ID = "tinkerers_smithing";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final TagKey<Item> DEWORK_INGREDIENTS = TagKey.of(Registry.ITEM_KEY, new Identifier(ID, "dework_ingredients"));
	public static final SpecialRecipeSerializer<ShapelessRepairRecipe> SHAPELESS_REPAIR_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_shapeless_repair"), new SpecialRecipeSerializer<>(ShapelessRepairRecipe::new));

	// Data
	public static final Map<Identifier, Collection<Item>> SMITHING_TYPES = new HashMap<>();
	public static final Map<Identifier, TinkerersSmithingMaterial> TOOL_MATERIALS = new HashMap<>();
	public static final Map<Identifier, TinkerersSmithingMaterial> ARMOR_MATERIALS = new HashMap<>();

	public static @Nullable Ingredient getRepairIngredient(Item item) {
		if (item.isDamageable()) {
			if (item instanceof ArmorItem ai && ai.getMaterial() != null && ai.getMaterial().getRepairIngredient() != null && !ai.getMaterial().getRepairIngredient().isEmpty()) {
				return ai.getMaterial().getRepairIngredient();
			} else if (item instanceof ToolItem ti && ti.getMaterial().getRepairIngredient() != null && !ti.getMaterial().getRepairIngredient().isEmpty()) {
				return ti.getMaterial().getRepairIngredient();
			}
		}
		return null;
	}

	public static void generateUnitCosts(MinecraftServer server) {
		if (server != null) {
			Registry.ITEM.forEach(item -> {
				((TinkerersSmithingItem) item).tinkerersSmithing$getUnitCosts().clear();

				SmithingUnitCostManager.UnitCostOverride override = SmithingUnitCostManager.INSTANCE.costOverrides.get(item);
				Map<Ingredient, Integer> costs = new HashMap<>();

				if (override == null || !override.replace()) {
					Ingredient repairIngredient = TinkerersSmithing.getRepairIngredient(item);
					if (repairIngredient != null) {
						// Naively calculate unit cost by testing the recipe with the same ID as the item itself
						server.getRecipeManager().get(Registry.ITEM.getId(item)).ifPresentOrElse(recipe -> {
							if (recipe.getOutput().isOf(item)) {
								int unitCost = Math.toIntExact(recipe.getIngredients().stream()
									.filter(ingredient -> Arrays.stream(ingredient.getMatchingStacks()).allMatch(repairIngredient))
									.filter(ingredient -> Arrays.stream(repairIngredient.getMatchingStacks()).allMatch(ingredient))
									.count());
								costs.put(repairIngredient, unitCost);
								if (unitCost != 0) {
									LOGGER.info("[Tinkerer's Smithing] Derived unit cost of {} for {}", unitCost, Registry.ITEM.getId(item));
								} else {
									LOGGER.info("[Tinkerer's Smithing] Couldn't derive unit cost using recipe {}", Registry.ITEM.getId(item));
								}
							}
						}, () -> {
							LOGGER.info("[Tinkerer's Smithing] No unit cost recipe for {}", Registry.ITEM.getId(item));
						});
					}
				}

				if (override != null) {
					costs.putAll(override.costs());
				}

				((TinkerersSmithingItem) item).tinkerersSmithing$getUnitCosts().putAll(costs);
			});

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
