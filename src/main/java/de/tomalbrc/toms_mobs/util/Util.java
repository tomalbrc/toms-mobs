package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.loader.AjModelLoader;
import de.tomalbrc.toms_mobs.TomsMobs;
import net.minecraft.resources.ResourceLocation;

public class Util {
    public static ResourceLocation id(String path) {
        return new ResourceLocation(TomsMobs.MODID, path);
    }

    public static Model loadModel(ResourceLocation resourceLocation) {
        return AjModelLoader.load(resourceLocation);
    }
}
