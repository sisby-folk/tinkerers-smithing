package folk.sisby.tinkerers_smithing;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueMap;
import net.minecraft.recipe.Ingredient;

import java.util.Map;

public class TinkerersSmithingConfig extends WrappedConfig {
	@Comment("Maps items to equivalent tags that other mods substitute in crafting recipes")
	public final Map<String, String> ingredientSubstitutions = ValueMap.builder("")
		.put("minecraft:gold_ingot", "forge:ingots/gold")
		.put("minecraft:gold_nugget", "forge:nuggets/gold")
		.put("minecraft:iron_ingot", "forge:ingots/iron")
		.put("minecraft:iron_nugget", "forge:nuggets/iron")
		.put("minecraft:netherite_ingot", "forge:ingots/netherite")
		.put("minecraft:copper_ingot", "forge:ingots/copper")
		.put("minecraft:amethyst_shard", "forge:gems/amethyst")
		.put("minecraft:diamond", "forge:gems/diamond")
		.put("minecraft:emerald", "forge:gems/emerald")
		.put("minecraft:chest", "forge:chests/wooden")
		.put("minecraft:cobblestone", "forge:cobblestone/normal")
		.put("minecraft:cobbled_deepslate", "forge:cobblestone/deepslate")
		.put("minecraft:string", "forge:string")
		.build();

	public final boolean matchesOrEquivalent(Ingredient fromRepair, Ingredient fromCrafting) {
		String transformedJsonString = fromRepair.toJson().toString();
		if (transformedJsonString.equals(fromCrafting.toJson().toString())) return true;
		for (Map.Entry<String, String> substitution : ingredientSubstitutions.entrySet()) {
			transformedJsonString = transformedJsonString.replace("{\"item\":\"%s\"}".formatted(substitution.getKey()), "{\"tag\":\"%s\"}".formatted(substitution.getValue()));
		}
		return transformedJsonString.equals(fromCrafting.toJson().toString());
	}
}
