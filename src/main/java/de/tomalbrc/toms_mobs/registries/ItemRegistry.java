package de.tomalbrc.toms_mobs.registries;

import de.tomalbrc.toms_mobs.TomsMobs;
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

public class ItemRegistry {
    public static final Object2ObjectLinkedOpenHashMap<ResourceLocation, Item> CUSTOM_ITEMS = new Object2ObjectLinkedOpenHashMap<>();

    public static final Item NAUTILUS_SHELL_FRAGMENT = register(ResourceLocation.fromNamespaceAndPath(TomsMobs.MODID, "nautilus_shell_fragment"), (x) -> new TexturedPolymerItem(x, ResourceLocation.fromNamespaceAndPath("toms_mobs", "nautilus_shell_fragment")));

    public static void registerItems() {
        CreativeModeTab ITEM_GROUP = new CreativeModeTab.Builder(null, -1)
                .title(Component.literal("Toms Mobs Items").withStyle(ChatFormatting.AQUA))
                .icon(NAUTILUS_SHELL_FRAGMENT::getDefaultInstance)
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
