package folk.sisby.tinkerers_smithing.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class GenericSpecialRecipeSerializer<T extends Recipe<Inventory>> implements RecipeSerializer<T> {
	private final Factory<T> factory;

	public GenericSpecialRecipeSerializer(Factory<T> factory) {
		this.factory = factory;
	}

	public T read(Identifier identifier, JsonObject jsonObject) {
		return this.factory.create(identifier);
	}

	public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
		return this.factory.create(identifier);
	}

	public void write(PacketByteBuf packetByteBuf, T craftingRecipe) {
	}

	@FunctionalInterface
	public interface Factory<T extends Recipe<Inventory>> {
		T create(Identifier identifier);
	}
}
