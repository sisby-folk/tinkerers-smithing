package folk.sisby.tinkerers_smithing;

import de.siphalor.nbtcrafting.NbtCrafting;
import de.siphalor.nbtcrafting.ingredient.IIngredient;
import de.siphalor.nbtcrafting.ingredient.IngredientEntry;
import de.siphalor.nbtcrafting.ingredient.IngredientStackEntry;
import de.siphalor.nbtcrafting.util.duck.ICloneable;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class TinkerersSmithingRecipeGenerator extends FabricRecipeProvider {
	public static final String ID = "tinkerers_smithing";

	public TinkerersSmithingRecipeGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	private static Ingredient ofAdvancedEntries(Stream<? extends IngredientEntry> entries) {
		if (entries == null)
			NbtCrafting.logError("Internal error: can't construct ingredient from null entry stream!");
		try {
			Ingredient ingredient;
			//noinspection ConstantConditions
			ingredient = (Ingredient) ((ICloneable) (Object) Ingredient.EMPTY).clone();
			((IIngredient) (Object) ingredient).nbtCrafting$setAdvancedEntries(entries);
			return ingredient;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return Ingredient.EMPTY;
	}

	@Override
	protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {

		ItemStack base = Items.IRON_PICKAXE.getDefaultStack();
		NbtCompound baseNbt = base.getOrCreateNbt();

		ItemStack result = Items.DIAMOND_PICKAXE.getDefaultStack();
		NbtCompound resultNbt = result.getOrCreateNbt();
		resultNbt.putString("$", "base");
		resultNbt.putString("Damage", String.valueOf(Math.floor(result.getItem().getMaxDamage() * ((3 - 1) / 4.0))));

		SmithingNBTRecipeJsonFactory.create(ofAdvancedEntries(Stream.of(
				new IngredientStackEntry(base)
		)), Ingredient.ofItems(Items.DIAMOND), result)
				.offerTo(exporter, new Identifier("diamond_pickaxe_upgrade"));

	}
}
