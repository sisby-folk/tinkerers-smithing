package folk.sisby.tinkerers_smithing.recipe;

import com.google.gson.JsonObject;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import static folk.sisby.tinkerers_smithing.TinkerersSmithingLoader.recipeId;

public class SmithingUpgradeRecipe extends SmithingTransformRecipe implements ServerRecipe<SmithingTransformRecipe> {
	public final Item baseItem;
	public final int additionCount;
	public final Item resultItem;

	public SmithingUpgradeRecipe(Item baseItem, Ingredient addition, int additionCount, Item resultItem) {
		super(recipeId("smithing", resultItem, baseItem), Ingredient.empty(), Ingredient.ofItems(baseItem), addition, getPreviewResult(resultItem, additionCount));
		this.baseItem = baseItem;
		this.additionCount = additionCount;
		this.resultItem = resultItem;
	}

	private static ItemStack getPreviewResult(Item resultItem, int additionCount) {
		ItemStack stack = resultItem.getDefaultStack().copy();
		stack.setDamage(resultDamage(resultItem, additionCount, 1));
		return stack;
	}

	public static int resultDamage(Item resultItem, int additionCount, int usedCount) {
		return Math.min(resultItem.getMaxDamage() - 1, (int) Math.floor(resultItem.getMaxDamage() * ((additionCount - usedCount) / 4.0)));
	}

	@Override
	public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
		ItemStack output = super.craft(inventory, registryManager);
		int usedCount = Math.min(additionCount, inventory.getStack(2).getCount());
		output.setDamage(resultDamage(output.getItem(), additionCount, usedCount));
		return output;
	}

	public static class Serializer implements RecipeSerializer<SmithingUpgradeRecipe> {
		public SmithingUpgradeRecipe read(Identifier id, JsonObject json) {
			Item baseItem = JsonHelper.getItem(json, "base");
			Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(json, "addition"));
			int additionCount = JsonHelper.getInt(json, "additionCount");
			Item resultItem = JsonHelper.getItem(json, "result");
			return new SmithingUpgradeRecipe(baseItem, addition, additionCount, resultItem);
		}

		public SmithingUpgradeRecipe read(Identifier id, PacketByteBuf buf) {
			Item baseItem = Item.byRawId(buf.readVarInt());
			Ingredient addition = Ingredient.fromPacket(buf);
			int additionCount = buf.readVarInt();
			Item resultItem = Item.byRawId(buf.readVarInt());
			return new SmithingUpgradeRecipe(baseItem, addition, additionCount, resultItem);
		}

		public void write(PacketByteBuf buf, SmithingUpgradeRecipe recipe) {
			buf.writeVarInt(Item.getRawId(recipe.baseItem));
			recipe.addition.write(buf);
			buf.writeVarInt(recipe.additionCount);
			buf.writeVarInt(Item.getRawId(recipe.resultItem));
		}
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SMITHING_UPGRADE_SERIALIZER;
	}

	@Override
	public @Nullable RecipeSerializer<SmithingTransformRecipe> getFallbackSerializer() {
		return RecipeSerializer.SMITHING_TRANSFORM;
	}
}
