package folk.sisby.tinkerers_smithing;

import de.siphalor.nbtcrafting.NbtCrafting;
import de.siphalor.nbtcrafting.ingredient.IIngredient;
import de.siphalor.nbtcrafting.ingredient.IngredientEntry;
import de.siphalor.nbtcrafting.ingredient.IngredientStackEntry;
import de.siphalor.nbtcrafting.util.duck.ICloneable;
import folk.sisby.tinkerers_smithing.json.ShapelessNBTRecipeJsonFactory;
import folk.sisby.tinkerers_smithing.json.SmithingNBTRecipeJsonFactory;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;
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

	private String clampPositive(String expression) {
		return "$(%s)*((2*(%s)+1)%%2+1)/2".formatted(expression, expression);
	}

	private void generateUpgradeRecipe(Consumer<RecipeJsonProvider> exporter, Item base, Item result, Item ingredient, int ingredientCount) {
		ItemStack baseStack = base.getDefaultStack();
		NbtCompound baseNbt = baseStack.getOrCreateNbt();
		baseNbt.remove("Damage");

		ItemStack resultStack = result.getDefaultStack();
		resultStack.setDamage((int) Math.floor(result.getMaxDamage() * ((ingredientCount - 1) / 4.0)));
		NbtCompound resultNbt = resultStack.getOrCreateNbt();
		resultNbt.putString("$", "base");

		SmithingNBTRecipeJsonFactory.create(
			ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))),
			Ingredient.ofItems(ingredient),
			resultStack
		).offerTo(exporter, new Identifier(ID, "%s_upgrade".formatted(Registry.ITEM.getId(result).getPath())));
	}

	private void generateRepairRecipe(Consumer<RecipeJsonProvider> exporter, Item base, Item ingredient) {
		ItemStack baseStack = base.getDefaultStack();
		NbtCompound baseNbt = baseStack.getOrCreateNbt();

		baseNbt.putString("Damage", "$1..");

		ItemStack resultStack = base.getDefaultStack();
		NbtCompound resultNbt = resultStack.getOrCreateNbt();
		resultNbt.putString("$", "base");
		resultNbt.putString("Damage", clampPositive("base.Damage-%s".formatted(Math.ceil(base.getMaxDamage() / 4.0))));

		SmithingNBTRecipeJsonFactory.create(
			ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))),
			Ingredient.ofItems(ingredient),
			resultStack
		).offerTo(exporter, new Identifier(ID, "%s_repair".formatted(Registry.ITEM.getId(base).getPath())));
	}

	private void generateDeWorkRecipe(Consumer<RecipeJsonProvider> exporter, Item base) {
		ItemStack baseStack = base.getDefaultStack();
		NbtCompound baseNbt = baseStack.getOrCreateNbt();

		baseNbt.remove("Damage");
		baseNbt.putString("RepairCost", "$1..");

		ItemStack resultStack = base.getDefaultStack();
		NbtCompound resultNbt = resultStack.getOrCreateNbt();
		resultNbt.putString("$", "base");
		resultNbt.putString("RepairCost", "$((base.RepairCost + 1)/2)-1");
		resultNbt.remove("Damage");

		SmithingNBTRecipeJsonFactory.create(
			ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))),
			Ingredient.ofItems(Items.NETHERITE_SCRAP),
			resultStack
		).offerTo(exporter, new Identifier(ID, "%s_dework".formatted(Registry.ITEM.getId(base).getPath())));
	}

	private void generateShapelessUpgradeRecipe(Consumer<RecipeJsonProvider> exporter, Item base, Item result, Item ingredient, int ingredientCount) {
		ItemStack baseStack = base.getDefaultStack();
		NbtCompound baseNbt = baseStack.getOrCreateNbt();
		baseNbt.remove("Damage");

		ItemStack resultStack = result.getDefaultStack();
		NbtCompound resultNbt = resultStack.getOrCreateNbt();
		resultNbt.putString("$", "base");
		resultNbt.remove("Damage");

		ShapelessNBTRecipeJsonFactory factory = ShapelessNBTRecipeJsonFactory.create(resultStack);
		factory.input(ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))));
		for (int i = 0; i < ingredientCount; i++) {
			factory.input(ingredient);
		}
		factory.offerTo(exporter, new Identifier(ID, "%s_upgrade".formatted(Registry.ITEM.getId(result).getPath())));
	}

	private void generateShapelessRepairRecipes(Consumer<RecipeJsonProvider> exporter, Item base, Ingredient ingredient, int ingredientCount) {
		for (int ingredientsUsed = 1; ingredientsUsed <= ingredientCount; ingredientsUsed++) {
			ItemStack baseStack = base.getDefaultStack();
			NbtCompound baseNbt = baseStack.getOrCreateNbt();

			baseNbt.putString("Damage", "$%d..".formatted((int) Math.ceil((base.getMaxDamage() * (ingredientsUsed - 1)) / (double) ingredientCount) + 1));

			ItemStack resultStack = base.getDefaultStack();
			NbtCompound resultNbt = resultStack.getOrCreateNbt();
			resultNbt.putString("$", "base");
			resultNbt.putString("Damage", clampPositive("i0.Damage-%d".formatted((int) Math.ceil((base.getMaxDamage() * ingredientsUsed) / (double) ingredientCount))));

			ShapelessNBTRecipeJsonFactory factory = ShapelessNBTRecipeJsonFactory.create(resultStack);
			factory.input(ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))));
			for (int i = 0; i < ingredientsUsed; i++) {
				factory.input(ingredient);
			}

			factory.offerTo(exporter, new Identifier(ID, "%s_repair_%d".formatted(Registry.ITEM.getId(base).getPath(), ingredientsUsed)));
		}
	}

	private void generateSacrificeRecipe(Consumer<RecipeJsonProvider> exporter, Item base, Item result, Map<Item, Integer> sacrifices, int ingredientCount) {
		sacrifices.forEach((sacrifice, sacrificeUnits) -> {
			ItemStack baseStack = base.getDefaultStack();
			NbtCompound baseNbt = baseStack.getOrCreateNbt();
			baseNbt.remove("Damage");

			ItemStack resultStack = result.getDefaultStack();
			resultStack.setDamage((int) Math.floor(result.getMaxDamage() * ((ingredientCount - 1) / 4.0)));
			NbtCompound resultNbt = resultStack.getOrCreateNbt();
			resultNbt.putString("$", "base");
			resultNbt.putString("Damage", clampPositive("(%d-((%d-ingredient.Damage)*%.1f/%d))#i".formatted(
				result.getMaxDamage(),
				sacrifice.getMaxDamage(),
				(double) (sacrificeUnits * result.getMaxDamage()),
				sacrifice.getMaxDamage() * ingredientCount
			)));

			SmithingNBTRecipeJsonFactory.create(
				ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))),
				Ingredient.ofItems(sacrifice),
				resultStack
			).offerTo(exporter, new Identifier(ID, "%s_sacrifice_%s".formatted(Registry.ITEM.getId(result).getPath(), Registry.ITEM.getId(sacrifice).getPath().replaceFirst("^%s_".formatted(Registry.ITEM.getId(result).getPath().split("_")[0]), ""))));
		});

	}

	@Override
	protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
		generateUpgradeRecipe(exporter, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE, Items.DIAMOND, 3);
		generateRepairRecipe(exporter, Items.DIAMOND_PICKAXE, Items.DIAMOND);
		generateDeWorkRecipe(exporter, Items.DIAMOND_PICKAXE);
		generateShapelessUpgradeRecipe(exporter, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.IRON_INGOT, 3);
		generateShapelessRepairRecipes(exporter, Items.STONE_PICKAXE, Ingredient.ofTag(ItemTags.STONE_TOOL_MATERIALS), 3);
		Map<Item, Integer> netheriteSacrifices = Map.of(
			Items.NETHERITE_SHOVEL, 1,
			Items.NETHERITE_SWORD, 2,
			Items.NETHERITE_HOE, 2,
			Items.NETHERITE_PICKAXE, 3,
			Items.NETHERITE_AXE, 3,
			Items.NETHERITE_BOOTS, 4,
			Items.NETHERITE_HELMET, 5,
			Items.NETHERITE_LEGGINGS, 7,
			Items.NETHERITE_CHESTPLATE, 8
		);
		generateSacrificeRecipe(exporter, Items.GOLDEN_PICKAXE, Items.NETHERITE_PICKAXE, netheriteSacrifices, 3);
	}
}
