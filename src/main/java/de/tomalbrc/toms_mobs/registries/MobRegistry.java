package de.tomalbrc.toms_mobs.registries;

import de.tomalbrc.toms_mobs.entities.hostile.*;
import de.tomalbrc.toms_mobs.entities.passive.*;
import de.tomalbrc.toms_mobs.util.BiomeHelper;
import de.tomalbrc.toms_mobs.util.Util;
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
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

public class MobRegistry {
    public static final EntityType<Penguin> PENGUIN = register(
            Penguin.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Penguin::new)
                    .spawnGroup(MobCategory.CREATURE)
                    .dimensions(EntityDimensions.scalable(0.6f, 0.95f))
                    .defaultAttributes(Penguin::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
    );

    public static final EntityType<Elephant> ELEPHANT = register(
            Elephant.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Elephant::new)
                    .spawnGroup(MobCategory.CREATURE)
                    .dimensions(EntityDimensions.scalable(3.f, 3.65f))
                    .defaultAttributes(Elephant::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
    );

    public static final EntityType<Capybara> CAPYBARA = register(
            Capybara.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Capybara::new)
                    .spawnGroup(MobCategory.CREATURE)
                    .dimensions(EntityDimensions.scalable(0.8f, 1.1f))
                    .defaultAttributes(Capybara::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
    );

    /*public static final EntityType<Vulture> VULTURE = register(
            Vulture.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Vulture::new)
                    .spawnGroup(MobCategory.CREATURE)
                    .dimensions(EntityDimensions.scalable(1.f, 1.f))
                    .defaultAttributes(Vulture::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
    );

    public static final EntityType<Seagull> SEAGULL = register(
            Seagull.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Seagull::new)
                    .spawnGroup(MobCategory.CREATURE)
                    .dimensions(EntityDimensions.scalable(1.f, 1.f))
                    .defaultAttributes(Seagull::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
    );*/

