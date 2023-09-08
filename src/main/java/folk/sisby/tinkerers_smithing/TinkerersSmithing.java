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
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class TinkerersSmithing implements ModInitializer {
	public static final String ID = "tinkerers_smithing";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final TagKey<Item> DEWORK_INGREDIENTS = TagKey.of(Registry.ITEM_KEY, new Identifier(ID, "dework_ingredients"));
	public static final SpecialRecipeSerializer<ShapelessRepairRecipe> SHAPELESS_REPAIR_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_shapeless_repair"), new SpecialRecipeSerializer<>(ShapelessRepairRecipe::new));
	public static final SpecialRecipeSerializer<ShapelessUpgradeRecipe> SHAPELESS_UPGRADE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_shapeless_upgrade"), new SpecialRecipeSerializer<>(ShapelessUpgradeRecipe::new));
	public static final SpecialRecipeSerializer<SmithingUpgradeRecipe> SMITHING_UPGRADE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_smithing_upgrade"), new SpecialRecipeSerializer<>(SmithingUpgradeRecipe::new));
	public static final SpecialRecipeSerializer<SacrificeUpgradeRecipe> SACRIFICE_UPGRADE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_sacrifice_upgrade"), new SpecialRecipeSerializer<>(SacrificeUpgradeRecipe::new));

	public static @Nullable PacketByteBuf SMITHING_RELOAD_BUF = null;

	private static void generateSmithingData(MinecraftServer server) {
		if (server != null) {
			TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Generating Smithing Data!");
			TinkerersSmithingLoader.INSTANCE.generateItemSmithingData(server);
			SMITHING_RELOAD_BUF = TinkerersSmithingNetworking.createSmithingReloadBuf();
			TinkerersSmithingNetworking.smithingReload(server, SMITHING_RELOAD_BUF);
		}
	}

	public static boolean isBroken(ItemStack stack) {
		return stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage() - (stack.getItem() instanceof ElytraItem ? 1 : 0);
	}

	public static boolean isKeeper(ItemStack stack) {
		return stack.hasCustomName() || stack.hasEnchantments() || stack.getItem() instanceof ElytraItem;
	}

	@Override
	public void onInitialize() {
		ServerPlayConnectionEvents.JOIN.register((TinkerersSmithingNetworking::onPlayReady));
		ServerLifecycleEvents.SERVER_STARTED.register(TinkerersSmithing::generateSmithingData);
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, throwable) -> TinkerersSmithing.generateSmithingData(server));
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SmithingToolMaterialLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SmithingArmorMaterialLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SmithingUnitCostManager.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SmithingTypeLoader.INSTANCE);

		LOGGER.info("[Tinkerer's Smithing] Initialized.");
	}
}
