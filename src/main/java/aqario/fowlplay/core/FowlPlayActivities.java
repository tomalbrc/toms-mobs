package aqario.fowlplay.core;

import de.tomalbrc.toms_mobs.TomsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FowlPlayActivities {
    public static final DeferredRegister<Activity> ACTIVITIES =
            DeferredRegister.create(Registries.ACTIVITY, TomsMobs.MODID);

    public static final DeferredHolder<Activity, Activity> DELIVER = register("deliver");
    public static final DeferredHolder<Activity, Activity> FORAGE = register("forage");
    public static final DeferredHolder<Activity, Activity> PERCH = register("perch");
    public static final DeferredHolder<Activity, Activity> PICK_UP = register("pick_up");
    public static final DeferredHolder<Activity, Activity> SOAR = register("soar");

    private static DeferredHolder<Activity, Activity> register(String id) {
        return ACTIVITIES.register(id, () -> new Activity(id));
    }
}