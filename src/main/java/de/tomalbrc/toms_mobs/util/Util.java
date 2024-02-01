package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.toms_mobs.TomsMobs;
import net.minecraft.resources.ResourceLocation;

public class Util {
    public static ResourceLocation id(String path) {
        return new ResourceLocation(TomsMobs.MODID, path);
    }
}
