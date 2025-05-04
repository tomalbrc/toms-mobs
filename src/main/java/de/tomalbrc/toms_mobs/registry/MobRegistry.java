package de.tomalbrc.toms_mobs.registry;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import de.tomalbrc.toms_mobs.ModConfig;
import de.tomalbrc.toms_mobs.entity.hostile.*;
import de.tomalbrc.toms_mobs.entity.passive.*;
import de.tomalbrc.toms_mobs.item.VanillaPolymerSpawnEggItem;
import de.tomalbrc.toms_mobs.util.BiomeHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;
import java.util.function.Function;

public class MobRegistry {
    public static final EntityType<Penguin> PENGUIN = register(
            Penguin.ID,
            FabricEntityType.Builder.createMob(Penguin::new, MobCategory.CREATURE, x -> x
                            .defaultAttributes(Penguin::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules))
                    .sized(0.6f, 1.05f)
    );

    public static final EntityType<Elephant> ELEPHANT = register(
            Elephant.ID,
            FabricEntityType.Builder.createMob(Elephant::new, MobCategory.CREATURE, x -> x
                            .defaultAttributes(Elephant::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules))
                    .sized(3.0f, 3.65f)
    );

    public static final EntityType<Capybara> CAPYBARA = register(
            Capybara.ID,
            FabricEntityType.Builder.createMob(Capybara::new, MobCategory.CREATURE, x -> x
                            .defaultAttributes(Capybara::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules))
                    .sized(0.9f, 1.1f)
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
*/
    public static final EntityType<Seagull> SEAGULL = register(
            Seagull.ID,
            FabricEntityType.Builder.createMob(Seagull::new, MobCategory.CREATURE, x -> x
                            .defaultAttributes(Seagull::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules))
                    .sized(0.8f, 0.9f)
    );

    public static final EntityType<Nautilus> NAUTILUS = register(
            Nautilus.ID,
            FabricEntityType.Builder.createMob(Nautilus::new, MobCategory.WATER_CREATURE, x -> x
                            .defaultAttributes(Nautilus::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules))
                    .sized(0.5f, 0.5f)
    );

    public static final EntityType<Mantaray> MANTARAY = register(
            Mantaray.ID,
            FabricEntityType.Builder.createMob(Mantaray::new, MobCategory.WATER_CREATURE, x -> x
                            .defaultAttributes(Mantaray::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules))
                    .sized(1.4f, 0.4f)
    );

    public static final EntityType<Tuna> TUNA = register(
            Tuna.ID,
            FabricEntityType.Builder.createMob(Tuna::new, MobCategory.WATER_AMBIENT, x -> x
                            .defaultAttributes(Tuna::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.IN_WATER, Heightmap.Types.OCEAN_FLOOR, WaterAnimal::checkSurfaceWaterAnimalSpawnRules))
                    .sized(0.55f, 0.55f)
    );

    public static final EntityType<Lobster> LOBSTER = register(
            Lobster.ID,
            FabricEntityType.Builder.createMob(Lobster::new, MobCategory.WATER_AMBIENT, x -> x
                            .defaultAttributes(Lobster::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.OCEAN_FLOOR, (xx, y, z, t, r) -> true))
                    .sized(0.65f, 0.35f)
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
            FabricEntityType.Builder.createMob(Firemoth::new, MobCategory.AMBIENT, x -> x
                            .defaultAttributes(Firemoth::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Firemoth::checkFiremothSpawnRules))
                    .sized(0.5f, 0.5f)
    );

    public static final EntityType<Butterfly> BUTTERFLY = register(
            Butterfly.ID,
            FabricEntityType.Builder.createMob(Butterfly::new, MobCategory.AMBIENT, x -> x
                            .defaultAttributes(Butterfly::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Butterfly::checkButterflySpawnRules))
                    .sized(0.25f, 0.25f)
    );

    public static final EntityType<Snake> SNAKE = register(
            Snake.ID,
            FabricEntityType.Builder.createMob(Snake::new, MobCategory.MONSTER, x -> x
                            .defaultAttributes(Snake::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Snake::checkSnakeSpawnRules))
                    .sized(0.9f, 0.4f)
    );

    public static final EntityType<Sculkling> SCULKLING = register(
            Sculkling.ID,
            FabricEntityType.Builder.createMob(Sculkling::new, MobCategory.MONSTER, x -> x
                            .defaultAttributes(Sculkling::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Sculkling::checkSculklingSpawnRules))
                    .sized(0.5f, 0.9f)
    );

    public static final EntityType<Showmaster> SHOWMASTER = register(
            Showmaster.ID,
            FabricEntityType.Builder.createMob(Showmaster::new, MobCategory.MONSTER, x -> x
                            .defaultAttributes(Showmaster::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Showmaster::checkMobSpawnRules))
                    .sized(0.7f, 1.8f)
    );

    public static final EntityType<Iceologer> ICEOLOGER = register(
            Iceologer.ID,
            FabricEntityType.Builder.createMob(Iceologer::new, MobCategory.MONSTER, x -> x
                            .defaultAttributes(Iceologer::createAttributes)
                            .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Iceologer::checkIceologerSpawnRules))
                    .sized(0.7f, 1.8f)
    );

