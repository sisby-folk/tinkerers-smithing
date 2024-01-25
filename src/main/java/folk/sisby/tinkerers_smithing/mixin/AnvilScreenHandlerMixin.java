package folk.sisby.tinkerers_smithing.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import folk.sisby.tinkerers_smithing.recipe.ShapelessRepairRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
	@Shadow private int repairItemUsage;
	@Final @Shadow private Property levelCost;

	public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@ModifyExpressionValue(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getNextCost(I)I"))
	private int noLevelsNoWork(int original) {
		return this.levelCost.get() == 0 ? (original - 1) / 2 : original;
	}

	@ModifyExpressionValue(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;canRepair(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
	private boolean overrideRepairMaterials(boolean original) {
		for (CraftingRecipe recipe : this.player.getWorld().getRecipeManager().listAllOfType(RecipeType.CRAFTING)) {
			if (recipe instanceof ShapelessRepairRecipe srr && this.getSlot(AnvilScreenHandler.INPUT_1_ID).getStack().isOf(srr.baseItem) && srr.addition.test(this.getSlot(AnvilScreenHandler.INPUT_2_ID).getStack())) {
				return true;
			}
		}
		return false;
	}

	@ModifyVariable(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V", ordinal = 0), ordinal = 0)
	private int unitRepairNoLevels(int original) {
		return original - 1;
	}

	@ModifyVariable(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V", ordinal = 1), ordinal = 0)
	private int combineRepairNoLevels(int original) {
		this.repairItemUsage = -1;
		return original - 2;
	}

	@ModifyExpressionValue(method = "canTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I", ordinal = 1))
	private int allowTakingFreeRepairs(int original) {
		return original == 0 && this.repairItemUsage != 0 ? 1 : original;
	}

	@ModifyVariable(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V", ordinal = 5, shift = At.Shift.AFTER), ordinal = 0)
	private int allowFreeRepairs(int original) {
		if (original == 0 && this.repairItemUsage != 0) {
			this.levelCost.set(0); // Remove RepairCost cost
			return 1;
		} else {
			return original;
		}
	}

	@Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 1), cancellable = true)
	private void applyDeworkMaterial(CallbackInfo ci) {
		ItemStack base = this.getSlot(AnvilScreenHandler.INPUT_1_ID).getStack();
		ItemStack ingredient = this.getSlot(AnvilScreenHandler.INPUT_2_ID).getStack();
		if (ingredient.isIn(TinkerersSmithing.DEWORK_INGREDIENTS) && base.getRepairCost() > 0) {
			ItemStack result = base.copy();
			this.repairItemUsage = 0;
			do  {
				result.setRepairCost(((result.getRepairCost() + 1)/2)-1);
				this.repairItemUsage++;
			} while (result.getRepairCost() > 0 && this.repairItemUsage < ingredient.getCount());
			this.getSlot(AnvilScreenHandler.OUTPUT_ID).setStack(result);
			this.levelCost.set(0);
			this.sendContentUpdates();
			ci.cancel();
		}
	}

	@Unique private int getSRCost(Map<Enchantment, Integer> base, Map<Enchantment, Integer> ingredient) {
		return ingredient.entrySet().stream().map(entry -> {
			Enchantment enchantment = entry.getKey();
			int level = entry.getValue();
			int baseLevel = base.getOrDefault(enchantment, 0);
			int resultLevel = baseLevel == level ? level + 1 : Math.max(level, baseLevel);
			int rarityCost = switch (enchantment.getRarity()) {
				case COMMON -> 1;
				case UNCOMMON -> 2;
				case RARE -> 4;
				case VERY_RARE -> 8;
			};
			return rarityCost * resultLevel;
		}).reduce(0, Integer::sum);
	}

	@Unique private boolean doSwapEnchantments(Map<Enchantment, Integer> base, Map<Enchantment, Integer> ingredient) {
		return !(this.getSlot(AnvilScreenHandler.INPUT_2_ID).getStack().isOf(Items.ENCHANTED_BOOK)) && getSRCost(base, ingredient) > getSRCost(ingredient, base);
	}

	@ModifyVariable(method = "updateResult", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;get(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;", ordinal = 1), ordinal = 0)
	private Map<Enchantment, Integer> orderlessCombineSwapBaseTable(Map<Enchantment, Integer> base) {
		Map<Enchantment, Integer> ingredient = EnchantmentHelper.get(this.getSlot(AnvilScreenHandler.INPUT_2_ID).getStack());
		return doSwapEnchantments(base, ingredient) ? ingredient : base;
	}

	@SuppressWarnings("InvalidInjectorMethodSignature")
	@ModifyVariable(method = "updateResult", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;get(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;", ordinal = 1), ordinal = 1)
	private Map<Enchantment, Integer> orderlessCombineSwapIngredientTable(Map<Enchantment, Integer> ingredient) {
		Map<Enchantment, Integer> base = EnchantmentHelper.get(this.getSlot(AnvilScreenHandler.INPUT_1_ID).getStack());
		return doSwapEnchantments(base, ingredient) ? base : ingredient;
	}
}
