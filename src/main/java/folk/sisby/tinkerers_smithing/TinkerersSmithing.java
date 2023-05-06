package folk.sisby.tinkerers_smithing;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinkerersSmithing implements ModInitializer {
	public static final String ID = "tinkerers_smithing";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize(ModContainer mod) {
		

		LOGGER.info("[Tinkerer's Smithing] Initialized.");
	}
}
