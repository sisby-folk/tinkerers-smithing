package folk.sisby.tinkerers_smithing.recipe;

import com.google.gson.JsonObject;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class SmithingUpgradeRecipe extends SmithingRecipe implements ServerRecipe {
	public final int additionCount;

	public SmithingUpgradeRecipe(Identifier id, Ingredient base, Ingredient addition, int additionCount, ItemStack result) {
		super(id, base, addition, result);
		this.additionCount = additionCount;
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		ItemStack output = super.craft(inventory);
		int usedCount = Math.min(additionCount, inventory.getStack(1).getCount());
		output.setDamage(Math.min(output.getMaxDamage() - 1, (int) Math.floor(output.getMaxDamage() * ((additionCount - usedCount) / 4.0))));
		return output;
	}

	public static class Serializer implements RecipeSerializer<SmithingUpgradeRecipe> {
		public SmithingUpgradeRecipe read(Identifier id, JsonObject json) {
			SmithingRecipe recipe = RecipeSerializer.SMITHING.read(id, json);
			int additionCount = JsonHelper.getInt(json, "additionCount");
			return new SmithingUpgradeRecipe(id, recipe.base, recipe.addition, additionCount, recipe.getOutput());
		}

		public SmithingUpgradeRecipe read(Identifier id, PacketByteBuf buf) {
			SmithingRecipe recipe = RecipeSerializer.SMITHING.read(id, buf);
			int additionCount = buf.readVarInt();
			return new SmithingUpgradeRecipe(id, recipe.base, recipe.addition, additionCount, recipe.getOutput());
		}

		public void write(PacketByteBuf buf, SmithingUpgradeRecipe recipe) {
			RecipeSerializer.SMITHING.write(buf, recipe);
			buf.writeVarInt(recipe.additionCount);
		}
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SMITHING_UPGRADE_SERIALIZER;
	}

	@Override
	public @Nullable RecipeSerializer<?> getFallbackSerializer() {
		return RecipeSerializer.SMITHING;
	}
}
