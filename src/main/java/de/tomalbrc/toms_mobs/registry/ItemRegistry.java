package de.tomalbrc.toms_mobs.registry;

import de.tomalbrc.toms_mobs.TomsMobs;
import de.tomalbrc.toms_mobs.item.ElephantHarnessItem;
import de.tomalbrc.toms_mobs.item.TexturedPolymerItem;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

@SuppressWarnings("unused")
public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, TomsMobs.MODID);

    public static final Object2ObjectLinkedOpenHashMap<Identifier, Item> CUSTOM_ITEMS = new Object2ObjectLinkedOpenHashMap<>();

    public static final DeferredHolder<Item, ElephantHarnessItem> PINK_ELEPHANT_HARNESS = register("pink_elephant_harness", (x) -> new ElephantHarnessItem(x, Identifier.fromNamespaceAndPath("toms_mobs", "pink_elephant_harness"), "pink"));
    public static final DeferredHolder<Item, ElephantHarnessItem> LIME_ELEPHANT_HARNESS = register("lime_elephant_harness", (x) -> new ElephantHarnessItem(x, Identifier.fromNamespaceAndPath("toms_mobs", "lime_elephant_harness"), "lime"));
    public static final DeferredHolder<Item, ElephantHarnessItem> BLACK_ELEPHANT_HARNESS = register("black_elephant_harness", (x) -> new ElephantHarnessItem(x, Identifier.fromNamespaceAndPath("toms_mobs", "black_elephant_harness"), "black"));

    public static final DeferredHolder<Item, TexturedPolymerItem> EMPEROR_WING_PATTERN = register("emperor_wing_pattern", (x) -> new TexturedPolymerItem(x, Identifier.fromNamespaceAndPath("toms_mobs", "emperor_wing_pattern")));

    public static void registerItems() {
        CreativeModeTab ITEM_GROUP = new CreativeModeTab.Builder(null, -1)
                .title(Component.literal("Toms Mobs Items").withStyle(ChatFormatting.BLUE))
                .icon(() -> EMPEROR_WING_PATTERN.get().getDefaultInstance())
                .displayItems((parameters, output) -> CUSTOM_ITEMS.forEach((key, value) -> output.accept(value)))
                .build();

        PolymerCreativeModeTabUtils.registerPolymerCreativeModeTab(Util.id("items"), ITEM_GROUP);
    }

    private static <T extends Item> DeferredHolder<Item, T> register(String path, Function<Item.Properties, T> function) {
        Identifier identifier = Identifier.fromNamespaceAndPath(TomsMobs.MODID, path);

        return ITEMS.register(path, () -> {
            T item = function.apply(new Item.Properties().stacksTo(64).setId(ResourceKey.create(Registries.ITEM, identifier)));
            CUSTOM_ITEMS.putIfAbsent(identifier, item);
            return item;
        });
    }
}