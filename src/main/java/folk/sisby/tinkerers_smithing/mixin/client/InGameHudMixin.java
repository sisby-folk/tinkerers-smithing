package folk.sisby.tinkerers_smithing.mixin.client;

import folk.sisby.tinkerers_smithing.TinkerersSmithing;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Shadow private ItemStack currentStack;

	@ModifyVariable(method = "renderHeldItemTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I", shift = At.Shift.BEFORE), ordinal = 0)
	private MutableText showBrokenHeldItemTooltip(MutableText text) {
		if (TinkerersSmithing.isBroken(currentStack)) {
			return Text.translatable("item.tinkerers_smithing.broken").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)).append(Text.literal(" ")).append(text);
		}
		return text;
	}
}
