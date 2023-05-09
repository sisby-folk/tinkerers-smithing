package folk.sisby.tinkerers_smithing.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

import java.util.*;

public class SmithingTypeLoader extends JsonDataLoader implements IdentifiableResourceReloader {
	public static final SmithingTypeLoader INSTANCE = new SmithingTypeLoader(new Gson());
	public static final Identifier ID = new Identifier(TinkerersSmithing.ID, "smithing_type_loader");
	public static final TagGroupLoader<Item> itemTagLoader = new TagGroupLoader<>(Registry.ITEM::getOrEmpty, "tags/items");
	public static final TagGroupLoader<Item> tagLoader = new TagGroupLoader<>(Registry.ITEM::getOrEmpty, "smithing_types");

	public SmithingTypeLoader(Gson gson) {
		super(gson, "smithing_types");
	}

	@Override
	public @NotNull Identifier getQuiltId() {
		return ID;
	}

	public static void addToTag(Map<Identifier, Collection<Item>> tags, String path, Item item) {
		Identifier id = new Identifier(TinkerersSmithing.ID, path);
		HashSet<Item> mutable = new HashSet<>(tags.computeIfAbsent(id, k -> new HashSet<>()));
		mutable.add(item);
		tags.put(id, ImmutableList.copyOf(mutable));
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		Map<Identifier, List<TagGroupLoader.EntryWithSource>> itemTags = itemTagLoader.loadTags(manager);
		Map<Identifier, List<TagGroupLoader.EntryWithSource>> typeTags = tagLoader.loadTags(manager);
		Map<Identifier, List<TagGroupLoader.EntryWithSource>> allTags = new HashMap<>();
		allTags.putAll(itemTags);
		allTags.putAll(typeTags);
		Map<Identifier, Collection<Item>> tags = tagLoader.build(allTags);
		tags.entrySet().removeIf(e -> !typeTags.containsKey(e.getKey()));
		// Manually jam in stuff by equipment slot, false positives should wash out by having no material.
		Registry.ITEM.forEach(item -> {
			if (item instanceof ArmorItem ai) {
				switch (ai.getSlotType()) {
					case FEET ->  addToTag(tags, "boots", item);
					case LEGS -> addToTag(tags, "leggings", item);
					case CHEST -> addToTag(tags, "chestplate", item);
					case HEAD -> addToTag(tags, "helmet", item);
				}
			}
		});
		TinkerersSmithing.SMITHING_TYPES.clear();
		TinkerersSmithing.SMITHING_TYPES.putAll(tags);
	}
}
