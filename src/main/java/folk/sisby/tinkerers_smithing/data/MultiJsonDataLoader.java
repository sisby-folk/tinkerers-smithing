package folk.sisby.tinkerers_smithing.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public abstract class MultiJsonDataLoader extends SinglePreparationResourceReloader<Map<Identifier, Collection<JsonElement>>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String FILE_SUFFIX = ".json";
	private final Gson gson;
	private final String dataType;

	public MultiJsonDataLoader(Gson gson, String dataType) {
		this.gson = gson;
		this.dataType = dataType;
	}

	@Override
	protected Map<Identifier, Collection<JsonElement>> prepare(ResourceManager manager, Profiler profiler) {
		Map<Identifier, Collection<JsonElement>> outMap = Maps.newHashMap();

		for(Map.Entry<Identifier, Resource> entry : manager.findResources(this.dataType, id -> id.getPath().endsWith(".json")).entrySet()) {
			Identifier fileId = entry.getKey();
			// Remove .json, ignore path prefixes: minecraft:campanion/diamond = minecraft:diamond
			Identifier id = new Identifier(fileId.getNamespace(), StringUtils.removeEndIgnoreCase(fileId.getPath().substring(fileId.getPath().lastIndexOf('/') + 1), FILE_SUFFIX));

			try {
				try (Reader reader = entry.getValue().openBufferedReader()) {
					JsonElement jsonContents = JsonHelper.deserialize(this.gson, reader, JsonElement.class);
					outMap.computeIfAbsent(id, k -> new ArrayList<>()).add(jsonContents);
				}
			} catch (IllegalArgumentException | IOException | JsonParseException e) {
				LOGGER.error("Couldn't parse data file {} from {}", id, fileId, e);
			}
		}

		return outMap;
	}
}
