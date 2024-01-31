package folk.sisby.tinkerers_smithing;

import folk.sisby.tinkerers_smithing.data.SmithingArmorMaterialLoader;
import folk.sisby.tinkerers_smithing.data.SmithingToolMaterialLoader;
import folk.sisby.tinkerers_smithing.data.SmithingTypeLoader;
import folk.sisby.tinkerers_smithing.data.SmithingUnitCostManager;
import folk.sisby.tinkerers_smithing.recipe.SacrificeUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import folk.sisby.tinkerers_smithing.recipe.ShapelessUpgradeRecipe;
import folk.sisby.tinkerers_smithing.recipe.SmithingUpgradeRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class TinkerersSmithing implements ModInitializer {
	public static final String ID = "tinkerers_smithing";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final TinkerersSmithingConfig CONFIG = TinkerersSmithingConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", ID, TinkerersSmithingConfig.class);

	public static final Identifier S2C_PING = new Identifier(ID, "ping");

	public static final TagKey<Item> DEWORK_INGREDIENTS = TagKey.of(Registry.ITEM_KEY, new Identifier(ID, "dework_ingredients"));
	public static final TagKey<Item> BROKEN_BLACKLIST = TagKey.of(Registry.ITEM_KEY, new Identifier(ID, "broken_blacklist"));

	// Must load before recipes do, so we can't use the usual fabric route.
	public static final List<ResourceReloader> RECIPE_DEPENDENCY_RELOADERS = List.of(SmithingToolMaterialLoader.INSTANCE, SmithingArmorMaterialLoader.INSTANCE, SmithingUnitCostManager.INSTANCE, SmithingTypeLoader.INSTANCE);

	public static final RecipeSerializer<ShapelessRepairRecipe> SHAPELESS_REPAIR_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "repair"), new ShapelessRepairRecipe.Serializer());
	public static final RecipeSerializer<SacrificeUpgradeRecipe> SACRIFICE_UPGRADE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "sacrifice"), new SacrificeUpgradeRecipe.Serializer());
	public static final RecipeSerializer<SmithingUpgradeRecipe> SMITHING_UPGRADE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "smithing"), new SmithingUpgradeRecipe.Serializer());
	public static final RecipeSerializer<ShapelessUpgradeRecipe> SHAPELESS_UPGRADE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "shapeless"), new ShapelessUpgradeRecipe.Serializer());

	public static void generateSmithingData(Map<Identifier, Recipe<?>> recipes) {
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Generating Smithing Data!");
		TinkerersSmithingLoader.INSTANCE.generateItemSmithingData(recipes);
	}

	public static boolean isBroken(ItemStack stack) {
		return !stack.isIn(BROKEN_BLACKLIST) && stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage() - (stack.getItem() instanceof ElytraItem ? 1 : 0);
	}

	public static boolean isKeeper(ItemStack stack) {
		return !stack.isIn(BROKEN_BLACKLIST) && (stack.hasCustomName() || stack.hasEnchantments() || stack.getItem() instanceof ElytraItem);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("[Tinkerer's Smithing] Initialized.");
	}
}
