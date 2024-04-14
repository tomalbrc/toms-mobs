package de.tomalbrc.toms_mobs;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import de.tomalbrc.toms_mobs.registries.MobRegistry;
import de.tomalbrc.toms_mobs.registries.SoundRegistry;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TomsMobs implements ModInitializer {
    public static final String MODID = "toms_mobs";

    @Override
    public void onInitialize() {
        forceAutohost();

        PolymerResourcePackUtils.addModAssets(MODID);
        PolymerResourcePackUtils.markAsRequired();

        SoundRegistry.registerSounds();
        MobRegistry.registerMobs();
    }

    void forceAutohost() {
        try {
            Path path = FabricLoader.getInstance().getGameDir().resolve("polymer/.force_autohost");
            Files.createDirectories(path.getParent()); // Create parent directories if they don't exist
            Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
