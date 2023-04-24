package folk.sisby.tinkerers_smithing;

import de.siphalor.nbtcrafting.NbtCrafting;
import de.siphalor.nbtcrafting.ingredient.IIngredient;
import de.siphalor.nbtcrafting.ingredient.IngredientEntry;
import de.siphalor.nbtcrafting.ingredient.IngredientStackEntry;
import de.siphalor.nbtcrafting.util.duck.ICloneable;
import folk.sisby.tinkerers_smithing.json.ShapelessNBTRecipeJsonFactory;
import folk.sisby.tinkerers_smithing.json.SmithingNBTRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public record TinkerersEquipment(Item item, Collection<String> modRequirements, int unitCost, boolean useGrid, Ingredient repairMaterial,
								 TinkerersEquipment upgradeTo, Collection<TinkerersEquipment> sacrifices,
								 Item sacrificeTo) {
	public TinkerersEquipment(Item item, Collection<String> modRequirements, int unitCost, boolean useGrid, Ingredient repairMaterial) {
		this(item, modRequirements, unitCost, useGrid, repairMaterial, null, List.of(), null);
	}

	public TinkerersEquipment(Item item, Collection<String> modRequirements, int unitCost, boolean useGrid, Ingredient repairMaterial, TinkerersEquipment upgradeTo) {
		this(item, modRequirements, unitCost, useGrid, repairMaterial, upgradeTo, List.of(), null);
	}

	public TinkerersEquipment(Item item, Collection<String> modRequirements, int unitCost, boolean useGrid, Ingredient repairMaterial, Collection<TinkerersEquipment> sacrifices, Item sacrificeTo) {
		this(item, modRequirements, unitCost, useGrid, repairMaterial, null, sacrifices, sacrificeTo);
	}

	public TinkerersEquipment(Item item, int unitCost, boolean useGrid, Ingredient repairMaterial) {
		this(item, List.of(), unitCost, useGrid, repairMaterial, null, List.of(), null);
	}

	public TinkerersEquipment(Item item, int unitCost, boolean useGrid, Ingredient repairMaterial, TinkerersEquipment upgradeTo) {
		this(item, List.of(), unitCost, useGrid, repairMaterial, upgradeTo, List.of(), null);
	}

	public TinkerersEquipment(Item item, int unitCost, boolean useGrid, Ingredient repairMaterial, Collection<TinkerersEquipment> sacrifices, Item sacrificeTo) {
		this(item, List.of(), unitCost, useGrid, repairMaterial, null, sacrifices, sacrificeTo);
	}

	public static final String ID = "tinkerers_smithing";

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

	private static String clampPositive(String expression) {
		return "$(%s)*((2*(%s)+1)%%2+1)/2".formatted(expression, expression);
	}

	public void generateUpgradeRecipe(Consumer<RecipeJsonProvider> exporter) {
		ItemStack baseStack = item.getDefaultStack();
		NbtCompound baseNbt = baseStack.getOrCreateNbt();
		baseNbt.remove("Damage");

		ItemStack resultStack = upgradeTo.item.getDefaultStack();
		NbtCompound resultNbt = resultStack.getOrCreateNbt();
		resultNbt.putString("$", "base");
		resultNbt.putString("Damage", "$%d".formatted((int) Math.floor(upgradeTo.item.getMaxDamage() * ((unitCost - 1) / 4.0))));


		SmithingNBTRecipeJsonFactory factory = SmithingNBTRecipeJsonFactory.create(
			ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))),
			upgradeTo().repairMaterial,
			resultStack
		);
		Set<String> requirements = new HashSet<>(modRequirements);
		requirements.addAll(upgradeTo.modRequirements);
		requirements.forEach(factory::requiresMod);
		factory.offerTo(exporter, new Identifier(ID, "%s_upgrade_%s".formatted(Registry.ITEM.getId(upgradeTo.item).getPath(), Registry.ITEM.getId(item).getPath().replaceFirst("_%s$".formatted(Registry.ITEM.getId(upgradeTo.item).getPath().split("_")[1]), ""))));
	}

	public void generateRepairRecipe(Consumer<RecipeJsonProvider> exporter) {
		ItemStack baseStack = item.getDefaultStack();
		NbtCompound baseNbt = baseStack.getOrCreateNbt();

		baseNbt.putString("Damage", "$1..");

		ItemStack resultStack = item.getDefaultStack();
		NbtCompound resultNbt = resultStack.getOrCreateNbt();
		resultNbt.putString("$", "base");
		resultNbt.putString("Damage", clampPositive("base.Damage-%s".formatted(Math.ceil(item.getMaxDamage() / 4.0))));

		SmithingNBTRecipeJsonFactory factory = SmithingNBTRecipeJsonFactory.create(
			ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))),
			repairMaterial,
			resultStack
		);
		modRequirements.forEach(factory::requiresMod);
		factory.offerTo(exporter, new Identifier(ID, "%s_repair".formatted(Registry.ITEM.getId(item).getPath())));
	}

	public void generateDeWorkRecipe(Consumer<RecipeJsonProvider> exporter) {
		ItemStack baseStack = item.getDefaultStack();
		NbtCompound baseNbt = baseStack.getOrCreateNbt();

		baseNbt.remove("Damage");
		baseNbt.putString("RepairCost", "$1..");

		ItemStack resultStack = item.getDefaultStack();
		NbtCompound resultNbt = resultStack.getOrCreateNbt();
		resultNbt.putString("$", "base");
		resultNbt.putString("RepairCost", "$((base.RepairCost + 1)/2)-1");
		resultNbt.remove("Damage");

		SmithingNBTRecipeJsonFactory factory = SmithingNBTRecipeJsonFactory.create(
			ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))),
			Ingredient.ofItems(Items.NETHERITE_SCRAP),
			resultStack
		);
		modRequirements.forEach(factory::requiresMod);
		factory.offerTo(exporter, new Identifier(ID, "%s_dework".formatted(Registry.ITEM.getId(item).getPath())));
	}

	public void generateShapelessUpgradeRecipe(Consumer<RecipeJsonProvider> exporter) {
		ItemStack baseStack = item.getDefaultStack();
		NbtCompound baseNbt = baseStack.getOrCreateNbt();
		baseNbt.remove("Damage");

		ItemStack resultStack = upgradeTo.item.getDefaultStack();
		NbtCompound resultNbt = resultStack.getOrCreateNbt();
		resultNbt.putString("$", "i0");
		resultNbt.remove("Damage");

		ShapelessNBTRecipeJsonFactory factory = ShapelessNBTRecipeJsonFactory.create(resultStack);
		factory.input(ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))));
		for (int i = 0; i < unitCost; i++) {
			factory.input(upgradeTo.repairMaterial);
		}
		Set<String> requirements = new HashSet<>(modRequirements);
		requirements.addAll(upgradeTo.modRequirements);
		requirements.forEach(factory::requiresMod);
		factory.offerTo(exporter, new Identifier(ID, "%s_upgrade_%s".formatted(Registry.ITEM.getId(upgradeTo.item).getPath(), Registry.ITEM.getId(item).getPath().replaceFirst("_%s$".formatted(Registry.ITEM.getId(upgradeTo.item).getPath().split("_")[1]), ""))));
	}

	public void generateShapelessRepairRecipes(Consumer<RecipeJsonProvider> exporter) {
		for (int ingredientsUsed = 1; ingredientsUsed <= unitCost; ingredientsUsed++) {
			ItemStack baseStack = item.getDefaultStack();
			NbtCompound baseNbt = baseStack.getOrCreateNbt();

			baseNbt.putString("Damage", "$%d..".formatted((int) Math.ceil((item.getMaxDamage() * (ingredientsUsed - 1)) / (double) unitCost) + 1));

			ItemStack resultStack = item.getDefaultStack();
			NbtCompound resultNbt = resultStack.getOrCreateNbt();
			resultNbt.putString("$", "i0");
			resultNbt.putString("Damage", clampPositive("i0.Damage-%d".formatted((int) Math.ceil((item.getMaxDamage() * ingredientsUsed) / (double) unitCost))));

			ShapelessNBTRecipeJsonFactory factory = ShapelessNBTRecipeJsonFactory.create(resultStack);
			factory.input(ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))));
			for (int i = 0; i < ingredientsUsed; i++) {
				factory.input(repairMaterial);
			}
			modRequirements.forEach(factory::requiresMod);
			factory.offerTo(exporter, new Identifier(ID, "%s_repair_%d".formatted(Registry.ITEM.getId(item).getPath(), ingredientsUsed)));
		}
	}

	public void generateSacrificeRecipe(Consumer<RecipeJsonProvider> exporter) {
		sacrifices.forEach(sacrifice -> {
			ItemStack baseStack = item.getDefaultStack();
			NbtCompound baseNbt = baseStack.getOrCreateNbt();
			baseNbt.remove("Damage");

			ItemStack resultStack = sacrificeTo.getDefaultStack();
			NbtCompound resultNbt = resultStack.getOrCreateNbt();
			resultNbt.putString("$", "base");
			resultNbt.putString("Damage", clampPositive("(%d-((%d-ingredient.Damage)*%.1f/%d))#i".formatted(
				sacrificeTo.getMaxDamage(),
				sacrifice.item.getMaxDamage(),
				(double) (sacrifice.unitCost * sacrificeTo.getMaxDamage()),
				sacrifice.item.getMaxDamage() * unitCost
			)));

			SmithingNBTRecipeJsonFactory factory = SmithingNBTRecipeJsonFactory.create(
				ofAdvancedEntries(Stream.of(new IngredientStackEntry(baseStack))),
				Ingredient.ofItems(sacrifice.item),
				resultStack
			);
			Set<String> requirements = new HashSet<>(modRequirements);
			requirements.addAll(sacrifice.modRequirements);
			requirements.forEach(factory::requiresMod);
			factory.offerTo(exporter, new Identifier(ID, "%s_sacrifice_%s".formatted(Registry.ITEM.getId(sacrificeTo).getPath(), Registry.ITEM.getId(sacrifice.item).getPath().replaceFirst("^%s_".formatted(Registry.ITEM.getId(sacrificeTo).getPath().split("_")[0]), ""))));
		});
	}
}
