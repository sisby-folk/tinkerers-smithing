package folk.sisby.tinkerers_smithing.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> {
	public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
		super(handler, playerInventory, title, texture);
	}

	@Inject(method = "drawForeground", at = @At(value = "TAIL"))
	private void drawRepairCost(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
		ItemStack base = handler.getSlot(AnvilScreenHandler.INPUT_1_ID).getStack();
		ItemStack ingredient = handler.getSlot(AnvilScreenHandler.INPUT_2_ID).getStack();
		ItemStack result = handler.getSlot(AnvilScreenHandler.OUTPUT_ID).getStack();
		if (!base.isEmpty() && !result.isEmpty() && this.handler.getLevelCost() == 0) {
			Text text = Text.translatable("container.repair.work", result.getRepairCost() - Math.max(base.getRepairCost(), ingredient.getRepairCost()));
			int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
			context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
			context.drawText(this.textRenderer, text.asOrderedText(), k, 69, 8453920, true);
		}
	}
}
