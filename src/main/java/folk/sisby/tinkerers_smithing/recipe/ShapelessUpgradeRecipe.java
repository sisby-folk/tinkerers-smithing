package folk.sisby.tinkerers_smithing.recipe;

import com.google.gson.JsonObject;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShapelessUpgradeRecipe extends ShapelessRecipe implements ServerRecipe {
	public ShapelessUpgradeRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input) {
		super(id, group, output, input);
	}

	public Ingredient getBaseIngredient() {
		return getIngredients().get(0);
	}

	public List<Ingredient> getAdditionalIngredients() {
		return getIngredients().subList(1, getIngredients().size());
	}

	@Override
	public ItemStack craft(CraftingInventory inventory) {
		ItemStack output = super.craft(inventory);
		for(int i = 0; i < inventory.size(); ++i) {
			ItemStack stack = inventory.getStack(i);
			if (getBaseIngredient().test(stack)) {
				output.setNbt(stack.getOrCreateNbt().copy());
			}
		}
		return output;
	}

	public static class Serializer implements RecipeSerializer<ShapelessUpgradeRecipe> {
		public ShapelessUpgradeRecipe read(Identifier id, JsonObject json) {
			ShapelessRecipe recipe = RecipeSerializer.SHAPELESS.read(id, json);
			return new ShapelessUpgradeRecipe(id, recipe.getGroup(), recipe.getOutput(), recipe.getIngredients());
		}

		public ShapelessUpgradeRecipe read(Identifier id, PacketByteBuf buf) {
			ShapelessRecipe recipe = RecipeSerializer.SHAPELESS.read(id, buf);
			return new ShapelessUpgradeRecipe(id, recipe.getGroup(), recipe.getOutput(), recipe.getIngredients());
		}

		public void write(PacketByteBuf buf, ShapelessUpgradeRecipe recipe) {
			RecipeSerializer.SHAPELESS.write(buf, recipe);
		}
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SHAPELESS_UPGRADE_SERIALIZER;
	}

	@Override
	public @Nullable RecipeSerializer<?> getFallbackSerializer() {
		return RecipeSerializer.SHAPELESS;
	}
}
