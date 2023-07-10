package folk.sisby.tinkerers_smithing.client.emi.recipe;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import folk.sisby.tinkerers_smithing.client.emi.IterativeSlotWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmiSmithingSacrificeRecipe implements EmiRecipe {
	private final Item tool;
	private final Map<Item, Integer> sacrificeTools;
	private final Item resultTool;
	private final int resultCost;
	private final int uniq = EmiUtil.RANDOM.nextInt();

	public EmiSmithingSacrificeRecipe(Item tool, Map<Item, Integer> sacrificeTools, Item resultTool, int resultCost) {
		this.tool = tool;
		this.sacrificeTools = sacrificeTools;
		this.resultTool = resultTool;
		this.resultCost = resultCost;
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return VanillaEmiRecipeCategories.SMITHING;
	}

	@Override
	public @Nullable Identifier getId() {
		return null;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(EmiStack.of(tool), EmiIngredient.of(sacrificeTools.keySet().stream().map(EmiStack::of).toList()));
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(EmiStack.of(resultTool));
	}

	@Override
	public boolean supportsRecipeTree() {
		return false;
	}

	@Override
	public int getDisplayWidth() {
		return 125;
	}

	@Override
	public int getDisplayHeight() {
		return 18;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.PLUS, 27, 3);
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
		widgets.addSlot(EmiStack.of(tool), 0, 0);
		widgets.add(new IterativeSlotWidget(this::getSacrificeStack, uniq, 49, 0));
		widgets.add(new IterativeSlotWidget(this::getTool, uniq, 107, 0).recipeContext(this));
	}

	private Map.Entry<Item, Integer> getSacrificeTool(long i) {
		return this.sacrificeTools.entrySet().stream().toList().get(Math.floorMod(i, this.sacrificeTools.size()));
	}

	private EmiStack getSacrificeStack(Random r, long i) {
		Map.Entry<Item, Integer> sacrificeTool = getSacrificeTool(i);
		ItemStack sacrificeStack = sacrificeTool.getKey().getDefaultStack();
		int sacrificeDamage = r.nextInt(sacrificeStack.getMaxDamage());
		sacrificeStack.setDamage(sacrificeDamage);
		return sacrificeDamage == 0 ? EmiStack.of(sacrificeTool.getKey()) : EmiStack.of(sacrificeStack);
	}

	private EmiStack getTool(Random r, long i) {
		Map.Entry<Item, Integer> sacrificeTool = getSacrificeTool(i);
		int sacrificeDamage = r.nextInt(sacrificeTool.getKey().getMaxDamage());
		int resultDamage = (int) Math.ceil(resultTool.getMaxDamage() - ((sacrificeTool.getKey().getMaxDamage() - sacrificeDamage) * ((double) sacrificeTool.getValue() * resultTool.getMaxDamage()) / ((double)sacrificeTool.getKey().getMaxDamage() * this.resultCost)));
		ItemStack stack = resultTool.getDefaultStack();
		stack.setDamage(resultDamage);
		if (resultDamage <= 0) {
			return EmiStack.of(resultTool);
		}
		return EmiStack.of(stack);
	}
}
