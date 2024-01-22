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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShapelessRepairRecipe extends ShapelessRecipe implements ServerRecipe {
	public ShapelessRepairRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input) {
		super(id, group, output, input);
	}

	public Ingredient getBaseIngredient() {
		return getIngredients().get(0);
	}

	public Ingredient getUnit() {
		return getIngredients().get(1);
	}

	public int getUnitCost() {
		return getIngredients().size() - 1;
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		ItemStack base = null;
		int units = 0;
		for(int i = 0; i < inventory.size(); ++i) {
			ItemStack stack = inventory.getStack(i);
			if (getBaseIngredient().test(stack)) {
				if (base != null || stack.hasEnchantments()) return false;
				base = stack;
			} else if (getUnit().test(stack)) {
				units++;
			}
		}
		if (base != null && units > 0 && units <= getUnitCost()) {
			return base.getDamage() - ((int) Math.ceil((base.getMaxDamage() * (units - 1)) / (double) getUnitCost())) > 0;
		}
		return false;
	}

	@Override
	public ItemStack craft(CraftingInventory inventory) {
		ItemStack output = super.craft(inventory);
		ItemStack base = null;
		int units = 0;
		for(int i = 0; i < inventory.size(); ++i) {
			ItemStack stack = inventory.getStack(i);
			if (getBaseIngredient().test(stack)) {
				base = stack;
			} else if (getUnit().test(stack)) {
				units++;
			}
		}
		if (base != null) {
			output.setDamage(Math.max(0, base.getDamage() - ((int) Math.ceil((base.getMaxDamage() * units) / (double) getUnitCost()))));
		}
		return output;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	public static class Serializer implements RecipeSerializer<ShapelessRepairRecipe> {
		public ShapelessRepairRecipe read(Identifier id, JsonObject json) {
			ShapelessRecipe recipe = RecipeSerializer.SHAPELESS.read(id, json);
			return new ShapelessRepairRecipe(id, recipe.getGroup(), recipe.getOutput(), recipe.getIngredients());
		}

		public ShapelessRepairRecipe read(Identifier id, PacketByteBuf buf) {
			ShapelessRecipe recipe = RecipeSerializer.SHAPELESS.read(id, buf);
			return new ShapelessRepairRecipe(id, recipe.getGroup(), recipe.getOutput(), recipe.getIngredients());
		}

		public void write(PacketByteBuf buf, ShapelessRepairRecipe recipe) {
			RecipeSerializer.SHAPELESS.write(buf, recipe);
		}
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return TinkerersSmithing.SHAPELESS_REPAIR_SERIALIZER;
	}

	@Override
	public @Nullable RecipeSerializer<?> getFallbackSerializer() {
		return RecipeSerializer.SHAPELESS;
	}
}
