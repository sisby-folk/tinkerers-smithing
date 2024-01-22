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

public class SacrificeUpgradeRecipe extends SmithingRecipe implements ServerRecipe {
	public final int additionUnits;
	public final int resultUnits;

	public SacrificeUpgradeRecipe(Identifier id, Ingredient base, Ingredient addition, int additionUnits, ItemStack result, int resultUnits) {
		super(id, base, addition, result);
		this.additionUnits = additionUnits;
		this.resultUnits = resultUnits;
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		ItemStack output = super.craft(inventory);
		ItemStack addition = inventory.getStack(1);
		int damage = (int) Math.ceil(output.getMaxDamage() - ((addition.getMaxDamage() - addition.getDamage()) * ((double) additionUnits * output.getMaxDamage()) / ((double)addition.getMaxDamage() * resultUnits)));
		output.setDamage(damage);
		return output;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	public static class Serializer implements RecipeSerializer<SacrificeUpgradeRecipe> {
		public SacrificeUpgradeRecipe read(Identifier id, JsonObject json) {
			SmithingRecipe recipe = RecipeSerializer.SMITHING.read(id, json);
			int additionUnits = JsonHelper.getInt(json, "additionUnits");
			int resultUnits = JsonHelper.getInt(json, "resultUnits");
			return new SacrificeUpgradeRecipe(id, recipe.base, recipe.addition, additionUnits, recipe.getOutput(), resultUnits);
		}

		public SacrificeUpgradeRecipe read(Identifier id, PacketByteBuf buf) {
			SmithingRecipe recipe = RecipeSerializer.SMITHING.read(id, buf);
			int additionUnits = buf.readVarInt();
			int resultUnits = buf.readVarInt();
			return new SacrificeUpgradeRecipe(id, recipe.base, recipe.addition, additionUnits, recipe.getOutput(), resultUnits);
		}

		public void write(PacketByteBuf buf, SacrificeUpgradeRecipe recipe) {
			RecipeSerializer.SMITHING.write(buf, recipe);
			buf.writeVarInt(recipe.additionUnits);
			buf.writeVarInt(recipe.resultUnits);
		}
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SACRIFICE_UPGRADE_SERIALIZER;
	}

	@Override
	public @Nullable RecipeSerializer<?> getFallbackSerializer() {
		return RecipeSerializer.SMITHING;
	}
}
