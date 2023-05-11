package folk.sisby.tinkerers_smithing;

import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record TinkerersSmithingItemData(Item item, Map<Ingredient, Integer> unitCosts, Set<Item> upgradePaths, Map<Item, Pair<Integer, Map<Item, Integer>>> sacrificePaths) {

	public static TinkerersSmithingItemData read(PacketByteBuf buf) {
		Item item = Registry.ITEM.get(buf.readIdentifier());
		Map<Ingredient, Integer> unitCosts = buf.readMap(Ingredient::fromPacket, PacketByteBuf::readInt);
		Set<Item> upgradePaths = buf.readCollection(HashSet::new, (buf2) -> Registry.ITEM.get(buf2.readIdentifier()));
		Map<Item, Pair<Integer, Map<Item, Integer>>> sacrificePaths = buf.readMap(
			(buf2) -> Registry.ITEM.get(buf2.readIdentifier()),
			(buf2) -> new Pair<>(
				buf2.readInt(),
				buf2.readMap((buf3) -> Registry.ITEM.get(buf3.readIdentifier()), PacketByteBuf::readInt)
			)
		);
		return new TinkerersSmithingItemData(item, unitCosts, upgradePaths, sacrificePaths);
	}

	public void write(PacketByteBuf buf) {
		buf.writeIdentifier(Registry.ITEM.getId(item));
		buf.writeMap(unitCosts, (buf2, ing) -> ing.write(buf2), PacketByteBuf::writeInt);
		buf.writeCollection(upgradePaths, (buf2, item2) -> buf2.writeIdentifier(Registry.ITEM.getId(item2)));
		buf.writeMap(sacrificePaths, (buf2, item2) -> buf2.writeIdentifier(Registry.ITEM.getId(item2)), (buf2, pair) -> {
			buf2.writeInt(pair.getLeft());
			buf2.writeMap(pair.getRight(), (buf3, item2) -> buf3.writeIdentifier(Registry.ITEM.getId(item2)), PacketByteBuf::writeInt);
		});
	}
}
