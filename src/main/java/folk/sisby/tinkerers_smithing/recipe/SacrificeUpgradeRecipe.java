package folk.sisby.tinkerers_smithing.recipe;

import com.google.gson.JsonObject;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import static folk.sisby.tinkerers_smithing.TinkerersSmithingLoader.appendId;
import static folk.sisby.tinkerers_smithing.TinkerersSmithingLoader.recipeId;

public class SacrificeUpgradeRecipe extends SmithingRecipe implements ServerRecipe<SmithingRecipe> {
	public final Item baseItem;
	public final int additionUnits;
	public final Item resultItem;
	public final int resultUnits;

	public SacrificeUpgradeRecipe(Item baseItem, Ingredient addition, int additionUnits, Item resultItem, int resultUnits) {
		super(appendId(recipeId("sacrifice", resultItem, baseItem), String.valueOf(additionUnits)), Ingredient.ofItems(baseItem), addition, getPreviewResult(resultItem, additionUnits, resultUnits));
		this.baseItem = baseItem;
		this.additionUnits = additionUnits;
		this.resultItem = resultItem;
		this.resultUnits = resultUnits;
	}

	private static ItemStack getPreviewResult(Item resultItem, int additionUnits, int resultUnits) {
		ItemStack stack = resultItem.getDefaultStack().copy();
		stack.setDamage(resultDamage(resultItem, additionUnits, resultUnits, 0, 1));
		return stack;
	}

	public static int resultDamage(Item resultItem, int additionUnits, int resultUnits, int additionDamage, int additionMaxDamage) {
		return (int) Math.ceil(resultItem.getMaxDamage() - ((additionMaxDamage - additionDamage) * ((double) additionUnits * resultItem.getMaxDamage()) / ((double)additionMaxDamage * resultUnits)));
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		ItemStack output = super.craft(inventory);
		ItemStack addition = inventory.getStack(1);
		output.setDamage(resultDamage(output.getItem(), additionUnits, resultUnits, addition.getDamage(), addition.getMaxDamage()));
		return output;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	public static class Serializer implements RecipeSerializer<SacrificeUpgradeRecipe> {
		public SacrificeUpgradeRecipe read(Identifier id, JsonObject json) {
			Item baseItem = JsonHelper.getItem(json, "base");
			Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(json, "addition"));
			int additionUnits = JsonHelper.getInt(json, "additionUnits");
			Item resultItem = JsonHelper.getItem(json, "result");
			int resultUnits = JsonHelper.getInt(json, "resultUnits");
			return new SacrificeUpgradeRecipe(baseItem, addition, additionUnits, resultItem, resultUnits);
		}

		public SacrificeUpgradeRecipe read(Identifier id, PacketByteBuf buf) {
			Item baseItem = Item.byRawId(buf.readVarInt());
			Ingredient addition = Ingredient.fromPacket(buf);
			int additionUnits = buf.readVarInt();
			Item resultItem = Item.byRawId(buf.readVarInt());
			int resultUnits = buf.readVarInt();
			return new SacrificeUpgradeRecipe(baseItem, addition, additionUnits, resultItem, resultUnits);
		}

		public void write(PacketByteBuf buf, SacrificeUpgradeRecipe recipe) {
			buf.writeVarInt(Item.getRawId(recipe.baseItem));
			recipe.addition.write(buf);
			buf.writeVarInt(recipe.additionUnits);
			buf.writeVarInt(Item.getRawId(recipe.resultItem));
			buf.writeVarInt(recipe.resultUnits);
		}
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SACRIFICE_UPGRADE_SERIALIZER;
	}

	@Override
	public @Nullable RecipeSerializer<SmithingRecipe> getFallbackSerializer() {
		return RecipeSerializer.SMITHING;
	}
}
