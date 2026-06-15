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
import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.RegistryOps;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = TomsMobs.MODID)
public class MobRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, TomsMobs.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, TomsMobs.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TomsMobs.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<Penguin>> PENGUIN = registerEntity(Penguin.ID, EntityType.Builder.of(Penguin::new, MobCategory.CREATURE).sized(0.6f, 1.05f));
    public static final DeferredHolder<EntityType<?>, EntityType<Elephant>> ELEPHANT = registerEntity(Elephant.ID, EntityType.Builder.of(Elephant::new, MobCategory.CREATURE).sized(2.99f, 3.65f));
    public static final DeferredHolder<EntityType<?>, EntityType<Capybara>> CAPYBARA = registerEntity(Capybara.ID, EntityType.Builder.of(Capybara::new, MobCategory.CREATURE).sized(0.9f, 1.f));
    public static final DeferredHolder<EntityType<?>, EntityType<Possum>> POSSUM = registerEntity(Possum.ID, EntityType.Builder.of(Possum::new, MobCategory.CREATURE).sized(0.9f, 1.f));
    public static final DeferredHolder<EntityType<?>, EntityType<Seagull>> SEAGULL = registerEntity(Seagull.ID, EntityType.Builder.of(Seagull::new, MobCategory.CREATURE).sized(0.6f, 0.8f).eyeHeight(0.7f));
    public static final DeferredHolder<EntityType<?>, EntityType<Mantaray>> MANTARAY = registerEntity(Mantaray.ID, EntityType.Builder.of(Mantaray::new, MobCategory.WATER_CREATURE).sized(1.4f, 0.4f));
    public static final DeferredHolder<EntityType<?>, EntityType<Tuna>> TUNA = registerEntity(Tuna.ID, EntityType.Builder.of(Tuna::new, MobCategory.WATER_AMBIENT).sized(0.55f, 0.55f));
    public static final DeferredHolder<EntityType<?>, EntityType<Lobster>> LOBSTER = registerEntity(Lobster.ID, EntityType.Builder.of(Lobster::new, MobCategory.WATER_CREATURE).sized(0.65f, 0.35f));
    public static final DeferredHolder<EntityType<?>, EntityType<Firemoth>> FIREMOTH = registerEntity(Firemoth.ID, EntityType.Builder.of(Firemoth::new, MobCategory.AMBIENT).sized(0.5f, 0.5f));
    public static final DeferredHolder<EntityType<?>, EntityType<Butterfly>> BUTTERFLY = registerEntity(Butterfly.ID, EntityType.Builder.of(Butterfly::new, MobCategory.AMBIENT).sized(0.25f, 0.25f));
    public static final DeferredHolder<EntityType<?>, EntityType<LargeButterfly>> EMPEROR_BUTTERFLY = registerEntity(Util.id("emperor_butterfly"), EntityType.Builder.of(LargeButterfly::new, MobCategory.AMBIENT).sized(0.6f, 0.6f));
    public static final DeferredHolder<EntityType<?>, EntityType<Snake>> SNAKE = registerEntity(Snake.ID, EntityType.Builder.of(Snake::new, MobCategory.CREATURE).sized(0.9f, 0.4f));
    public static final DeferredHolder<EntityType<?>, EntityType<Sculkling>> SCULKLING = registerEntity(Sculkling.ID, EntityType.Builder.of(Sculkling::new, MobCategory.MONSTER).sized(0.5f, 0.9f));
    public static final DeferredHolder<EntityType<?>, EntityType<Showmaster>> SHOWMASTER = registerEntity(Showmaster.ID, EntityType.Builder.of(Showmaster::new, MobCategory.MONSTER).sized(0.7f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<Iceologer>> ICEOLOGER = registerEntity(Iceologer.ID, EntityType.Builder.of(Iceologer::new, MobCategory.MONSTER).sized(0.7f, 1.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSpike>> ICE_SPIKE = registerEntity(IceSpike.ID, EntityType.Builder.of(IceSpike::new, MobCategory.MISC).sized(1.f, 2.f));
    public static final DeferredHolder<EntityType<?>, EntityType<IceSpikeSmall>> ICE_SPIKE_SMALL = registerEntity(IceSpikeSmall.ID, EntityType.Builder.of(IceSpikeSmall::new, MobCategory.MISC).sized(1.2f, 0.8f));
    public static final DeferredHolder<EntityType<?>, EntityType<IceCluster>> ICE_CLUSTER = registerEntity(IceCluster.ID, EntityType.Builder.of(IceCluster::new, MobCategory.MISC).sized(2, 1));

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(Identifier id, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(id.getPath(), () -> {
            EntityType<T> type = builder.build(ResourceKey.create(Registries.ENTITY_TYPE, id));
            PolymerEntityUtils.registerType(type);
            return type;
        });
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEM_GROUP = CREATIVE_MODE_TABS.register("spawn_eggs", () -> {
        CreativeModeTab tab = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, -1)
                .title(Component.literal("Toms Mobs").withStyle(ChatFormatting.DARK_GREEN))
                .icon(Items.BAT_SPAWN_EGG::getDefaultInstance)
                .displayItems((parameters, output) -> ITEMS.getEntries().forEach(holder -> output.accept(holder.get())))
                .build();
        PolymerCreativeModeTabUtils.registerPolymerCreativeModeTab(Util.id("spawn-eggs"), tab);
        return tab;
    });

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(PENGUIN.get(), Penguin.createAttributes().build());
        event.put(ELEPHANT.get(), Elephant.createAttributes().build());
        event.put(CAPYBARA.get(), Capybara.createAttributes().build());
        event.put(POSSUM.get(), Possum.createAttributes().build());
        event.put(SEAGULL.get(), Seagull.createAttributes().build());
        event.put(MANTARAY.get(), Mantaray.createAttributes().build());
        event.put(TUNA.get(), Tuna.createAttributes().build());
        event.put(LOBSTER.get(), Lobster.createAttributes().build());
        event.put(FIREMOTH.get(), Firemoth.createAttributes().build());
        event.put(BUTTERFLY.get(), Butterfly.createAttributes().build());
        event.put(EMPEROR_BUTTERFLY.get(), LargeButterfly.createAttributes().build());
        event.put(SNAKE.get(), Snake.createAttributes().build());
        event.put(SCULKLING.get(), Sculkling.createAttributes().build());
        event.put(SHOWMASTER.get(), Showmaster.createAttributes().build());
        event.put(ICEOLOGER.get(), Iceologer.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(PENGUIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ELEPHANT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CAPYBARA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(POSSUM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SEAGULL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FlyingBirdEntity::canSpawnShorebirds, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(MANTARAY.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mantaray::checkRareDeepWaterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TUNA.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.OCEAN_FLOOR, Tuna::checkDeepWaterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(LOBSTER.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.OCEAN_FLOOR, (xx, levelAccessor, z, blockPos, r) -> r.nextInt(4) == 2 && blockPos.getY() < levelAccessor.getSeaLevel() + 3 && (TurtleEggBlock.onSand(levelAccessor, blockPos) || levelAccessor.getBlockState(blockPos).getFluidState().is(FluidTags.WATER)) && levelAccessor.getRawBrightness(blockPos, 0) > 1, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(FIREMOTH.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Firemoth::checkFiremothSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BUTTERFLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Butterfly::checkButterflySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EMPEROR_BUTTERFLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, LargeButterfly::checkLargeButterflySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SNAKE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SCULKLING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Sculkling::checkSculklingSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SHOWMASTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Showmaster::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ICEOLOGER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Iceologer::checkIceologerSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static void registerMobs(RegistryAccess.Frozen layeredRegistryAccess) {
        var biomeLookup = layeredRegistryAccess.lookupOrThrow(Registries.BIOME);
        var res = ConfiguredSpawn.CODEC.codec().listOf().decode(RegistryOps.create(JsonOps.INSTANCE, layeredRegistryAccess), ModConfig.getInstance().spawnsJson);
        res.ifError(x -> TomsMobs.LOGGER.info("Could not decode spawn data! {}", x.message()));

        if (res.hasResultOrPartial()) {
            var list = res.getPartialOrThrow().getFirst();
            for (ConfiguredSpawn config : list) {
                List<Holder<@NotNull Biome>> biomes = new ArrayList<>();

                for (String biome : config.biomes()) {
                    if (!biome.startsWith("#")) {
                        var ughWhyMojank = biomeLookup.get(ResourceKey.create(Registries.BIOME, Identifier.parse(biome)));
                        ughWhyMojank.ifPresent(biomes::add);
                    } else {
                        var ughWhyMojank = biomeLookup.get(TagKey.create(Registries.BIOME, Identifier.parse(biome.substring(1))));
                        ughWhyMojank.ifPresent(x -> biomes.addAll(x.stream().toList()));
                    }
                }

                BiomeHelper.addSpawn(BuiltInRegistries.ENTITY_TYPE.getValue(config.mob()), config.weight(), config.minGroup(), config.maxGroup(), context -> biomes.contains(context.getBiomeHolder()));
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
        addSpawnEgg(LOBSTER, Items.PARROT_SPAWN_EGG);

        addSpawnEgg(SCULKLING, Items.WARDEN_SPAWN_EGG);
        addSpawnEggModeled(SNAKE, Util.id("snake_spawn_egg"));
        addSpawnEgg(SHOWMASTER, Items.ENDERMITE_SPAWN_EGG);
        addSpawnEgg(ICEOLOGER, Items.VEX_SPAWN_EGG);
    }

    private static void addSpawnEggModeled(DeferredHolder<EntityType<?>, ? extends EntityType<? extends Mob>> typeHolder, Identifier model) {
        String path = typeHolder.getId().getPath() + "_spawn_egg";
        Identifier itemIdentifier = Util.id(path);
        ITEMS.register(path, () -> new TexturedPolymerSpawnEggItem(
                typeHolder.get(),
                new Item.Properties().stacksTo(64).setId(ResourceKey.create(Registries.ITEM, itemIdentifier)),
                model
        ));
    }

    private static void addSpawnEgg(DeferredHolder<EntityType<?>, ? extends EntityType<? extends Mob>> typeHolder, Item vanillaItem) {
        String path = typeHolder.getId().getPath() + "_spawn_egg";
        Identifier itemIdentifier = Util.id(path);
        ITEMS.register(path, () -> new VanillaPolymerSpawnEggItem(
                typeHolder.get(),
                vanillaItem,
                new Item.Properties().stacksTo(64).setId(ResourceKey.create(Registries.ITEM, itemIdentifier))
        ));
    }
}