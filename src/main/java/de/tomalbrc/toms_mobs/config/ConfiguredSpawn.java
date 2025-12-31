package de.tomalbrc.toms_mobs.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.tomalbrc.toms_mobs.util.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public record ConfiguredSpawn(Identifier mob, HolderSet<@NotNull Biome> biomes, int weight, int minGroup, int maxGroup) {
    public static final Identifier ID = Util.id("spawn-data");
    public static final MapCodec<ConfiguredSpawn> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            Identifier.CODEC.fieldOf("mob").forGetter(ConfiguredSpawn::mob),
                            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ConfiguredSpawn::biomes),
                            Codec.INT.fieldOf("weight").forGetter(ConfiguredSpawn::weight),
                            Codec.INT.fieldOf("min-group").forGetter(ConfiguredSpawn::minGroup),
                            Codec.INT.fieldOf("max-group").forGetter(ConfiguredSpawn::maxGroup))
                    .apply(builder, ConfiguredSpawn::new)
    );
}