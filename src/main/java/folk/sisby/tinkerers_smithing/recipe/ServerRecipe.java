package folk.sisby.tinkerers_smithing.recipe;

import net.minecraft.recipe.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public interface ServerRecipe {
	default @Nullable RecipeSerializer<?> getFallbackSerializer() {
		return null;
	}
}
