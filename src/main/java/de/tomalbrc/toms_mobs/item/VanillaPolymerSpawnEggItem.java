package de.tomalbrc.toms_mobs.item;

import eu.pb4.polymer.core.api.item.PolymerSpawnEggItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import xyz.nucleoid.packettweaker.PacketContext;

public class VanillaPolymerSpawnEggItem extends PolymerSpawnEggItem {
    public VanillaPolymerSpawnEggItem(EntityType<? extends Mob> type, Item visualItem, Properties settings) {
        super(visualItem, settings.component(DataComponents.ENTITY_DATA, TypedEntityData.of(type, new CompoundTag())));
    }

    @Override
    public ResourceLocation getPolymerItemModel(ItemStack itemStack, PacketContext context) {
        return this.getPolymerItem(itemStack, context).getDefaultInstance().get(DataComponents.ITEM_MODEL);
    }
}