    public static final EntityType<Nautilus> NAUTILUS = register(
            Nautilus.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Nautilus::new)
                    .spawnGroup(MobCategory.WATER_CREATURE)
                    .dimensions(EntityDimensions.scalable(0.5f, 0.5f))
                    .defaultAttributes(Nautilus::createAttributes)
    );

    public static final EntityType<Mantaray> MANTARAY = register(
            Mantaray.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Mantaray::new)
                    .spawnGroup(MobCategory.WATER_CREATURE)
                    .dimensions(EntityDimensions.scalable(1.4f, 0.4f))
                    .defaultAttributes(Mantaray::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules)
    );

    public static final EntityType<Tuna> TUNA = register(
            Tuna.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Tuna::new)
                    .spawnGroup(MobCategory.WATER_CREATURE)
                    .dimensions(EntityDimensions.scalable(0.5f, 0.5f))
                    .defaultAttributes(Tuna::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules)
    );

    public static final EntityType<Lobster> LOBSTER = register(
            Lobster.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Lobster::new)
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .dimensions(EntityDimensions.scalable(0.65f, 0.35f))
                    .defaultAttributes(Lobster::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.OCEAN_FLOOR, (x,y,z,t,r) -> true)
    );

    /*
    public static final EntityType<Jellyfish> JELLYFISH = register(
            Jellyfish.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Jellyfish::new)
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .dimensions(EntityDimensions.scalable(0.5f, 0.5f))
                    .defaultAttributes(Jellyfish::createAttributes)
    );
    */


    public static final EntityType<Firemoth> FIREMOTH = register(
            Firemoth.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Firemoth::new)
                    .spawnGroup(MobCategory.AMBIENT)
                    .dimensions(EntityDimensions.scalable(0.5f, 0.5f))
                    .defaultAttributes(Firemoth::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Firemoth::checkMobSpawnRules)
    );

    public static final EntityType<Butterfly> BUTTERFLY = register(
            Butterfly.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Butterfly::new)
                    .spawnGroup(MobCategory.AMBIENT)
                    .dimensions(EntityDimensions.scalable(0.25f, 0.25f))
                    .defaultAttributes(Butterfly::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Butterfly::checkButterflySpawnRules)
    );

    public static final EntityType<Snake> SNAKE = register(
            Snake.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Snake::new)
                    .spawnGroup(MobCategory.MONSTER)
                    .dimensions(EntityDimensions.scalable(1.f, 0.4f))
                    .defaultAttributes(Snake::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Snake::checkSnakeSpawnRules)
    );

    public static final EntityType<Sculkling> SCULKLING = register(
            Sculkling.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Sculkling::new)
                    .spawnGroup(MobCategory.MONSTER)
                    .dimensions(EntityDimensions.scalable(0.5f, 0.9f))
                    .defaultAttributes(Snake::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Sculkling::checkSculklingSpawnRules)
    );

    public static final EntityType<Showmaster> SHOWMASTER = register(
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
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Iceologer::checkIceologerSpawnRules)
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
        BiomeHelper.addSpawn(PENGUIN, 15, 2, 5, BiomeSelectors.spawnsOneOf(EntityType.POLAR_BEAR)
                .or(BiomeSelectors.tag(BiomeTags.SPAWNS_SNOW_FOXES))
                .or(BiomeSelectors.tag(BiomeTags.HAS_IGLOO))
                .or(BiomeSelectors.includeByKey(Biomes.SNOWY_BEACH, Biomes.ICE_SPIKES))
        );

        BiomeHelper.addSpawn(SNAKE, 15, 2, 4, BiomeSelectors.spawnsOneOf(EntityType.HUSK)
                .or(BiomeSelectors.tag(BiomeTags.IS_JUNGLE))
                .or(BiomeSelectors.tag(BiomeTags.HAS_DESERT_PYRAMID))
                .or(BiomeSelectors.tag(BiomeTags.HAS_VILLAGE_DESERT))
                .or(BiomeSelectors.tag(BiomeTags.HAS_RUINED_PORTAL_DESERT))
                .or(BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP))
        );

        BiomeHelper.addSpawn(ELEPHANT, 20, 1, 3, BiomeSelectors.includeByKey(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU).or(BiomeSelectors.tag(BiomeTags.IS_JUNGLE)));

        BiomeHelper.addSpawn(SCULKLING, 50, 2, 4, BiomeSelectors.spawnsOneOf(EntityType.ZOMBIE).and(BiomeSelectors.excludeByKey(Biomes.LUSH_CAVES)));

        BiomeHelper.addSpawn(FIREMOTH, 5, 2, 3, BiomeSelectors.foundInTheNether()
                .and(BiomeSelectors.excludeByKey(Biomes.BASALT_DELTAS))
        );

        BiomeHelper.addSpawn(BUTTERFLY, 50, 2, 5, BiomeSelectors.foundInOverworld()
                .and(BiomeHelper.excludeTag(BiomeTags.IS_OCEAN))
                .and(BiomeHelper.excludeTag(BiomeTags.IS_RIVER))
                .and(BiomeHelper.excludeTag(BiomeTags.SPAWNS_SNOW_FOXES))
        );

        BiomeHelper.addSpawn(CAPYBARA, 15, 1, 3, BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.RIVER));

        // Icy
        BiomeHelper.addSpawn(ICEOLOGER, 1, 1, 3, BiomeSelectors.foundInOverworld().and(BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN)));

        BiomeHelper.addSpawn(MANTARAY, 20, 1, 1, BiomeSelectors.tag(BiomeTags.IS_OCEAN));
        BiomeHelper.addSpawn(TUNA, 10, 1, 3, BiomeSelectors.tag(BiomeTags.IS_OCEAN));
        BiomeHelper.addSpawn(NAUTILUS, 10, 1, 1, BiomeSelectors.tag(BiomeTags.IS_OCEAN));
        //BiomeHelper.addSpawn(JELLYFISH, 30, 1, 1, BiomeSelectors.tag(BiomeTags.IS_OCEAN));
        BiomeHelper.addSpawn(LOBSTER, 15, 1, 3,
                BiomeSelectors.spawnsOneOf(EntityType.TROPICAL_FISH)
                        .or(BiomeSelectors.tag(BiomeTags.IS_BEACH))
                        .or(BiomeSelectors.tag(BiomeTags.IS_OCEAN))
        );

        addSpawnEgg(PENGUIN, Items.POLAR_BEAR_SPAWN_EGG);
        addSpawnEgg(ELEPHANT, Items.DOLPHIN_SPAWN_EGG);
        addSpawnEgg(FIREMOTH, Items.PARROT_SPAWN_EGG);
        addSpawnEgg(BUTTERFLY, Items.PARROT_SPAWN_EGG);
        addSpawnEgg(CAPYBARA, Items.DONKEY_SPAWN_EGG);

        addSpawnEgg(MANTARAY, Items.WARDEN_SPAWN_EGG);
        addSpawnEgg(NAUTILUS, Items.HORSE_SPAWN_EGG);
        addSpawnEgg(TUNA, Items.COD_SPAWN_EGG);
        //addSpawnEgg(JELLYFISH, Items.SALMON_SPAWN_EGG);
        addSpawnEgg(LOBSTER, Items.PARROT_SPAWN_EGG);

        addSpawnEgg(SCULKLING, Items.WARDEN_SPAWN_EGG);
        addSpawnEgg(SNAKE, Items.PANDA_SPAWN_EGG);

        addSpawnEgg(SHOWMASTER, Items.ENDERMITE_SPAWN_EGG);

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
            .title(Component.literal("Toms Mobs").withStyle(ChatFormatting.DARK_GREEN))
            .icon(Items.BAT_SPAWN_EGG::getDefaultInstance)
            .displayItems((parameters, output) -> SPAWN_EGGS.values().forEach(output::accept))
            .build();
}
