package de.tomalbrc.toms_mobs.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class TexturedPolymerItem extends Item implements PolymerItem {
    private final PolymerModelData modelData;

    public TexturedPolymerItem(Properties settings, ResourceLocation modelPath) {
        super(settings);
        this.modelData = PolymerResourcePackUtils.requestModel(Items.PAPER, modelPath);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayer player) {
        return this.modelData.item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayer player) {
        return this.modelData.value();
    }
}
