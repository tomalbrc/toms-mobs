package de.tomalbrc.toms_mobs.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {
    public static final SoundEvent PENGUIN_AMBIENT = register("penguin.ambient");
    public static final SoundEvent PENGUIN_HURT = register("penguin.hurt");
    public static final SoundEvent PENGUIN_DEATH = register("penguin.death");

    private static SoundEvent register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("toms_mobs", name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerSounds() {
    }
}

