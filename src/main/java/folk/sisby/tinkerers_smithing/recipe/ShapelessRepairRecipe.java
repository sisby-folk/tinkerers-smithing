package folk.sisby.tinkerers_smithing.recipe;

import com.google.gson.JsonObject;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static folk.sisby.tinkerers_smithing.TinkerersSmithingLoader.repairRecipeId;

public class ShapelessRepairRecipe extends ShapelessRecipe implements ServerRecipe<ShapelessRecipe> {
	public final Item baseItem;
	public final Ingredient addition;
	public final int additionCount;

	public ShapelessRepairRecipe(Item baseItem, Ingredient addition, int additionCount) {
		super(repairRecipeId(baseItem, addition), "", CraftingRecipeCategory.EQUIPMENT, getPreviewResult(baseItem), assembleIngredients(baseItem, addition, additionCount));
		this.baseItem = baseItem;
		this.addition = addition;
		this.additionCount = additionCount;
	}

	private static ItemStack getPreviewResult(Item baseItem) {
		ItemStack stack = baseItem.getDefaultStack().copy();
		stack.setDamage(1); // Helps signal to vanilla players that the recipe is durability related.
		return stack;
	}

	private static DefaultedList<Ingredient> assembleIngredients(Item item, Ingredient addition, int additionCount) {
		DefaultedList<Ingredient> ingredients = DefaultedList.of();
		Ingredient additionWithAir = Ingredient.ofEntries(Arrays.stream(ArrayUtils.addAll(addition.entries, Ingredient.ofItems(Items.AIR).entries)));
		ingredients.add(Ingredient.ofItems(item));
		for (int i = 0; i < additionCount; i++) {
			ingredients.add(i > 0 ? additionWithAir : addition);
		}
		return ingredients;
	}

	private ItemStack findBase(CraftingInventory inventory) {
		List<ItemStack> bases = inventory.stacks.stream().filter(s -> s.isOf(baseItem)).toList();
		return bases.size() == 1 && !bases.get(0).hasEnchantments() && bases.get(0).isDamaged() ? bases.get(0) : null;
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		ItemStack base = findBase(inventory);
		long units = inventory.stacks.stream().filter(addition).count();
        if (base == null || units <= 0 || units > additionCount) return false;

        return base.getDamage() - ((int) Math.ceil((base.getMaxDamage() * (units - 1)) / (double) additionCount)) > 0;
    }

	@Override
	public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
		ItemStack base = findBase(inventory);
		long units = inventory.stacks.stream().filter(addition).count();
		if (base == null || units <= 0 || units > additionCount) return ItemStack.EMPTY;

		ItemStack output = super.craft(inventory, registryManager);
		output.setNbt(base.getOrCreateNbt().copy());
		output.setDamage(Math.max(0, base.getDamage() - ((int) Math.ceil((base.getMaxDamage() * units) / (double) additionCount))));
		return output;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	public static class Serializer implements RecipeSerializer<ShapelessRepairRecipe> {
		public ShapelessRepairRecipe read(Identifier id, JsonObject json) {
			Item baseItem = JsonHelper.getItem(json, "base");
			Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(json, "addition"));
			int additionCount = JsonHelper.getInt(json, "additionCount");
			return new ShapelessRepairRecipe(baseItem, addition, additionCount);
		}

		public ShapelessRepairRecipe read(Identifier id, PacketByteBuf buf) {
			Item baseItem = Item.byRawId(buf.readVarInt());
			Ingredient addition = Ingredient.fromPacket(buf);
			int additionCount = buf.readVarInt();
			return new ShapelessRepairRecipe(baseItem, addition, additionCount);
		}

		public void write(PacketByteBuf buf, ShapelessRepairRecipe recipe) {
			buf.writeVarInt(Item.getRawId(recipe.baseItem));
			if (recipe.addition.getMatchingStacks().length == 0) TinkerersSmithing.LOGGER.error("No matching stacks while serializing repair recipe {}!", recipe.getId());
			recipe.addition.write(buf);
			buf.writeVarInt(recipe.additionCount);
		}
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SHAPELESS_REPAIR_SERIALIZER;
	}

	@Override
	public @Nullable RecipeSerializer<ShapelessRecipe> getFallbackSerializer() {
		return RecipeSerializer.SHAPELESS;
	}
}
