package de.tomalbrc.toms_mobs;

import de.tomalbrc.bil.util.ResourcePackUtil;
import de.tomalbrc.toms_mobs.entity.effect.CustomMobEffects;
import de.tomalbrc.toms_mobs.registry.ItemRegistry;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.registry.SoundRegistry;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.List;

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

        var overrides = List.of(
                "assets/bil/textures/item/butterfly/texture.png.mcmeta",
                "assets/bil/textures/item/butterfly/variant1.png.mcmeta",
                "assets/bil/textures/item/butterfly/variant2.png.mcmeta",
                "assets/bil/textures/item/sculkling/texture.png.mcmeta"
        );

        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(x -> {
            for (String override : overrides) {
                try {
                    ResourcePackUtil.add(ResourceLocation.withDefaultNamespace(override), TomsMobs.class.getResourceAsStream("/"+override).readAllBytes());
                } catch (IOException ignored) {}
            }
        });
    }
}
