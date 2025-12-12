package de.tomalbrc.toms_mobs.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {
    public static final SoundEvent PENGUIN_AMBIENT = register("penguin.ambient");
    public static final SoundEvent PENGUIN_HURT = register("penguin.hurt");
    public static final SoundEvent PENGUIN_DEATH = register("penguin.death");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath("toms_mobs", name);
        return SoundEvent.createVariableRangeEvent(id);
    }

    public static void registerSounds() {
    }
}

