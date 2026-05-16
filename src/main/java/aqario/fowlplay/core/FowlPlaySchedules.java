package aqario.fowlplay.core;

import net.minecraft.world.entity.schedule.Activity;
import net.tslat.smartbrainlib.api.core.schedule.SmartBrainSchedule;

import java.util.function.Supplier;

public class FowlPlaySchedules {
    // TODO: have separate schedules for babies and adults, and separate schedules for domestic and wild variants
    public static final Supplier<SmartBrainSchedule> FORAGER = register("forager", SmartBrainSchedule.byDaytime()
            .activityAt(0L, FowlPlayActivities.PERCH.get())
            .activityAt(1000L, FowlPlayActivities.FORAGE.get())
            .activityAt(6000L, FowlPlayActivities.PERCH.get())
            .activityAt(8000L, FowlPlayActivities.FORAGE.get())
            .activityAt(11000L, FowlPlayActivities.PERCH.get())
            .activityAt(12500L, Activity.REST)
            .activityAt(23000L, FowlPlayActivities.PERCH.get())
    );
    public static final Supplier<SmartBrainSchedule> RAPTOR = register("raptor", SmartBrainSchedule.byDaytime()
            .activityAt(0L, FowlPlayActivities.PERCH.get())
            .activityAt(1000L, FowlPlayActivities.SOAR.get())
            .activityAt(6000L, FowlPlayActivities.PERCH.get())
            .activityAt(8000L, FowlPlayActivities.SOAR.get())
            .activityAt(11000L, FowlPlayActivities.PERCH.get())
            .activityAt(12500L, Activity.REST)
            .activityAt(23000L, FowlPlayActivities.PERCH.get())
    );
    public static final Supplier<SmartBrainSchedule> SEABIRD = register("seabird", SmartBrainSchedule.byDaytime()
            .activityAt(0L, Activity.IDLE)
            .activityAt(1000L, FowlPlayActivities.SOAR.get())
            .activityAt(6000L, FowlPlayActivities.FORAGE.get())
            .activityAt(8000L, FowlPlayActivities.SOAR.get())
            .activityAt(11000L, Activity.IDLE)
            .activityAt(12500L, Activity.REST)
            .activityAt(23000L, Activity.IDLE)
    );

    public static final Supplier<SmartBrainSchedule> WATERFOWL = register("waterfowl", SmartBrainSchedule.byDaytime()
            .activityAt(0L, Activity.IDLE)
            .activityAt(1000L, FowlPlayActivities.FORAGE.get())
            .activityAt(6000L, Activity.IDLE)
            .activityAt(8000L, FowlPlayActivities.FORAGE.get())
            .activityAt(11000L, Activity.IDLE)
            .activityAt(12500L, Activity.REST)
            .activityAt(23000L, Activity.IDLE)
    );

    private static Supplier<SmartBrainSchedule> register(String id, SmartBrainSchedule schedule) {
        return () -> schedule;
    }

    public static void init() {
    }
}