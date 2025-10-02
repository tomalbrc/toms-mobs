package de.tomalbrc.toms_mobs.registry;

import de.tomalbrc.toms_mobs.TomsMobs;
import de.tomalbrc.toms_mobs.item.ElephantHarnessItem;
import de.tomalbrc.toms_mobs.item.TexturedPolymerItem;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.Function;

@SuppressWarnings("unused")
public class ItemRegistry {
    public static final Object2ObjectLinkedOpenHashMap<ResourceLocation, Item> CUSTOM_ITEMS = new Object2ObjectLinkedOpenHashMap<>();

    public static final Item NAUTILUS_SHELL_FRAGMENT = register(ResourceLocation.fromNamespaceAndPath(TomsMobs.MODID, "nautilus_shell_fragment"), (x) -> new TexturedPolymerItem(x, ResourceLocation.fromNamespaceAndPath("toms_mobs", "nautilus_shell_fragment")));
    public static final Item PINK_ELEPHANT_HARNESS = register(ResourceLocation.fromNamespaceAndPath(TomsMobs.MODID, "pink_elephant_harness"), (x) -> new ElephantHarnessItem(x, ResourceLocation.fromNamespaceAndPath("toms_mobs", "pink_elephant_harness"), "pink"));
    public static final Item LIME_ELEPHANT_HARNESS = register(ResourceLocation.fromNamespaceAndPath(TomsMobs.MODID, "lime_elephant_harness"), (x) -> new ElephantHarnessItem(x, ResourceLocation.fromNamespaceAndPath("toms_mobs", "lime_elephant_harness"), "lime"));
    public static final Item BLACK_ELEPHANT_HARNESS = register(ResourceLocation.fromNamespaceAndPath(TomsMobs.MODID, "black_elephant_harness"), (x) -> new ElephantHarnessItem(x, ResourceLocation.fromNamespaceAndPath("toms_mobs", "black_elephant_harness"), "black"));

    public static final Item EMPEROR_WING_PATTERN = register(ResourceLocation.fromNamespaceAndPath(TomsMobs.MODID, "emperor_wing_pattern"), (x) -> new TexturedPolymerItem(x, ResourceLocation.fromNamespaceAndPath("toms_mobs", "emperor_wing_pattern")));

    public static void registerItems() {
        CreativeModeTab ITEM_GROUP = new CreativeModeTab.Builder(null, -1)
                .title(Component.literal("Toms Mobs Items").withStyle(ChatFormatting.BLUE))
                .icon(EMPEROR_WING_PATTERN::getDefaultInstance)
                .displayItems((parameters, output) -> CUSTOM_ITEMS.forEach((key, value) -> output.accept(value)))
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Util.id("items"), ITEM_GROUP);
    }

    static public <T extends Item> T register(ResourceLocation identifier, Function<Item.Properties, T> function) {
        var x = function.apply(new Item.Properties().stacksTo(64).setId(ResourceKey.create(Registries.ITEM, identifier)));
        Registry.register(BuiltInRegistries.ITEM, identifier, x);
        CUSTOM_ITEMS.putIfAbsent(identifier, x);
        return x;
    }
}
