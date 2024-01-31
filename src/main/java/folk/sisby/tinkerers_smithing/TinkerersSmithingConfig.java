package folk.sisby.tinkerers_smithing;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueMap;

import java.util.Map;

public class TinkerersSmithingConfig extends WrappedConfig {
	@Comment("Maps ingredient JSON to their equivalents substituted in recipes by other mods")
	public final Map<String, String> ingredientSubstitutions = ValueMap.builder("")
		.put("{\"item\":\"minecraft:gold_ingot\"}", "{\"tag\":\"forge:ingots/gold\"}")
		.build();
}
