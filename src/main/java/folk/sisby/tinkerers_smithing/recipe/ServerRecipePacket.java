package folk.sisby.tinkerers_smithing.recipe;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.registry.Registry;

public interface ServerRecipePacket<T extends Packet<?>> {
	T tinkerersSmithing$withFallback();

	static <T extends F, F extends Recipe<?>> void writeRecipeWithFallback(PacketByteBuf buf, T recipe) {
		if (recipe instanceof ServerRecipe<?> sr) {
			RecipeSerializer<F> serializer = (RecipeSerializer<F>) sr.getFallbackSerializer();
			buf.writeIdentifier(Registry.RECIPE_SERIALIZER.getId(serializer));
			buf.writeIdentifier(recipe.getId());
			serializer.write(buf, recipe);
		} else {
			SynchronizeRecipesS2CPacket.writeRecipe(buf, recipe);
		}
	}
}
