package de.tomalbrc.toms_mobs.registries;

import de.tomalbrc.toms_mobs.entities.*;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.PolymerSpawnEggItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import de.tomalbrc.toms_mobs.util.BiomeHelper;
import de.tomalbrc.toms_mobs.util.Util;

public class MobRegistry {
    public static final EntityType<Penguin> PENGUIN = register(
            Penguin.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Penguin::new)
                    .spawnGroup(MobCategory.CREATURE)
                    .dimensions(EntityDimensions.scalable(0.6f, 0.95f))
                    .defaultAttributes(Penguin::createAttributes)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
    );

    public static final EntityType<Elephant> ELEPHANT = register(
            Elephant.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Elephant::new)
                    .spawnGroup(MobCategory.CREATURE)
                    .dimensions(EntityDimensions.scalable(3.f, 3.65f))
                    .defaultAttributes(Elephant::createAttributes)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
                    .trackRangeChunks(8)
    );

    public static final EntityType<Firemoth> FIREMOTH = register(
            Firemoth.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Firemoth::new)
                    .spawnGroup(MobCategory.AMBIENT)
                    .dimensions(EntityDimensions.scalable(0.5f, 0.5f))
                    .defaultAttributes(Firemoth::createAttributes)
                    .spawnRestriction(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Firemoth::checkFiremothSpawnRules)
    );

    public static final EntityType<Butterfly> BUTTERFLY = register(
            Butterfly.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Butterfly::new)
                    .spawnGroup(MobCategory.AMBIENT)
                    .dimensions(EntityDimensions.scalable(0.25f, 0.25f))
                    .defaultAttributes(Butterfly::createAttributes)
                    .spawnRestriction(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Butterfly::checkButterflySpawnRules)
    );

    public static final EntityType<Snake> SNAKE = register(
            Snake.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Snake::new)
                    .spawnGroup(MobCategory.MONSTER)
                    .dimensions(EntityDimensions.scalable(1.f, 0.4f))
                    .defaultAttributes(Snake::createAttributes)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Snake::checkSnakeSpawnRules)
    );

    public static final EntityType<Sculkling> SCULKLING = register(
            Sculkling.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Sculkling::new)
                    .spawnGroup(MobCategory.MONSTER)
                    .dimensions(EntityDimensions.scalable(0.5f, 0.9f))
                    .defaultAttributes(Snake::createAttributes)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Sculkling::checkSculklingSpawnRules)
    );

    public static final EntityType<Showmaster> STENEL = register(
            Showmaster.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Showmaster::new)
                    .spawnGroup(MobCategory.MONSTER)
                    .dimensions(EntityDimensions.scalable(0.7f, 1.8f))
                    .defaultAttributes(Showmaster::createAttributes)
    );

    public static final EntityType<Iceologer> ICEOLOGER = register(
            Iceologer.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Iceologer::new)
                    .spawnGroup(MobCategory.MONSTER)
                    .dimensions(EntityDimensions.scalable(0.7f, 1.8f))
                    .defaultAttributes(Iceologer::createAttributes)
    );

    public static final EntityType<IceSpike> ICE_SPIKE = register(
            IceSpike.ID,
            FabricEntityTypeBuilder.create()
                    .entityFactory(IceSpike::new)
                    .spawnGroup(MobCategory.MISC)
                    .dimensions(EntityDimensions.scalable(1.f, 2.f))
    );

    public static final EntityType<IceSpikeSmall> ICE_SPIKE_SMALL = register(
            IceSpikeSmall.ID,
            FabricEntityTypeBuilder.create()
                    .entityFactory(IceSpikeSmall::new)
                    .spawnGroup(MobCategory.MISC)
                    .dimensions(EntityDimensions.scalable(1.2f, 0.8f))
    );

    public static final EntityType<IceCluster> ICE_CLUSTER = register(
            IceCluster.ID,
            FabricEntityTypeBuilder.create()
                    .entityFactory(IceCluster::new)
                    .spawnGroup(MobCategory.MISC)
                    .dimensions(EntityDimensions.scalable(2, 1))
    );

    private static <T extends Entity> EntityType<T> register(ResourceLocation id, FabricEntityTypeBuilder<T> builder) {
        EntityType<T> type = builder.build();
        PolymerEntityUtils.registerType(type);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
    }

    public static void registerMobs() {
        BiomeHelper.addSpawn(PENGUIN, 3, 2, 4, BiomeSelectors.spawnsOneOf(EntityType.POLAR_BEAR)
                .or(BiomeSelectors.tag(BiomeTags.HAS_IGLOO))
                .or(BiomeSelectors.includeByKey(Biomes.SNOWY_BEACH, Biomes.ICE_SPIKES))
                .or(BiomeHelper.includeByLocation("c:icy"))
        );

        BiomeHelper.addSpawn(SNAKE, 50, 1, 3, BiomeSelectors.spawnsOneOf(EntityType.HUSK)
                .or(BiomeSelectors.tag(BiomeTags.IS_JUNGLE))
                .or(BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP))
                .or(BiomeHelper.includeByLocation("c:desert", "c:swamp", "c:jungle"))
        );

        BiomeHelper.addSpawn(ELEPHANT, 10, 1, 3, BiomeSelectors.includeByKey(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU)
                .or(BiomeHelper.includeByLocation("c:savanna"))
        );

        BiomeHelper.addSpawn(SCULKLING, 30, 1, 3, BiomeSelectors.foundInOverworld()
                .and(BiomeSelectors.includeByKey(Biomes.DEEP_DARK))
        );

        BiomeHelper.addSpawn(FIREMOTH, 1, 1, 1, BiomeSelectors.foundInOverworld()
                .and(BiomeHelper.excludeTag(BiomeTags.IS_OCEAN))
                .and(BiomeHelper.excludeTag(BiomeTags.IS_RIVER))
                .and(BiomeHelper.excludeByLocation("c:icy", "c:snowy"))
        );

        BiomeHelper.addSpawn(BUTTERFLY, 10, 1, 3, BiomeSelectors.foundInOverworld()
                .and(BiomeHelper.excludeTag(BiomeTags.IS_OCEAN))
                .and(BiomeHelper.excludeTag(BiomeTags.IS_RIVER))
                .and(BiomeHelper.excludeByLocation("c:icy", "c:snowy"))
        );

        addSpawnEgg(PENGUIN, Items.POLAR_BEAR_SPAWN_EGG);
        addSpawnEgg(SNAKE, Items.PANDA_SPAWN_EGG);
        addSpawnEgg(ELEPHANT, Items.DOLPHIN_SPAWN_EGG);
        addSpawnEgg(SCULKLING, Items.WARDEN_SPAWN_EGG);
        addSpawnEgg(FIREMOTH, Items.PARROT_SPAWN_EGG);
        addSpawnEgg(BUTTERFLY, Items.PARROT_SPAWN_EGG);

        addSpawnEgg(STENEL, Items.ENDERMITE_SPAWN_EGG);
        addSpawnEgg(ICEOLOGER, Items.VEX_SPAWN_EGG);
        addSpawnEgg(ICE_SPIKE, Items.SNOW_GOLEM_SPAWN_EGG);
        addSpawnEgg(ICE_SPIKE_SMALL, Items.SNOW_GOLEM_SPAWN_EGG);
        addSpawnEgg(ICE_CLUSTER, Items.SNOW_GOLEM_SPAWN_EGG);

        PolymerItemGroupUtils.registerPolymerItemGroup(Util.id("spawn-eggs"), ITEM_GROUP);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addSpawnEgg(EntityType type, Item item) {
        Item spawnEgg = new PolymerSpawnEggItem(type, item, new Item.Properties());
        registerItem(Util.id(EntityType.getKey(type).getPath() + "_spawn_egg"), spawnEgg);
    }

    private static void registerItem(ResourceLocation identifier, Item item) {
        Registry.register(BuiltInRegistries.ITEM, identifier, item);
        SPAWN_EGGS.putIfAbsent(identifier, item);
    }

    public static final Object2ObjectOpenHashMap<ResourceLocation, Item> SPAWN_EGGS = new Object2ObjectOpenHashMap<>();
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab.Builder(null, -1)
            .title(Component.literal("Tom's Mobs").withStyle(ChatFormatting.DARK_GREEN))
            .icon(Items.BAT_SPAWN_EGG::getDefaultInstance)
            .displayItems((parameters, output) -> SPAWN_EGGS.values().forEach(output::accept))
            .build();
}
