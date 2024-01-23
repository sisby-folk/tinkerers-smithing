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

import static folk.sisby.tinkerers_smithing.TinkerersSmithingLoader.recipeId;

public class SacrificeUpgradeRecipe extends SmithingRecipe implements ServerRecipe<SmithingRecipe> {
	public final Item baseItem;
	private final Item additionItem;
	public final int additionUnits;
	public final Item resultItem;
	public final int resultUnits;

	public SacrificeUpgradeRecipe(Item baseItem, Item additionItem, Integer additionUnits, Item resultItem, int resultUnits) {
		super(recipeId("sacrifice", resultItem, baseItem, additionItem), Ingredient.ofItems(baseItem), Ingredient.ofItems(additionItem), resultItem.getDefaultStack());
		this.baseItem = baseItem;
		this.additionItem = additionItem;
		this.additionUnits = additionUnits;
		this.resultItem = resultItem;
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
			Item baseItem = JsonHelper.getItem(json, "base");
			Item additionItem = JsonHelper.getItem(json, "addition");
			int additionUnits = JsonHelper.getInt(json, "additionUnits");
			Item resultItem = JsonHelper.getItem(json, "result");
			int resultUnits = JsonHelper.getInt(json, "resultUnits");
			return new SacrificeUpgradeRecipe(baseItem, additionItem, additionUnits, resultItem, resultUnits);
		}

		public SacrificeUpgradeRecipe read(Identifier id, PacketByteBuf buf) {
			Item baseItem = Item.byRawId(buf.readVarInt());
			Item additionItem = Item.byRawId(buf.readVarInt());
			int additionUnits = buf.readVarInt();
			Item resultItem = Item.byRawId(buf.readVarInt());
			int resultUnits = buf.readVarInt();
			return new SacrificeUpgradeRecipe(baseItem, additionItem, additionUnits, resultItem, resultUnits);
		}

		public void write(PacketByteBuf buf, SacrificeUpgradeRecipe recipe) {
			buf.writeVarInt(Item.getRawId(recipe.baseItem));
			buf.writeVarInt(Item.getRawId(recipe.additionItem));
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
