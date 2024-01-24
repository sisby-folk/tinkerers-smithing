package folk.sisby.tinkerers_smithing.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public interface ServerRecipe<T extends Recipe<?>> {
	default @Nullable RecipeSerializer<T> getFallbackSerializer() {
		return null;
	}
}
