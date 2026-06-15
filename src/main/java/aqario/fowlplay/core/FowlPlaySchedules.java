package aqario.fowlplay.core;

import net.minecraft.world.entity.schedule.Activity;
import net.tslat.smartbrainlib.api.core.schedule.SmartBrainSchedule;

import java.util.function.Supplier;

public class FowlPlaySchedules {
    private static SmartBrainSchedule forager;
    private static SmartBrainSchedule raptor;
    private static SmartBrainSchedule seabird;
    private static SmartBrainSchedule waterfowl;

    // These fields are completely safe because the lambda doesn't touch the builders until invoked!
    public static final Supplier<SmartBrainSchedule> FORAGER = () -> getForager();
    public static final Supplier<SmartBrainSchedule> RAPTOR = () -> getRaptor();
    public static final Supplier<SmartBrainSchedule> SEABIRD = () -> getSeabird();
    public static final Supplier<SmartBrainSchedule> WATERFOWL = () -> getWaterfowl();

    private static SmartBrainSchedule getForager() {
        if (forager == null) {
            forager = SmartBrainSchedule.byDaytime()
                    .activityAt(0L, FowlPlayActivities.PERCH.get())
                    .activityAt(1000L, FowlPlayActivities.FORAGE.get())
                    .activityAt(6000L, FowlPlayActivities.PERCH.get())
                    .activityAt(8000L, FowlPlayActivities.FORAGE.get())
                    .activityAt(11000L, FowlPlayActivities.PERCH.get())
                    .activityAt(12500L, Activity.REST)
                    .activityAt(23000L, FowlPlayActivities.PERCH.get());
        }
        return forager;
    }

    private static SmartBrainSchedule getRaptor() {
        if (raptor == null) {
            raptor = SmartBrainSchedule.byDaytime()
                    .activityAt(0L, FowlPlayActivities.PERCH.get())
                    .activityAt(1000L, FowlPlayActivities.SOAR.get())
                    .activityAt(6000L, FowlPlayActivities.PERCH.get())
                    .activityAt(8000L, FowlPlayActivities.SOAR.get())
                    .activityAt(11000L, FowlPlayActivities.PERCH.get())
                    .activityAt(12500L, Activity.REST)
                    .activityAt(23000L, FowlPlayActivities.PERCH.get());
        }
        return raptor;
    }

    private static SmartBrainSchedule getSeabird() {
        if (seabird == null) {
            seabird = SmartBrainSchedule.byDaytime()
                    .activityAt(0L, Activity.IDLE)
                    .activityAt(1000L, FowlPlayActivities.SOAR.get())
                    .activityAt(6000L, FowlPlayActivities.FORAGE.get())
                    .activityAt(8000L, FowlPlayActivities.SOAR.get())
                    .activityAt(11000L, Activity.IDLE)
                    .activityAt(12500L, Activity.REST)
                    .activityAt(23000L, Activity.IDLE);
        }
        return seabird;
    }

    private static SmartBrainSchedule getWaterfowl() {
        if (waterfowl == null) {
            waterfowl = SmartBrainSchedule.byDaytime()
                    .activityAt(0L, Activity.IDLE)
                    .activityAt(1000L, FowlPlayActivities.FORAGE.get())
                    .activityAt(6000L, Activity.IDLE)
                    .activityAt(8000L, FowlPlayActivities.FORAGE.get())
                    .activityAt(11000L, Activity.IDLE)
                    .activityAt(12500L, Activity.REST)
                    .activityAt(23000L, Activity.IDLE);
        }
        return waterfowl;
    }
}