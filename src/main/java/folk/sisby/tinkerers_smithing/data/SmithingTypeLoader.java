package folk.sisby.tinkerers_smithing.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class SmithingTypeLoader extends MultiJsonDataLoader implements IdentifiableResourceReloadListener, RecipeDataDependency {
	public static final SmithingTypeLoader INSTANCE = new SmithingTypeLoader(new Gson());
	public static final Identifier ID = new Identifier(TinkerersSmithing.ID, "smithing_type_loader");
	public static final TagGroupLoader<Item> ITEM_TAG_LOADER = new TagGroupLoader<>(Registries.ITEM::getOrEmpty, "tags/items");
	public static final String AVOIDANCE_PREFIX = "tinkerers_smithing_types/";

	public SmithingTypeLoader(Gson gson) {
		super(gson, "smithing_types");
	}

	@Override
	public @NotNull Identifier getFabricId() {
		return ID;
	}

	public static void addToTag(Map<Identifier, Collection<Item>> tags, String path, Item item) {
		Identifier id = new Identifier(path); // minecraft for slot stuff
		HashSet<Item> mutable = new HashSet<>(tags.computeIfAbsent(id, k -> new HashSet<>()));
		mutable.add(item);
		tags.put(id, ImmutableList.copyOf(mutable));
	}

	@Override
	protected void apply(Map<Identifier, Collection<Pair<JsonElement, String>>> prepared, ResourceManager manager, Profiler profiler) {
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Loading Types!");
		TinkerersSmithingLoader.INSTANCE.SMITHING_TYPES.clear();
		Map<Identifier, List<TagGroupLoader.TrackedEntry>> typeTags = new HashMap<>();
		prepared.forEach((id, jsons) -> {
			Identifier collisionAvoidingID = new Identifier(id.getNamespace(), AVOIDANCE_PREFIX + id.getPath());
			jsons.forEach(jsonEntry -> {
				List<TagGroupLoader.TrackedEntry> list = typeTags.computeIfAbsent(collisionAvoidingID, k -> new ArrayList<>());
				TagFile tagFile = TagFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, jsonEntry.getLeft())).getOrThrow(false, TinkerersSmithing.LOGGER::error);
				if (tagFile.replace()) {
					list.clear();
				}
				tagFile.entries().forEach(entry -> list.add(new TagGroupLoader.TrackedEntry(entry, jsonEntry.getRight())));
			});
		});

		Map<Identifier, List<TagGroupLoader.TrackedEntry>> itemTags = ITEM_TAG_LOADER.loadTags(manager);
		Map<Identifier, List<TagGroupLoader.TrackedEntry>> allTags = new HashMap<>();
		allTags.putAll(itemTags);
		allTags.putAll(typeTags);
		Map<Identifier, Collection<Item>> tags = ITEM_TAG_LOADER.buildGroup(allTags);
		tags.entrySet().removeIf(e -> !typeTags.containsKey(e.getKey()));
		// Strip collision avoiding ID
		tags = tags.entrySet().stream().collect(Collectors.toMap(e -> new Identifier(e.getKey().getNamespace(), StringUtils.removeStart(e.getKey().getPath(), AVOIDANCE_PREFIX)), Map.Entry::getValue));
		// Manually jam in stuff by equipment slot, false positives should wash out by having no material.
		for (Item item : Registries.ITEM) {
			if (item instanceof ArmorItem ai) {
				switch (ai.getType()) {
					case BOOTS -> addToTag(tags, "boots", item);
					case LEGGINGS -> addToTag(tags, "leggings", item);
					case CHESTPLATE -> addToTag(tags, "chestplate", item);
					case HELMET -> addToTag(tags, "helmet", item);
				}
			}
		}
		TinkerersSmithingLoader.INSTANCE.SMITHING_TYPES.putAll(tags);
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Reloaded smithing types");
	}
}
