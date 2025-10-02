package de.tomalbrc.toms_mobs;

import de.tomalbrc.toms_mobs.entity.effect.CustomMobEffects;
import de.tomalbrc.toms_mobs.registry.ItemRegistry;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.registry.SoundRegistry;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

public class TomsMobs implements ModInitializer {
    public static final String MODID = "toms_mobs";

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(MODID);
        PolymerResourcePackUtils.markAsRequired();

        CustomMobEffects.init();

        SoundRegistry.registerSounds();
        MobRegistry.registerMobs();
        ItemRegistry.registerItems();
    }
}
