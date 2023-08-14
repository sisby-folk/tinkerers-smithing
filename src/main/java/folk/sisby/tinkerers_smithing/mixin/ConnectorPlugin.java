package folk.sisby.tinkerers_smithing.mixin;

import net.fabricmc.loader.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class ConnectorPlugin implements IMixinConfigPlugin {
	public static final List<String> BANNED_MIXINS = List.of(
		"folk.sisby.tinkerers_smithing.mixin.ItemStackMixin",
		"folk.sisby.tinkerers_smithing.mixin.client.InGameHudMixin");

	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.startsWith("folk.sisby.tinkerers_smithing.mixin.") && FabricLoader.INSTANCE.isModLoaded("connectormod")) {
			return !BANNED_MIXINS.contains(mixinClassName);
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}
}
