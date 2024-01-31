package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SimpleResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(SimpleResourceReload.class)
public class SimpleResourceReloadMixin {
	@ModifyVariable(method = "start", at = @At("HEAD"), index = 1, argsOnly = true)
	private static List<ResourceReloader> addRecipeDataDependencies(List<ResourceReloader> original) {
		List<ResourceReloader> reloaders = new ArrayList<>(original);
		List<ResourceReloader> toAdd = new ArrayList<>(TinkerersSmithing.RECIPE_DEPENDENCY_RELOADERS);
		reloaders.removeAll(toAdd);
		for (int i = 0; i < reloaders.size(); i++) {
			if (reloaders.get(i) instanceof RecipeManager) {
				reloaders.addAll(i, toAdd);
				break;
			}
		}
		return reloaders;
	}
}
