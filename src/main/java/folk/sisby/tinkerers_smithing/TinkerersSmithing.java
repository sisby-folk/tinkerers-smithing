package folk.sisby.tinkerers_smithing;

import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoaderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinkerersSmithing implements ModInitializer {
	public static final String ID = "tinkerers_smithing";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final TagKey<Item> DEWORK_INGREDIENTS = TagKey.of(Registry.ITEM_KEY, new Identifier(ID, "dework_ingredients"));
	public static final SpecialRecipeSerializer<ShapelessRepairRecipe> SHAPELESS_REPAIR = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "shapeless_repair"), new SpecialRecipeSerializer<>(ShapelessRepairRecipe::new));

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

	@Override
	public void onInitialize(ModContainer mod) {
		// Naively calculate unit cost by testing the recipe with the same ID as the item itself
		ResourceLoaderEvents.END_DATA_PACK_RELOAD.register(((server, resourceManager, error) -> {
			Registry.ITEM.forEach(item -> {
				Ingredient repairIngredient = getRepairIngredient(item);
				if (repairIngredient != null) {
					((TinkerersSmithingItem) item).tinkerersSmithing$setUnitCost(0);
					server.getRecipeManager().get(Registry.ITEM.getId(item)).ifPresent(recipe -> {
						if (recipe.getOutput().isOf(item) && recipe.getIngredients().contains(repairIngredient)) {
							((TinkerersSmithingItem) item).tinkerersSmithing$setUnitCost((int) recipe.getIngredients().stream().filter(ingredient -> ingredient.equals(repairIngredient)).count());
						}
					});
				}
			});
		}));

		LOGGER.info("[Tinkerer's Smithing] Initialized.");
	}
}
