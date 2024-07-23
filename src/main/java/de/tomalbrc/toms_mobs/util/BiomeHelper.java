package de.tomalbrc.toms_mobs.util;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;

import java.util.Collection;
import java.util.function.Predicate;

public class BiomeHelper {
    public static void addSpawn(EntityType<?> type, int weight, int minGroupSize, int maxGroupSize, Predicate<BiomeSelectionContext> selector) {
        BiomeHelper.addSpawn(type, type.getCategory(), weight, minGroupSize, maxGroupSize, selector);
    }

    public static void addSpawn(EntityType<?> type, MobCategory category, int weight, int minGroupSize, int maxGroupSize, Predicate<BiomeSelectionContext> selector) {
        BiomeModifications.addSpawn(selector, category, type, weight, minGroupSize, maxGroupSize);
    }

    public static Predicate<BiomeSelectionContext> excludeTag(TagKey<Biome> tag) {
        return context -> !context.hasTag(tag);
    }

    public static Predicate<BiomeSelectionContext> excludeByLocation(String... locations) {
        return excludeByLocation(toResourceLocation(locations));
    }

    public static Predicate<BiomeSelectionContext> excludeByLocation(ResourceLocation... locations) {
        return excludeByLocation(ImmutableSet.copyOf(locations));
    }

    public static Predicate<BiomeSelectionContext> excludeByLocation(Collection<ResourceLocation> locations) {
        return context -> !locations.contains(context.getBiomeKey().location());
    }

    public static Predicate<BiomeSelectionContext> includeByLocation(String... locations) {
        return includeByLocation(toResourceLocation(locations));
    }

    public static Predicate<BiomeSelectionContext> includeByLocation(ResourceLocation... locations) {
        return includeByLocation(ImmutableSet.copyOf(locations));
    }

    public static Predicate<BiomeSelectionContext> includeByLocation(Collection<ResourceLocation> locations) {
        return context -> {
            for (var l : locations) {
                if (context.getBiomeRegistryEntry().is(of(l))) {
                    return true;
                }
            }
            return false;
        };
    }

    private static TagKey<Biome> of(ResourceLocation id) {
        var tagKey = TagKey.create(Registries.BIOME, id);
        return tagKey;
    }

    private static ResourceLocation[] toResourceLocation(String[] strings) {
        ResourceLocation[] locations = new ResourceLocation[strings.length];
        for (int i = 0; i < strings.length; i++) {
            locations[i] = ResourceLocation.parse(strings[i]);
        }
        return locations;
    }
}
