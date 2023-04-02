package folk.sisby.tinkerers_smithing;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ShapelessNBTRecipeJsonFactory implements CraftingRecipeJsonFactory {
	private final Item output;
	private final int outputCount;
	private final List<Ingredient> inputs = Lists.<Ingredient>newArrayList();
	private final Advancement.Task builder = Advancement.Task.create();
	@Nullable
	private String group;

	public ShapelessNBTRecipeJsonFactory(ItemConvertible output, int outputCount) {
		this.output = output.asItem();
		this.outputCount = outputCount;
	}

	public static ShapelessNBTRecipeJsonFactory create(ItemConvertible output) {
		return new ShapelessNBTRecipeJsonFactory(output, 1);
	}

	public static ShapelessNBTRecipeJsonFactory create(ItemConvertible output, int outputCount) {
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

	public ShapelessNBTRecipeJsonFactory group(@Nullable String string) {
		this.group = string;
		return this;
	}

	@Override
	public Item getOutputItem() {
		return this.output;
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
						recipeId,
						this.output,
						this.outputCount,
						this.group == null ? "" : this.group,
						this.inputs,
						this.builder,
						new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getGroup().getName() + "/" + recipeId.getPath())
				)
		);
	}

	private void validate(Identifier recipeId) {
		if (this.builder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static class ShapelessNBTRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final Item output;
		private final int count;
		private final String group;
		private final List<Ingredient> inputs;
		private final Advancement.Task builder;
		private final Identifier advancementId;

		public ShapelessNBTRecipeJsonProvider(
				Identifier recipeId, Item output, int outputCount, String group, List<Ingredient> inputs, Advancement.Task builder, Identifier advancementId
		) {
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
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			JsonArray jsonArray = new JsonArray();

			for(Ingredient ingredient : this.inputs) {
				jsonArray.add(ingredient.toJson());
			}

			json.add("ingredients", jsonArray);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getId(this.output).toString());
			if (this.count > 1) {
				jsonObject.addProperty("count", this.count);
			}

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
			return this.builder.toJson();
		}

		@Nullable
		@Override
		public Identifier getAdvancementId() {
			return this.advancementId;
		}
	}
}
