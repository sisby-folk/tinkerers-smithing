package folk.sisby.tinkerers_smithing.json;


import com.google.gson.JsonObject;
import de.siphalor.nbtcrafting.NbtCrafting;
import de.siphalor.nbtcrafting.api.nbt.NbtUtil;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SmithingNBTRecipeJsonFactory {
	private final Ingredient base;
	private final Ingredient addition;
	private final ItemStack result;
	private final Advancement.Task builder = Advancement.Task.create();
	private final RecipeSerializer<?> serializer;

	public SmithingNBTRecipeJsonFactory(RecipeSerializer<?> serializer, Ingredient base, Ingredient addition, ItemStack result) {
		this.serializer = serializer;
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

	public static SmithingNBTRecipeJsonFactory create(Ingredient base, Ingredient addition, ItemStack result) {
		return new SmithingNBTRecipeJsonFactory(NbtCrafting.SMITHING_RECIPE_SERIALIZER, base, addition, result);
	}

	public SmithingNBTRecipeJsonFactory criterion(String criterionName, CriterionConditions conditions) {
		this.builder.criterion(criterionName, conditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeId) {
		this.offerTo(exporter, new Identifier(recipeId));
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		this.builder
				.parent(CraftingRecipeJsonFactory.f_rbjlzbic)
				.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId))
				.criteriaMerger(CriterionMerger.OR);
		exporter.accept(
				new SmithingNBTRecipeJsonFactory.SmithingNBTRecipeJsonProvider(
						recipeId,
						this.serializer,
						this.base,
						this.addition,
						this.result,
						this.builder,
						new Identifier(recipeId.getNamespace(), "recipes/" + this.result.getItem().getGroup().getName() + "/" + recipeId.getPath())
				)
		);
	}

	private void validate(Identifier recipeId) {
//		if (this.builder.getCriteria().isEmpty()) {
//			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
//		}
	}

	public static class SmithingNBTRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final Ingredient base;
		private final Ingredient ingredient;
		private final ItemStack result;
		private final Advancement.Task builder;
		private final Identifier advancementId;
		private final RecipeSerializer<?> serializer;

		public SmithingNBTRecipeJsonProvider(
			Identifier recipeId, RecipeSerializer<?> serializer, Ingredient base, Ingredient ingredient, ItemStack result, Advancement.Task builder, Identifier advancementId
		) {
			this.recipeId = recipeId;
			this.serializer = serializer;
			this.base = base;
			this.ingredient = ingredient;
			this.result = result;
			this.builder = builder;
			this.advancementId = advancementId;
		}

		@Override
		public void serialize(JsonObject json) {
			JsonObject baseJson = this.base.toJson().getAsJsonObject();
			if (baseJson.has("require")) {
				// Fix required nesting
				JsonObject dataJson = new JsonObject();
				baseJson.add("data", dataJson);
				dataJson.add("require", baseJson.getAsJsonObject("require"));
				baseJson.remove("require");
			}
			json.add("base", baseJson);

			json.add("ingredient", this.ingredient.toJson());
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getId(this.result.getItem()).toString());
			if (this.result.hasNbt()) jsonObject.add("data", NbtUtil.toJson(this.result.getNbt()));
			json.add("result", jsonObject);
		}

		@Override
		public Identifier getRecipeId() {
			return this.recipeId;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return this.serializer;
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