package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.loader.AjBlueprintLoader;
import de.tomalbrc.bil.file.loader.BbModelLoader;
import de.tomalbrc.toms_mobs.TomsMobs;
import net.minecraft.resources.Identifier;

public class Util {
    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(TomsMobs.MODID, path);
    }

    public static Model loadModel(Identifier resourceLocation) {
        return AjBlueprintLoader.load(resourceLocation);
    }

    public static Model loadBbModel(Identifier resourceLocation) {
        return BbModelLoader.load(resourceLocation);
    }
}
