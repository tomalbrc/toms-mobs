package de.tomalbrc.toms_mobs.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class TexturedPolymerItem extends Item implements PolymerItem {
    private final Identifier model;

    public TexturedPolymerItem(Properties settings, Identifier modelPath) {
        super(settings);
        this.model = modelPath;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PAPER;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return this.model;
    }
}
