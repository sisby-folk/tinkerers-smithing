package folk.sisby.tinkerers_smithing.recipe;

import com.google.gson.JsonObject;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import static folk.sisby.tinkerers_smithing.TinkerersSmithingLoader.recipeId;

public class ShapelessUpgradeRecipe extends ShapelessRecipe implements ServerRecipe {
	public final Item baseItem;
	public final Ingredient addition;
	public final int additionCount;
	public final Item resultItem;

	public ShapelessUpgradeRecipe(Item baseItem, Ingredient addition, Integer additionCount, Item resultItem) {
		super(recipeId("shapeless", resultItem, baseItem), "", resultItem.getDefaultStack(), assembleIngredients(baseItem, addition, additionCount));
		this.baseItem = baseItem;
		this.addition = addition;
		this.additionCount = additionCount;
		this.resultItem = resultItem;
	}

	private static DefaultedList<Ingredient> assembleIngredients(Item item, Ingredient addition, int additionCount) {
		DefaultedList<Ingredient> ingredients = DefaultedList.of();
		ingredients.add(Ingredient.ofItems(item));
		for (int i = 0; i < additionCount; i++) {
			ingredients.add(addition);
		}
		return ingredients;
	}

	@Override
	public ItemStack craft(CraftingInventory inventory) {
		ItemStack output = super.craft(inventory);
		for (ItemStack stack : inventory.stacks) {
			if (stack.isOf(baseItem)) {
				output.setNbt(stack.getOrCreateNbt().copy());
			}
		}
		return output;
	}

	public static class Serializer implements RecipeSerializer<ShapelessUpgradeRecipe> {
		public ShapelessUpgradeRecipe read(Identifier id, JsonObject json) {
			Item baseItem = JsonHelper.getItem(json, "base");
			Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(json, "addition"));
			int additionCount = JsonHelper.getInt(json, "additionCount");
			Item resultItem = JsonHelper.getItem(json, "result");
			return new ShapelessUpgradeRecipe(baseItem, addition, additionCount, resultItem);
		}

		public ShapelessUpgradeRecipe read(Identifier id, PacketByteBuf buf) {
			Item baseItem = Item.byRawId(buf.readVarInt());
			Ingredient addition = Ingredient.fromPacket(buf);
			int additionCount = buf.readVarInt();
			Item resultItem = Item.byRawId(buf.readVarInt());
			return new ShapelessUpgradeRecipe(baseItem, addition, additionCount, resultItem);
		}

		public void write(PacketByteBuf buf, ShapelessUpgradeRecipe recipe) {
			buf.writeVarInt(Item.getRawId(recipe.baseItem));
			recipe.addition.write(buf);
			buf.writeVarInt(recipe.additionCount);
			buf.writeVarInt(Item.getRawId(recipe.resultItem));
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
