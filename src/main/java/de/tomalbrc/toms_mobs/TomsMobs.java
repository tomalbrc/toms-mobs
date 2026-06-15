package de.tomalbrc.toms_mobs;

import aqario.fowlplay.common.entity.ai.brain.sensor.FowlPlaySensorTypes;
import aqario.fowlplay.core.FowlPlayActivities;
import aqario.fowlplay.core.FowlPlayMemoryTypes;
import aqario.fowlplay.core.FowlPlaySchedules;
import com.mojang.logging.LogUtils;
import de.tomalbrc.bil.util.ResourcePackUtil;
import de.tomalbrc.toms_mobs.registry.ItemRegistry;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.registry.SoundRegistry;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

@Mod(TomsMobs.MODID)
public class TomsMobs {
    public static final String MODID = "toms_mobs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TomsMobs(IEventBus modEventBus) {
        onInitialize(modEventBus);
    }

    public void onInitialize(IEventBus modEventBus) {
        PolymerResourcePackUtils.addModAssets(MODID);
        PolymerResourcePackUtils.markAsRequired();

        FowlPlaySensorTypes.SENSOR_TYPES.register(modEventBus);
        FowlPlayMemoryTypes.MEMORY_MODULE_TYPES.register(modEventBus);
        FowlPlayActivities.ACTIVITIES.register(modEventBus);

        SoundRegistry.registerSounds();

        MobRegistry.registerContent();

        MobRegistry.ENTITY_TYPES.register(modEventBus);
        MobRegistry.ITEMS.register(modEventBus);
        MobRegistry.CREATIVE_MODE_TABS.register(modEventBus);

        ItemRegistry.registerItems();
        ItemRegistry.ITEMS.register(modEventBus);

        var overrides = List.of(
                "assets/bil/textures/item/butterfly/texture.png.mcmeta",
                "assets/bil/textures/item/butterfly/variant1.png.mcmeta",
                "assets/bil/textures/item/butterfly/variant2.png.mcmeta",
                "assets/bil/textures/item/sculkling/texture.png.mcmeta"
        );

        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(x -> {
            for (String override : overrides) {
                try (var resource = TomsMobs.class.getResourceAsStream("/"+override)){
                    if (resource != null) ResourcePackUtil.add(Identifier.withDefaultNamespace(override), resource.readAllBytes());
                } catch (IOException ignored) {

                }
            }
        });
    }
}
