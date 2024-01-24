package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.data.RecipeDataDependency;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.minecraft.resource.ResourceReloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ResourceManagerHelperImpl.class)
public class ResourceManagerHelperImplMixin {
	@ModifyVariable(method = "sort(Ljava/util/List;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"), ordinal = 1, remap = false)
	public List<IdentifiableResourceReloadListener> sortRecipeDataDependencies(List<IdentifiableResourceReloadListener> fabricListeners, List<ResourceReloader> vanillaListeners) {
		List<IdentifiableResourceReloadListener> recipeDependencyListeners = new ArrayList<>(fabricListeners.stream().filter(r -> r instanceof RecipeDataDependency).toList());
		fabricListeners.removeAll(recipeDependencyListeners);
		for (int i = 0; i < vanillaListeners.size(); i++) {
			if (vanillaListeners.get(i) instanceof IdentifiableResourceReloadListener irrl && irrl.getFabricId() == ResourceReloadListenerKeys.RECIPES) {
				vanillaListeners.addAll(i, recipeDependencyListeners);
				break;
			}
		}
		return fabricListeners;
	}
}
