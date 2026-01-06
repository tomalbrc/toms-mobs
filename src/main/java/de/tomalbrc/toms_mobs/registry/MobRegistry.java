package de.tomalbrc.toms_mobs.registry;

import aqario.fowlplay.common.entity.bird.FlyingBirdEntity;
import com.mojang.serialization.JsonOps;
import de.tomalbrc.toms_mobs.TomsMobs;
import de.tomalbrc.toms_mobs.config.ConfiguredSpawn;
import de.tomalbrc.toms_mobs.config.ModConfig;
import de.tomalbrc.toms_mobs.entity.hostile.*;
import de.tomalbrc.toms_mobs.entity.passive.*;
import de.tomalbrc.toms_mobs.entity.passive.butterfly.Butterfly;
import de.tomalbrc.toms_mobs.entity.passive.butterfly.LargeButterfly;
import de.tomalbrc.toms_mobs.item.TexturedPolymerSpawnEggItem;
import de.tomalbrc.toms_mobs.item.VanillaPolymerSpawnEggItem;
import de.tomalbrc.toms_mobs.util.BiomeHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MobRegistry {
    public static final EntityType<@NotNull Penguin> PENGUIN = register(Penguin.ID, FabricEntityType.Builder.createMob(Penguin::new, MobCategory.CREATURE, x -> x.defaultAttributes(Penguin::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)).sized(0.6f, 1.05f));

    public static final EntityType<@NotNull Elephant> ELEPHANT = register(Elephant.ID, FabricEntityType.Builder.createMob(Elephant::new, MobCategory.CREATURE, x -> x.defaultAttributes(Elephant::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules)).sized(2.99f, 3.65f));

    public static final EntityType<@NotNull Capybara> CAPYBARA = register(Capybara.ID, FabricEntityType.Builder.createMob(Capybara::new, MobCategory.CREATURE, x -> x.defaultAttributes(Capybara::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules)).sized(0.9f, 1.f));

    public static final EntityType<@NotNull Possum> POSSUM = register(Possum.ID, FabricEntityType.Builder.createMob(Possum::new, MobCategory.CREATURE, x -> x.defaultAttributes(Possum::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules)).sized(0.9f, 1.f));

    /*public static final EntityType<Vulture> VULTURE = register(
            Vulture.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Vulture::new)
                    .spawnGroup(MobCategory.CREATURE)
                    .dimensions(EntityDimensions.scalable(1.f, 1.f))
                    .defaultAttributes(Vulture::createAttributes)
                    .spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules)
    );
*/
    public static final EntityType<@NotNull Seagull> SEAGULL = register(Seagull.ID, FabricEntityType.Builder.createMob(Seagull::new, MobCategory.CREATURE, x -> x.defaultAttributes(Seagull::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FlyingBirdEntity::canSpawnShorebirds)).sized(0.6f, 0.8f).eyeHeight(0.7f));

    public static final EntityType<@NotNull Mantaray> MANTARAY = register(Mantaray.ID, FabricEntityType.Builder.createMob(Mantaray::new, MobCategory.WATER_CREATURE, x -> x.defaultAttributes(Mantaray::createAttributes).spawnRestriction(SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mantaray::checkRareDeepWaterSpawnRules)).sized(1.4f, 0.4f));

    public static final EntityType<@NotNull Tuna> TUNA = register(Tuna.ID, FabricEntityType.Builder.createMob(Tuna::new, MobCategory.WATER_AMBIENT, x -> x.defaultAttributes(Tuna::createAttributes).spawnRestriction(SpawnPlacementTypes.IN_WATER, Heightmap.Types.OCEAN_FLOOR, Tuna::checkDeepWaterSpawnRules)).sized(0.55f, 0.55f));

    public static final EntityType<@NotNull Lobster> LOBSTER = register(Lobster.ID, FabricEntityType.Builder.createMob(Lobster::new, MobCategory.WATER_CREATURE, x -> x.defaultAttributes(Lobster::createAttributes).spawnRestriction(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.OCEAN_FLOOR, (xx, levelAccessor, z, blockPos, r) -> r.nextInt(4) == 2 && blockPos.getY() < levelAccessor.getSeaLevel() + 3 && (TurtleEggBlock.onSand(levelAccessor, blockPos) || levelAccessor.getBlockState(blockPos).getFluidState().is(FluidTags.WATER)) && levelAccessor.getRawBrightness(blockPos, 0) > 1)).sized(0.65f, 0.35f));

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

    public static final EntityType<@NotNull Firemoth> FIREMOTH = register(Firemoth.ID, FabricEntityType.Builder.createMob(Firemoth::new, MobCategory.AMBIENT, x -> x.defaultAttributes(Firemoth::createAttributes).spawnRestriction(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Firemoth::checkFiremothSpawnRules)).sized(0.5f, 0.5f));

    public static final EntityType<@NotNull Butterfly> BUTTERFLY = register(Butterfly.ID, FabricEntityType.Builder.createMob(Butterfly::new, MobCategory.AMBIENT, x -> x.defaultAttributes(Butterfly::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Butterfly::checkButterflySpawnRules)).sized(0.25f, 0.25f));

    public static final EntityType<@NotNull LargeButterfly> EMPEROR_BUTTERFLY = register(Util.id("emperor_butterfly"), FabricEntityType.Builder.createMob(LargeButterfly::new, MobCategory.AMBIENT, x -> x.defaultAttributes(LargeButterfly::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, LargeButterfly::checkLargeButterflySpawnRules)).sized(0.6f, 0.6f));

    public static final EntityType<@NotNull Snake> SNAKE = register(Snake.ID, FabricEntityType.Builder.createMob(Snake::new, MobCategory.CREATURE, x -> x.defaultAttributes(Snake::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules)).sized(0.9f, 0.4f));

    public static final EntityType<@NotNull Sculkling> SCULKLING = register(Sculkling.ID, FabricEntityType.Builder.createMob(Sculkling::new, MobCategory.MONSTER, x -> x.defaultAttributes(Sculkling::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Sculkling::checkSculklingSpawnRules)).sized(0.5f, 0.9f));

    public static final EntityType<@NotNull Showmaster> SHOWMASTER = register(Showmaster.ID, FabricEntityType.Builder.createMob(Showmaster::new, MobCategory.MONSTER, x -> x.defaultAttributes(Showmaster::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Showmaster::checkMobSpawnRules)).sized(0.7f, 1.8f));

    public static final EntityType<@NotNull Iceologer> ICEOLOGER = register(Iceologer.ID, FabricEntityType.Builder.createMob(Iceologer::new, MobCategory.MONSTER, x -> x.defaultAttributes(Iceologer::createAttributes).spawnRestriction(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Iceologer::checkIceologerSpawnRules)).sized(0.7f, 1.8f));

    public static final EntityType<@NotNull IceSpike> ICE_SPIKE = register(IceSpike.ID, EntityType.Builder.of(IceSpike::new, MobCategory.MISC).sized(1.f, 2.f));

    public static final EntityType<@NotNull IceSpikeSmall> ICE_SPIKE_SMALL = register(IceSpikeSmall.ID, EntityType.Builder.of(IceSpikeSmall::new, MobCategory.MISC).sized(1.2f, 0.8f));

    public static final EntityType<@NotNull IceCluster> ICE_CLUSTER = register(IceCluster.ID, EntityType.Builder.of(IceCluster::new, MobCategory.MISC).sized(2, 1));

    private static <T extends Entity> EntityType<@NotNull T> register(Identifier id, EntityType.Builder<@NotNull T> builder) {
        EntityType<@NotNull T> type = builder.build(ResourceKey.create(Registries.ENTITY_TYPE, id));
        PolymerEntityUtils.registerType(type);

        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
    }

    public static void registerMobs(LayeredRegistryAccess<@NotNull RegistryLayer> layeredRegistryAccess) {
        var lookup = HolderLookup.Provider.create(layeredRegistryAccess.compositeAccess().listRegistries());
        var biomeLookup = HolderLookup.Provider.create(layeredRegistryAccess.compositeAccess().listRegistries()).lookup(Registries.BIOME).get();
        var res = ConfiguredSpawn.CODEC.codec().listOf().decode(RegistryOps.create(JsonOps.INSTANCE, lookup), ModConfig.getInstance().spawnsJson);
        res.ifError(x -> TomsMobs.LOGGER.info("Could not decode spawn data! {}", x.message()));

        if (res.hasResultOrPartial()) {
            var list = res.getPartialOrThrow().getFirst();
            for (ConfiguredSpawn config : list) {
                List<Holder<@NotNull Biome>> biomes = new ArrayList<>();

                for (String biome : config.biomes()) {
                    if (!biome.startsWith("#")) {
                        var ughWhyMojank = biomeLookup.get(ResourceKey.create(Registries.BIOME, Identifier.parse(biome)));
                        ughWhyMojank.ifPresent(biomes::add);
                    } else if (biome.startsWith("#")) {
                        var ughWhyMojank = biomeLookup.get(TagKey.create(Registries.BIOME, Identifier.parse(biome.substring(1))));
                        ughWhyMojank.ifPresent(x -> biomes.addAll(x.stream().toList()));
                    }
                }

                BiomeHelper.addSpawn(BuiltInRegistries.ENTITY_TYPE.getValue(config.mob()), config.weight(), config.minGroup(), config.maxGroup(), context -> biomes.contains(context.getBiomeRegistryEntry()));
            }
        }
    }

    public static void registerContent() {
        addSpawnEgg(PENGUIN, Items.POLAR_BEAR_SPAWN_EGG);
        addSpawnEggModeled(ELEPHANT, Util.id("elephant_spawn_egg"));
        addSpawnEgg(FIREMOTH, Items.PARROT_SPAWN_EGG);
        addSpawnEgg(SEAGULL, Items.CAT_SPAWN_EGG);
        addSpawnEgg(BUTTERFLY, Items.ENDER_DRAGON_SPAWN_EGG);
        addSpawnEgg(EMPEROR_BUTTERFLY, Items.ENDER_DRAGON_SPAWN_EGG);
        addSpawnEgg(POSSUM, Items.CAMEL_SPAWN_EGG);
        addSpawnEggModeled(CAPYBARA, Util.id("capybara_spawn_egg"));

        addSpawnEgg(MANTARAY, Items.WARDEN_SPAWN_EGG);
        addSpawnEgg(TUNA, Items.COD_SPAWN_EGG);
        //addSpawnEgg(JELLYFISH, Items.SALMON_SPAWN_EGG);
        addSpawnEgg(LOBSTER, Items.PARROT_SPAWN_EGG);

        addSpawnEgg(SCULKLING, Items.WARDEN_SPAWN_EGG);
        addSpawnEggModeled(SNAKE, Util.id("snake_spawn_egg"));

        addSpawnEgg(SHOWMASTER, Items.ENDERMITE_SPAWN_EGG);

        addSpawnEgg(ICEOLOGER, Items.VEX_SPAWN_EGG);

        PolymerItemGroupUtils.registerPolymerItemGroup(Util.id("spawn-eggs"), ITEM_GROUP);
    }

    private static void addSpawnEggModeled(EntityType<? extends @NotNull Mob> type, Identifier model) {
        register(Util.id(EntityType.getKey(type).getPath() + "_spawn_egg"), properties -> new TexturedPolymerSpawnEggItem(type, properties, model));
    }

    private static void addSpawnEgg(EntityType<? extends @NotNull Mob> type, Item vanillaItem) {
        register(Util.id(EntityType.getKey(type).getPath() + "_spawn_egg"), properties -> new VanillaPolymerSpawnEggItem(type, vanillaItem, properties));
    }

    static public <T extends Item> void register(Identifier identifier, Function<Item.Properties, T> function) {
        var x = function.apply(new Item.Properties().stacksTo(64).setId(ResourceKey.create(Registries.ITEM, identifier)));
        Registry.register(BuiltInRegistries.ITEM, identifier, x);
        SPAWN_EGGS.putIfAbsent(identifier, x);
    }

    public static final Object2ObjectOpenHashMap<Identifier, Item> SPAWN_EGGS = new Object2ObjectOpenHashMap<>();
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, -1).title(Component.literal("Toms Mobs").withStyle(ChatFormatting.DARK_GREEN)).icon(Items.BAT_SPAWN_EGG::getDefaultInstance).displayItems((parameters, output) -> SPAWN_EGGS.values().forEach(output::accept)).build();
}
