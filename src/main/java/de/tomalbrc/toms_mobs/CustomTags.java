package de.tomalbrc.toms_mobs;

import de.tomalbrc.toms_mobs.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class CustomTags {
    public static final TagKey<@NotNull Block> PERCHES = TagKey.create(Registries.BLOCK, Util.id("perches"));
    public static final TagKey<@NotNull Block> SHOREBIRDS_SPAWNABLE_ON = TagKey.create(Registries.BLOCK, Util.id("shorebirds_spawnable_on"));

    public static final TagKey<@NotNull EntityType<?>> WATERBIRDS = TagKey.create(Registries.ENTITY_TYPE, Util.id("waterbirds"));
    public static final TagKey<@NotNull EntityType<?>> SEAGULL_HUNT_TARGET = TagKey.create(Registries.ENTITY_TYPE, Util.id("seagull_hunt_targets"));
}
