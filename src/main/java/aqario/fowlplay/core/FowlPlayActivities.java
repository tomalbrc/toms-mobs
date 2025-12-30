package aqario.fowlplay.core;

import de.tomalbrc.toms_mobs.util.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.schedule.Activity;

import java.util.function.Supplier;

public final class FowlPlayActivities {
    public static final Supplier<Activity> DELIVER = register("deliver");
    public static final Supplier<Activity> FORAGE = register("forage");
    public static final Supplier<Activity> PERCH = register("perch");
    public static final Supplier<Activity> PICK_UP = register("pick_up");
    public static final Supplier<Activity> SOAR = register("soar");

    private static Supplier<Activity> register(String id) {
        return registerActivity(id, () -> new Activity(id));
    }

    public static Supplier<Activity> registerActivity(String id, Supplier<Activity> activity) {
        Activity registry = Registry.register(BuiltInRegistries.ACTIVITY, Util.id(id), activity.get());
        return () -> registry;
    }

    public static void init() {
    }
}