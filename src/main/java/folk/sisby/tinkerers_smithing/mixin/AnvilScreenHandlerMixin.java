package folk.sisby.tinkerers_smithing.mixin;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
	@Shadow private int repairItemUsage;
	@Final @Shadow private Property levelCost;
	@Shadow private String newItemName;

	public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getNextCost(I)I"))
	private int repairNoWork(int i) {
		return this.repairItemUsage > 0 ? i : AnvilScreenHandler.getNextCost(i);
	}

	@Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;sendContentUpdates()V"))
	private void repairNoLevels(CallbackInfo ci) {
		if (this.repairItemUsage > 0) {
			this.levelCost.set((StringUtils.isBlank(this.newItemName) && !this.input.getStack(0).hasCustomName()) || Objects.equals(this.input.getStack(0).getName().getString(), this.newItemName) ? 0 : 1);
		}
	}

	@Redirect(method = "canTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I", ordinal = 1))
	private int allowTakingFreeOutput(Property instance) {
		return this.repairItemUsage > 0 ? 1 : instance.get();
	}

	@ModifyVariable(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V", ordinal = 5), ordinal = 0)
	private int allowGeneratingFreeOutput(int setCost) {
		return setCost == 0 && this.repairItemUsage > 0 ? 1 : setCost;
	}

	@Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 1), cancellable = true)
	private void applyDeworkMaterial(CallbackInfo ci) {
		ItemStack base = this.input.getStack(0);
		ItemStack ingredient = this.input.getStack(1);
		if (ingredient.isIn(TinkerersSmithing.DEWORK_INGREDIENTS) && base.getRepairCost() > 0) {
			ItemStack result = base.copy();
			this.repairItemUsage = 0;
			do  {
				result.setRepairCost(((result.getRepairCost() + 1)/2)-1);
				this.repairItemUsage++;
			} while (result.getRepairCost() > 0 && this.repairItemUsage < ingredient.getCount());
			this.output.setStack(0, result);
			this.levelCost.set(0);
			this.sendContentUpdates();
			ci.cancel();
		}
	}

	@ModifyVariable(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V", ordinal = 1), ordinal = 0)
	private int noCombineRepairCost(int original) {
		this.repairItemUsage = 1;
		return original - 2;
	}

	private int getSRCost(Map<Enchantment, Integer> base, Map<Enchantment, Integer> ingredient) {
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

	private boolean doSwapEnchantments(Map<Enchantment, Integer> base, Map<Enchantment, Integer> ingredient) {
		return !(this.input.getStack(1).isOf(Items.ENCHANTED_BOOK)) && getSRCost(base, ingredient) > getSRCost(ingredient, base);
	}

	@ModifyVariable(method = "updateResult", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;get(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;", ordinal = 1), ordinal = 0)
	private Map<Enchantment, Integer> orderlessCombineSwapBaseTable(Map<Enchantment, Integer> base) {
		Map<Enchantment, Integer> ingredient = EnchantmentHelper.get(this.input.getStack(1));
		return doSwapEnchantments(base, ingredient) ? ingredient : base;
	}

	@ModifyVariable(method = "updateResult", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;get(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;", ordinal = 1), ordinal = 1)
	private Map<Enchantment, Integer> orderlessCombineSwapIngredientTable(Map<Enchantment, Integer> ingredient) {
		Map<Enchantment, Integer> base = EnchantmentHelper.get(this.input.getStack(0));
		return doSwapEnchantments(base, ingredient) ? base : ingredient;
	}
}
