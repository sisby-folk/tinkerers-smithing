package folk.sisby.tinkerers_smithing;

import folk.sisby.tinkerers_smithing.data.SmithingArmorMaterialLoader;
import folk.sisby.tinkerers_smithing.data.SmithingToolMaterialLoader;
import folk.sisby.tinkerers_smithing.data.SmithingTypeLoader;
import folk.sisby.tinkerers_smithing.data.SmithingUnitCostManager;
import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourceLoaderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TinkerersSmithing implements ModInitializer {
	public static final String ID = "tinkerers_smithing";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final TagKey<Item> DEWORK_INGREDIENTS = TagKey.of(Registry.ITEM_KEY, new Identifier(ID, "dework_ingredients"));
	public static final SpecialRecipeSerializer<ShapelessRepairRecipe> SHAPELESS_REPAIR_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_shapeless_repair"), new SpecialRecipeSerializer<>(ShapelessRepairRecipe::new));

	// Data
	public static final Map<Identifier, Collection<Item>> SMITHING_TYPES = new HashMap<>();
	public static final Map<Identifier, TinkerersSmithingMaterial> TOOL_MATERIALS = new HashMap<>();
	public static final Map<Identifier, TinkerersSmithingMaterial> ARMOR_MATERIALS = new HashMap<>();

	public static List<Ingredient> getMaterialRepairIngredients(Item item) {
		List<Ingredient> outList = new ArrayList<>();

		ARMOR_MATERIALS.values().forEach(material -> {
			if (material.items().contains(item)) {
				outList.addAll(material.repairMaterials());
			}
		});
		TOOL_MATERIALS.values().forEach(material -> {
			if (material.items().contains(item)) {
				outList.addAll(material.repairMaterials());
			}
		});

		return outList;
	}

	public static void generateUnitCosts(MinecraftServer server) {
		if (server != null) {
			Registry.ITEM.forEach(item -> {
				if (item instanceof  TinkerersSmithingItem tsi) {
					Map<Ingredient, Integer> costs = tsi.tinkerersSmithing$getUnitCosts();
					costs.clear();

					SmithingUnitCostManager.UnitCostOverride override = SmithingUnitCostManager.INSTANCE.costOverrides.get(item);

					if (override == null || !override.replace()) {
						// Naively calculate unit cost by testing the recipe with the same ID as the item itself
						server.getRecipeManager().get(Registry.ITEM.getId(item)).ifPresentOrElse(recipe -> {
							if (recipe.getOutput().isOf(item)) {
								List<Ingredient> repairIngredients = getMaterialRepairIngredients(item);
								repairIngredients.forEach(repairIngredient -> {
									int unitCost = Math.toIntExact(recipe.getIngredients().stream()
										.filter(ingredient -> Arrays.stream(ingredient.getMatchingStacks()).allMatch(repairIngredient))
										.filter(ingredient -> Arrays.stream(repairIngredient.getMatchingStacks()).allMatch(ingredient))
										.count());
									costs.put(repairIngredient, unitCost);
								});
							}
						}, () -> {
							LOGGER.info("[Tinkerer's Smithing] No unit cost recipe for {}", Registry.ITEM.getId(item));
						});
					}

					if (override != null) {
						costs.putAll(override.costs());
					}
				}
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
