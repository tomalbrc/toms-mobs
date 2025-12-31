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
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class TomsMobs implements ModInitializer {
    public static final String MODID = "toms_mobs";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(MODID);
        PolymerResourcePackUtils.markAsRequired();

        FowlPlaySchedules.init();
        FowlPlaySensorTypes.init();
        FowlPlayMemoryTypes.init();
        FowlPlayActivities.init();

        SoundRegistry.registerSounds();
        MobRegistry.registerContent();
        ItemRegistry.registerItems();

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
