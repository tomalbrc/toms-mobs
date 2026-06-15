package de.tomalbrc.toms_mobs.util;

import com.mojang.serialization.MapCodec;
import de.tomalbrc.toms_mobs.TomsMobs;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber(modid = TomsMobs.MODID)
public class BiomeHelper {
    public interface BiomeSelectionContext {
        Holder<Biome> getBiomeHolder();

        default boolean hasTag(TagKey<Biome> tag) {
            return getBiomeHolder().is(tag);
        }
    }

    public record SpawnRecord(EntityType<?> type, MobCategory category, int weight, int minGroupSize, int maxGroupSize, Predicate<BiomeSelectionContext> selector) {}

    public static final List<SpawnRecord> QUEUED_SPAWNS = new ArrayList<>();

    public static void addSpawn(EntityType<?> type, int weight, int minGroupSize, int maxGroupSize, Predicate<BiomeSelectionContext> selector) {
        BiomeHelper.addSpawn(type, type.getCategory(), weight, minGroupSize, maxGroupSize, selector);
    }

    public static void addSpawn(EntityType<?> type, MobCategory category, int weight, int minGroupSize, int maxGroupSize, Predicate<BiomeSelectionContext> selector) {
        QUEUED_SPAWNS.add(new SpawnRecord(type, category, weight, minGroupSize, maxGroupSize, selector));
    }

    public static Predicate<BiomeSelectionContext> excludeTag(TagKey<Biome> tag) {
        return context -> !context.hasTag(tag);
    }

    public static Predicate<BiomeSelectionContext> includeTag(TagKey<Biome> tag) {
        return context -> context.hasTag(tag);
    }

    public static class TomsMobsBiomeModifier implements BiomeModifier {
        public static final TomsMobsBiomeModifier INSTANCE = new TomsMobsBiomeModifier();
        public static final MapCodec<TomsMobsBiomeModifier> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD) {
                BiomeSelectionContext context = () -> biome;

                for (SpawnRecord spawn : QUEUED_SPAWNS) {
                    if (spawn.selector().test(context)) {
                        builder.getMobSpawnSettings().addSpawn(
                                spawn.category(),
                                spawn.weight(),
                                new MobSpawnSettings.SpawnerData(spawn.type(), spawn.minGroupSize(), spawn.maxGroupSize())
                        );
                    }
                }
            }
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return CODEC;
        }
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, helper -> {
            helper.register(Util.id("dynamic_spawns_serializer"), TomsMobsBiomeModifier.CODEC);
        });

        event.register(NeoForgeRegistries.Keys.BIOME_MODIFIERS, helper -> {
            helper.register(Util.id("dynamic_spawns"), TomsMobsBiomeModifier.INSTANCE);
        });
    }
}