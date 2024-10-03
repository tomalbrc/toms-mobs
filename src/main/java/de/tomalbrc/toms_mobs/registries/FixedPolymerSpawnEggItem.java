package de.tomalbrc.toms_mobs.registries;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

public class FixedPolymerSpawnEggItem extends SpawnEggItem implements PolymerItem {

    private final Item visualItem;

    public FixedPolymerSpawnEggItem(EntityType<? extends Mob> type, Item visualItem, Properties settings) {
        super(type, 0, 0, settings);
        this.visualItem = visualItem;
    }


    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayer player) {
        return this.visualItem;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, HolderLookup.Provider lookup, @Nullable ServerPlayer player) {
        itemStack = PolymerItemUtils.createItemStack(itemStack, tooltipType, lookup, player);
        itemStack.set(DataComponents.ITEM_MODEL, this.visualItem.components().get(DataComponents.ITEM_MODEL));
        return itemStack;
    }
}