    public static final EntityType<IceSpike> ICE_SPIKE = register(
            IceSpike.ID,
            EntityType.Builder.of(IceSpike::new, MobCategory.MISC)
                    .sized(1.f, 2.f)
    );

    public static final EntityType<IceSpikeSmall> ICE_SPIKE_SMALL = register(
            IceSpikeSmall.ID,
            EntityType.Builder.of(IceSpikeSmall::new, MobCategory.MISC)
                    .sized(1.2f, 0.8f)
    );

    public static final EntityType<IceCluster> ICE_CLUSTER = register(
            IceCluster.ID,
            EntityType.Builder.of(IceCluster::new, MobCategory.MISC)
                    .sized(2, 1)
    );

    private static <T extends Entity> EntityType<T> register(ResourceLocation id, EntityType.Builder<T> builder) {
        @SuppressWarnings("unchecked") Map<String, Type<?>> types = (Map<String, Type<?>>) DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getDataVersion().getVersion())).findChoiceType(References.ENTITY).types();
        types.put(id.toString(), types.get(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE).toString()));

        EntityType<T> type = builder.build(ResourceKey.create(Registries.ENTITY_TYPE, id));
        PolymerEntityUtils.registerType(type);

        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
    }

    public static void registerMobs() {
        if (!ModConfig.getInstance().disabledMobs.contains(Penguin.ID))
            BiomeHelper.addSpawn(PENGUIN, 15, 2, 5, BiomeSelectors.spawnsOneOf(EntityType.POLAR_BEAR)
                    .or(BiomeSelectors.tag(BiomeTags.SPAWNS_SNOW_FOXES))
                    .or(BiomeSelectors.tag(BiomeTags.HAS_IGLOO))
                    .or(BiomeSelectors.includeByKey(Biomes.SNOWY_BEACH, Biomes.ICE_SPIKES))
            );

        if (!ModConfig.getInstance().disabledMobs.contains(Snake.ID))
            BiomeHelper.addSpawn(SNAKE, 15, 2, 4, BiomeSelectors.spawnsOneOf(EntityType.HUSK)
                    .or(BiomeSelectors.tag(ConventionalBiomeTags.IS_JUNGLE))
                    .or(BiomeSelectors.tag(BiomeTags.HAS_DESERT_PYRAMID))
                    .or(BiomeSelectors.tag(BiomeTags.HAS_VILLAGE_DESERT))
                    .or(BiomeSelectors.tag(BiomeTags.HAS_RUINED_PORTAL_DESERT))
                    .or(BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP))
            );

        if (!ModConfig.getInstance().disabledMobs.contains(Elephant.ID))
            BiomeHelper.addSpawn(ELEPHANT, 20, 1, 3, BiomeSelectors.includeByKey(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU).or(BiomeSelectors.tag(ConventionalBiomeTags.IS_JUNGLE)));

        if (!ModConfig.getInstance().disabledMobs.contains(Sculkling.ID))
            BiomeHelper.addSpawn(SCULKLING, 20, 2, 4, BiomeSelectors.spawnsOneOf(EntityType.ZOMBIE).and(BiomeSelectors.excludeByKey(Biomes.LUSH_CAVES)));

        if (!ModConfig.getInstance().disabledMobs.contains(Firemoth.ID))
            BiomeHelper.addSpawn(FIREMOTH, 5, 2, 3, BiomeSelectors.foundInTheNether()
                    .and(BiomeSelectors.excludeByKey(Biomes.BASALT_DELTAS))
            );

        if (!ModConfig.getInstance().disabledMobs.contains(Butterfly.ID))
            BiomeHelper.addSpawn(BUTTERFLY, 25, 2, 5, BiomeSelectors.foundInOverworld()
                    .and(BiomeHelper.excludeTag(ConventionalBiomeTags.IS_OCEAN).or(BiomeHelper.excludeTag(ConventionalBiomeTags.IS_RIVER)).or(BiomeHelper.excludeTag(BiomeTags.SPAWNS_SNOW_FOXES)))
            );

        if (!ModConfig.getInstance().disabledMobs.contains(Capybara.ID))
            BiomeHelper.addSpawn(CAPYBARA, 10, 1, 3, BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.RIVER));

        // Icy
        if (!ModConfig.getInstance().disabledMobs.contains(Iceologer.ID))
            BiomeHelper.addSpawn(ICEOLOGER, 1, 1, 3, BiomeSelectors.foundInOverworld().and(BiomeSelectors.tag(ConventionalBiomeTags.IS_MOUNTAIN)));

        if (!ModConfig.getInstance().disabledMobs.contains(Mantaray.ID))
            BiomeHelper.addSpawn(MANTARAY, 6, 1, 1, BiomeSelectors.tag(ConventionalBiomeTags.IS_OCEAN));
        if (!ModConfig.getInstance().disabledMobs.contains(Tuna.ID))
            BiomeHelper.addSpawn(TUNA, 5, 1, 2, BiomeSelectors.tag(ConventionalBiomeTags.IS_OCEAN));
        if (!ModConfig.getInstance().disabledMobs.contains(Nautilus.ID))
            BiomeHelper.addSpawn(NAUTILUS, 4, 1, 1, BiomeSelectors.tag(ConventionalBiomeTags.IS_OCEAN));

        if (!ModConfig.getInstance().disabledMobs.contains(Lobster.ID)) BiomeHelper.addSpawn(LOBSTER, 10, 1, 3,
                BiomeSelectors.spawnsOneOf(EntityType.TROPICAL_FISH).or(BiomeSelectors.spawnsOneOf(EntityType.TURTLE))
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.IS_BEACH))
                        .or(BiomeSelectors.tag(ConventionalBiomeTags.IS_OCEAN))
        );

        addSpawnEgg(PENGUIN, Items.POLAR_BEAR_SPAWN_EGG);
        addSpawnEgg(ELEPHANT, Items.DOLPHIN_SPAWN_EGG);
        addSpawnEgg(FIREMOTH, Items.PARROT_SPAWN_EGG);
        addSpawnEgg(SEAGULL, Items.CAT_SPAWN_EGG);
        addSpawnEgg(BUTTERFLY, Items.ENDER_DRAGON_SPAWN_EGG);
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

        PolymerItemGroupUtils.registerPolymerItemGroup(Util.id("spawn-eggs"), ITEM_GROUP);
    }

    private static void addSpawnEgg(EntityType<? extends Mob> type, Item vanillaItem) {
        register(Util.id(EntityType.getKey(type).getPath() + "_spawn_egg"), properties -> new VanillaPolymerSpawnEggItem(type, vanillaItem, properties));
    }

    static public <T extends Item> void register(ResourceLocation identifier, Function<Item.Properties, T> function) {
        var x = function.apply(new Item.Properties().stacksTo(64).setId(ResourceKey.create(Registries.ITEM, identifier)));
        Registry.register(BuiltInRegistries.ITEM, identifier, x);
        SPAWN_EGGS.putIfAbsent(identifier, x);
    }

    public static final Object2ObjectOpenHashMap<ResourceLocation, Item> SPAWN_EGGS = new Object2ObjectOpenHashMap<>();
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab.Builder(null, -1)
            .title(Component.literal("Toms Mobs").withStyle(ChatFormatting.DARK_GREEN))
            .icon(Items.BAT_SPAWN_EGG::getDefaultInstance)
            .displayItems((parameters, output) -> SPAWN_EGGS.values().forEach(output::accept))
            .build();
}
