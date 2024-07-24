package de.tomalbrc.toms_mobs.registries;

import de.tomalbrc.toms_mobs.TomsMobs;
import de.tomalbrc.toms_mobs.item.TexturedPolymerItem;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ItemRegistry {
    public static final Item NAUTILUS_SHELL_FRAGMENT = new TexturedPolymerItem(new Item.Properties().stacksTo(64), ResourceLocation.fromNamespaceAndPath("toms_mobs", "item/nautilus_shell_fragment"));

    public static final Object2ObjectLinkedOpenHashMap<ResourceLocation, Item> CUSTOM_ITEMS = new Object2ObjectLinkedOpenHashMap<>();

    public static void registerItems() {
        register(NAUTILUS_SHELL_FRAGMENT, ResourceLocation.fromNamespaceAndPath(TomsMobs.MODID, "nautilus_shell_fragment"));

        CreativeModeTab ITEM_GROUP = new CreativeModeTab.Builder(null, -1)
                .title(Component.literal("Toms Mobs Items").withStyle(ChatFormatting.AQUA))
                .icon(NAUTILUS_SHELL_FRAGMENT::getDefaultInstance)
                .displayItems((parameters, output) -> CUSTOM_ITEMS.forEach((key, value) -> output.accept(value)))
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Util.id("items"), ITEM_GROUP);
    }

    static public void register(Item item, ResourceLocation identifier) {
        Registry.register(BuiltInRegistries.ITEM, identifier, item);
        CUSTOM_ITEMS.putIfAbsent(identifier, item);
    }
}
