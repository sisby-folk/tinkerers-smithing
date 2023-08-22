package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.recipe.ServerRecipe;
import net.minecraft.network.packet.s2c.play.RecipeSynchronizationS2CPacket;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(RecipeSynchronizationS2CPacket.class)
public class SynchronizeRecipesS2CPacketMixin {
	@Shadow @Final private List<Recipe<?>> recipes;

	@Inject(method = "<init>(Ljava/util/Collection;)V", at = @At("RETURN"))
	public void onCreated(Collection<Recipe<?>> inRecipes, CallbackInfo ci) {
		recipes.removeIf(recipe -> recipe instanceof ServerRecipe);
	}
}
