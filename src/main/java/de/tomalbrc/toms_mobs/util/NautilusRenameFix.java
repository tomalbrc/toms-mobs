package de.tomalbrc.toms_mobs.util;

import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.SimplestEntityRenameFix;
import org.jetbrains.annotations.NotNull;

public class NautilusRenameFix extends SimplestEntityRenameFix {
    public NautilusRenameFix(Schema outputSchema, boolean changesType) {
        super("NautilusRenameFix", outputSchema, changesType);
    }

    @Override
    protected @NotNull String rename(@NotNull String name) {
        if ("toms_mobs:nautilus".equals(name)) {
            return "minecraft:nautilus";
        }
        return name;
    }
}