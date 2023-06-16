package folk.sisby.tinkerers_smithing;

import folk.sisby.tinkerers_smithing.data.SmithingArmorMaterialLoader;
import folk.sisby.tinkerers_smithing.data.SmithingToolMaterialLoader;
import folk.sisby.tinkerers_smithing.data.SmithingTypeLoader;
import folk.sisby.tinkerers_smithing.data.SmithingUnitCostManager;
import folk.sisby.tinkerers_smithing.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourceLoaderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinkerersSmithing implements ModInitializer {
	public static final String ID = "tinkerers_smithing";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final TagKey<Item> DEWORK_INGREDIENTS = TagKey.of(RegistryKeys.ITEM, new Identifier(ID, "dework_ingredients"));
	public static final SpecialRecipeSerializer<ShapelessRepairRecipe> SHAPELESS_REPAIR_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_shapeless_repair"), new SpecialRecipeSerializer<>(ShapelessRepairRecipe::new));
	public static final SpecialRecipeSerializer<ShapelessUpgradeRecipe> SHAPELESS_UPGRADE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_shapeless_upgrade"), new SpecialRecipeSerializer<>(ShapelessUpgradeRecipe::new));
	public static final GenericSpecialRecipeSerializer<SmithingUpgradeRecipe> SMITHING_UPGRADE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_smithing_upgrade"), new GenericSpecialRecipeSerializer<>(SmithingUpgradeRecipe::new));
	public static final GenericSpecialRecipeSerializer<SacrificeUpgradeRecipe> SACRIFICE_UPGRADE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ID, "crafting_special_sacrifice_upgrade"), new GenericSpecialRecipeSerializer<>(SacrificeUpgradeRecipe::new));

	// Discarded and picked up each reload. Probably shouldn't be accessed outside of reload time.
	private static TinkerersSmithingLoader LOADER_INSTANCE = null;

	public static TinkerersSmithingLoader getLoaderInstance() {
		return LOADER_INSTANCE;
	}

	public static @Nullable PacketByteBuf SMITHING_RELOAD_BUF = null;

	private static void generateSmithingData(MinecraftServer server) {
		if (server != null) {
			getLoaderInstance().generateItemSmithingData(server);
			LOADER_INSTANCE = null;
			SMITHING_RELOAD_BUF = TinkerersSmithingNetworking.createSmithingReloadBuf();
			TinkerersSmithingNetworking.smithingReload(server, SMITHING_RELOAD_BUF);
		}
	}

	private static void resetLoader() {
		SMITHING_RELOAD_BUF = null;
		LOADER_INSTANCE = new TinkerersSmithingLoader();
	}

	@Override
	public void onInitialize(ModContainer mod) {
		ResourceLoaderEvents.START_DATA_PACK_RELOAD.register((context) -> TinkerersSmithing.resetLoader());
		ServerLifecycleEvents.READY.register(TinkerersSmithing::generateSmithingData);
		ResourceLoaderEvents.END_DATA_PACK_RELOAD.register((context) -> TinkerersSmithing.generateSmithingData(context.server()));
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(SmithingToolMaterialLoader.INSTANCE);
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(SmithingArmorMaterialLoader.INSTANCE);
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(SmithingUnitCostManager.INSTANCE);
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(SmithingTypeLoader.INSTANCE);

		LOGGER.info("[Tinkerer's Smithing] Initialized.");
	}
}
