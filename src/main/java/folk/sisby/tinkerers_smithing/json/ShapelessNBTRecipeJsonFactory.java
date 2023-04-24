package folk.sisby.tinkerers_smithing.json;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.siphalor.nbtcrafting.api.nbt.NbtUtil;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ShapelessNBTRecipeJsonFactory implements CraftingRecipeJsonFactory {
	private final Collection<String> modRequirements = new ArrayList<>();
	private final ItemStack output;
	private final int outputCount;
	private final List<Ingredient> inputs = Lists.<Ingredient>newArrayList();
	private final Advancement.Task builder = Advancement.Task.create();
	@Nullable
	private String group;

	public ShapelessNBTRecipeJsonFactory(ItemStack output, int outputCount) {
		this.output = output;
		this.outputCount = outputCount;
	}

	public static ShapelessNBTRecipeJsonFactory create(ItemStack output) {
		return new ShapelessNBTRecipeJsonFactory(output, 1);
	}

	public static ShapelessNBTRecipeJsonFactory create(ItemStack output, int outputCount) {
		return new ShapelessNBTRecipeJsonFactory(output, outputCount);
	}

	public ShapelessNBTRecipeJsonFactory m_jrksubfg(TagKey<Item> item) {
		return this.input(Ingredient.ofTag(item));
	}

	public ShapelessNBTRecipeJsonFactory input(ItemConvertible itemProvider) {
		return this.input(itemProvider, 1);
	}

	public ShapelessNBTRecipeJsonFactory input(ItemConvertible itemProvider, int size) {
		for(int i = 0; i < size; ++i) {
			this.input(Ingredient.ofItems(itemProvider));
		}

		return this;
	}

	public ShapelessNBTRecipeJsonFactory input(Ingredient ingredient) {
		return this.input(ingredient, 1);
	}

	public ShapelessNBTRecipeJsonFactory input(Ingredient ingredient, int size) {
		for(int i = 0; i < size; ++i) {
			this.inputs.add(ingredient);
		}

		return this;
	}

	public ShapelessNBTRecipeJsonFactory criterion(String string, CriterionConditions criterionConditions) {
		this.builder.criterion(string, criterionConditions);
		return this;
	}

	public ShapelessNBTRecipeJsonFactory requiresMod(String id) {
		this.modRequirements.add(id);
		return this;
	}

	public ShapelessNBTRecipeJsonFactory group(@Nullable String string) {
		this.group = string;
		return this;
	}

	@Override
	public Item getOutputItem() {
		return this.output.getItem();
	}

	@Override
	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		this.builder
				.parent(f_rbjlzbic)
				.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId))
				.criteriaMerger(CriterionMerger.OR);
		exporter.accept(
				new ShapelessNBTRecipeJsonFactory.ShapelessNBTRecipeJsonProvider(
					this.modRequirements, recipeId,
						this.output,
						this.outputCount,
						this.group == null ? "" : this.group,
						this.inputs,
						this.builder,
						new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getItem().getGroup().getName() + "/" + recipeId.getPath())
				)
		);
	}

	private void validate(Identifier recipeId) {
//		if (this.builder.getCriteria().isEmpty()) {
//			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
//		}
	}

	public static class ShapelessNBTRecipeJsonProvider implements RecipeJsonProvider {
		private final Collection<String> modRequirements;
		private final Identifier recipeId;
		private final ItemStack output;
		private final int count;
		private final String group;
		private final List<Ingredient> inputs;
		private final Advancement.Task builder;
		private final Identifier advancementId;

		public ShapelessNBTRecipeJsonProvider(
			Collection<String> modRequirements, Identifier recipeId, ItemStack output, int outputCount, String group, List<Ingredient> inputs, Advancement.Task builder, Identifier advancementId
		) {
			this.modRequirements = modRequirements;
			this.recipeId = recipeId;
			this.output = output;
			this.count = outputCount;
			this.group = group;
			this.inputs = inputs;
			this.builder = builder;
			this.advancementId = advancementId;
		}

		@Override
		public void serialize(JsonObject json) {
			if (!modRequirements.isEmpty()) {
				JsonArray loadConditions = new JsonArray();
				JsonObject loadCondition = new JsonObject();
				loadCondition.addProperty("condition","fabric:all_mods_loaded");
				JsonArray modArray = new JsonArray();
				modRequirements.forEach(modArray::add);
				loadCondition.add("value", modArray);
				loadConditions.add(loadCondition);
				json.add("fabric:load_conditions", loadConditions);
			}

			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			JsonArray jsonArray = new JsonArray();

			for(Ingredient ingredient : this.inputs) {
				JsonObject baseJson = ingredient.toJson().getAsJsonObject();
				if (baseJson.has("require")) {
					// Fix required nesting
					JsonObject dataJson = new JsonObject();
					baseJson.add("data", dataJson);
					dataJson.add("require", baseJson.getAsJsonObject("require"));
					baseJson.remove("require");
				}
				jsonArray.add(baseJson);
			}

			json.add("ingredients", jsonArray);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getId(this.output.getItem()).toString());
			if (this.count > 1) {
				jsonObject.addProperty("count", this.count);
			}
			if (this.output.hasNbt()) jsonObject.add("data", NbtUtil.toJson(this.output.getNbt()));

			json.add("result", jsonObject);
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return RecipeSerializer.SHAPELESS;
		}

		@Override
		public Identifier getRecipeId() {
			return this.recipeId;
		}

		@Nullable
		@Override
		public JsonObject toAdvancementJson() {
			return null;
		}

		@Nullable
		@Override
		public Identifier getAdvancementId() {
			return null;
		}
	}
}
