package folk.sisby.tinkerers_smithing.mixin;

import com.google.common.collect.ImmutableMap;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.TinkerersSmithingLoader;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
	@Unique private ImmutableMap.Builder<Identifier, Recipe<?>> builder = null;

	@ModifyVariable(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0), index = 5)
	private ImmutableMap.Builder<Identifier, Recipe<?>> AddRuntimeRecipes(ImmutableMap.Builder<Identifier, Recipe<?>> builder) {
		this.builder = builder;
		return builder;
	}

	@ModifyVariable(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 1), ordinal = 1)
	private Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> AddRuntimeRecipes(Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> recipes) {
		Map<Identifier, Recipe<?>> dataRecipes = builder.build();
		TinkerersSmithing.generateSmithingData(dataRecipes);
		TinkerersSmithingLoader.INSTANCE.RECIPES.forEach(recipe -> {
			if (!dataRecipes.containsKey(recipe.getId())) {
				recipes.computeIfAbsent(recipe.getType(), recipeType -> ImmutableMap.builder()).put(recipe.getId(), recipe);
				builder.put(recipe.getId(), recipe);
			}
		});
		TinkerersSmithing.LOGGER.info("[Tinkerer's Smithing] Added {} runtime recipes!", TinkerersSmithingLoader.INSTANCE.RECIPES.size());
		return recipes;
	}
}
