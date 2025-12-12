package de.tomalbrc.toms_mobs.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class TexturedPolymerSpawnEggItem extends SpawnEggItem implements PolymerItem {
    private final Identifier model;

    public TexturedPolymerSpawnEggItem(EntityType<? extends Mob> e, Properties settings, Identifier modelPath) {
        super(settings.component(DataComponents.ENTITY_DATA, TypedEntityData.of(e, new CompoundTag())));
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
