package folk.sisby.tinkerers_smithing.client.emi;

import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;


public class IterativeSlotWidget extends SlotWidget {
	private static final int INCREMENT = 1000;
	private final BiFunction<Random, Long, EmiIngredient> stackSupplier;
	private final int unique;
	private long lastGenerate = 0;
	private EmiIngredient stack = null;

	public IterativeSlotWidget(BiFunction<Random, Long, EmiIngredient> stackSupplier, int unique, int x, int y) {
		super(EmiStack.EMPTY, x, y);
		this.stackSupplier = stackSupplier;
		this.unique = unique;
	}

	public IterativeSlotWidget(Function<Long, EmiIngredient> stackSupplier, int x, int y) {
		super(EmiStack.EMPTY, x, y);
		this.stackSupplier = (r, i) -> stackSupplier.apply(i);
		this.unique = 0;
	}

	public IterativeSlotWidget(Function<Random, EmiIngredient> stackSupplier, int unique, int x, int y) {
		super(EmiStack.EMPTY, x, y);
		this.stackSupplier = (r, i) -> stackSupplier.apply(r);
		this.unique = unique;
	}

	@Override
	public void drawOverlay(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (!getStack().isEmpty()) {
			int off = 1;
			if (output) {
				off = 5;
			}
			EmiRender.renderIngredientIcon(getStack(), matrices, x + off, y + off);
		}
		super.drawOverlay(matrices, mouseX, mouseY, delta);
	}

	@Override
	public EmiIngredient getStack() {
		long time = System.currentTimeMillis() / INCREMENT;
		if (stack == null || time > lastGenerate) {
			lastGenerate = time;
			stack = stackSupplier.apply(getRandom(time), time);
		}
		return stack;
	}

	private Random getRandom(long time) {
		return new Random(new Random(time ^ unique).nextInt());
	}
}
