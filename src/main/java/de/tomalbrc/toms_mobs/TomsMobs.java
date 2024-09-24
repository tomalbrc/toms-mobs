package de.tomalbrc.toms_mobs;

import de.tomalbrc.toms_mobs.registries.ItemRegistry;
import de.tomalbrc.toms_mobs.registries.MobRegistry;
import de.tomalbrc.toms_mobs.registries.SoundRegistry;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

public class TomsMobs implements ModInitializer {
    public static final String MODID = "toms_mobs";

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(MODID);
        PolymerResourcePackUtils.markAsRequired();

        SoundRegistry.registerSounds();
        MobRegistry.registerMobs();
        ItemRegistry.registerItems();
    }
}
