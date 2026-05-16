package aqario.fowlplay.common.entity.ai.brain;

import aqario.fowlplay.common.entity.bird.BirdEntity;
import aqario.fowlplay.core.FowlPlayActivities;
import aqario.fowlplay.core.FowlPlayMemoryTypes;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.ActivityBuilder;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public interface BirdBrain<T extends BirdEntity & BirdBrain<T>> extends SmartBrainOwner<T> {
    @SafeVarargs
    static <T extends BirdEntity & BirdBrain<T>> ActivityBuilder<@NonNull T> coreActivity(Behavior<? super T>... behaviours) {
        return ActivityBuilder.<T>create(Activity.CORE).behaviourPriorityBase(0).behaviours(behaviours);
    }

    @SafeVarargs
    static <T extends BirdEntity & BirdBrain<T>> ActivityBuilder<@NonNull T> avoidActivity(Behavior<? super T>... behaviours) {
        return ActivityBuilder.<T>create(Activity.AVOID).behaviourPriorityBase(10).behaviours(behaviours)
                .requireAndClearMemoriesOnUse(FowlPlayMemoryTypes.IS_AVOIDING.get());
    }

    @SafeVarargs
    static <T extends BirdEntity & BirdBrain<T>> ActivityBuilder<@NonNull T> deliverActivity(Behavior<? super T>... behaviours) {
        return ActivityBuilder.<T>create(FowlPlayActivities.DELIVER.get()).behaviourPriorityBase(10).behaviours(behaviours)
                .requireAndClearMemoriesOnUse(FowlPlayMemoryTypes.RECIPIENT.get());
    }

    @SafeVarargs
    static <T extends BirdEntity & BirdBrain<T>> ActivityBuilder<@NonNull T> fightActivity(Behavior<? super T>... behaviours) {
        return ActivityBuilder.<T>create(Activity.FIGHT).behaviourPriorityBase(10).behaviours(behaviours)
                .requireAndClearMemoriesOnUse(MemoryModuleType.ATTACK_TARGET);
    }

    @SafeVarargs
    static <T extends BirdEntity & BirdBrain<T>> ActivityBuilder<T> forageActivity(Behavior<? super T>... behaviours) {
        return ActivityBuilder.<T>create(FowlPlayActivities.FORAGE.get()).behaviourPriorityBase(10).behaviours(behaviours);
    }

    @SafeVarargs
    static <T extends BirdEntity & BirdBrain<T>> ActivityBuilder<@NonNull T> idleActivity(Behavior<? super T>... behaviours) {
        return ActivityBuilder.<T>create(Activity.IDLE).behaviourPriorityBase(10).behaviours(behaviours);
    }

    @SafeVarargs
    static <T extends BirdEntity & BirdBrain<T>> ActivityBuilder<T> perchActivity(Behavior<? super T>... behaviours) {
        return ActivityBuilder.<T>create(FowlPlayActivities.PERCH.get()).behaviourPriorityBase(10).behaviours(behaviours);
    }

    @SafeVarargs
    static <T extends BirdEntity & BirdBrain<T>> ActivityBuilder<T> pickupFoodActivity(Behavior<? super T>... behaviours) {
        return ActivityBuilder.<T>create(FowlPlayActivities.PICK_UP.get()).behaviourPriorityBase(10).behaviours(behaviours)
                .requireAndClearMemoriesOnUse(FowlPlayMemoryTypes.SEES_FOOD.get());
    }

    @SafeVarargs
    static <T extends BirdEntity & BirdBrain<T>> ActivityBuilder<T> restActivity(Behavior<? super T>... behaviours) {
        return ActivityBuilder.<T>create(Activity.REST).behaviourPriorityBase(10).behaviours(behaviours);
    }

    @SafeVarargs
    static <T extends BirdEntity & BirdBrain<T>> ActivityBuilder<T> soarActivity(Behavior<? super T>... behaviours) {
        return ActivityBuilder.<T>create(FowlPlayActivities.SOAR.get()).behaviourPriorityBase(10).behaviours(behaviours);
    }

    default ActivityBuilder<@NonNull T> getAvoidTasks() {
        return ActivityBuilder.create(Activity.AVOID);
    }

    default ActivityBuilder<@NonNull T> getDeliverTasks() {
        return ActivityBuilder.create(FowlPlayActivities.DELIVER.get());
    }

    default ActivityBuilder<@NonNull T> getForageTasks() {
        return ActivityBuilder.create(FowlPlayActivities.FORAGE.get());
    }

    default ActivityBuilder<@NonNull T> getPerchTasks() {
        return ActivityBuilder.create(FowlPlayActivities.PERCH.get());
    }

    default ActivityBuilder<@NonNull T> getPickupFoodTasks() {
        return ActivityBuilder.create(FowlPlayActivities.PICK_UP.get());
    }

    default ActivityBuilder<@NonNull T> getRestTasks() {
        return ActivityBuilder.create(Activity.REST);
    }

    default ActivityBuilder<@NonNull T> getSoarTasks() {
        return ActivityBuilder.create(FowlPlayActivities.SOAR.get());
    }

    @Override
    default ActivityBuilder<? extends @NonNull T> getActivityGroupFor(Activity activity) {

        if (activity == FowlPlayActivities.DELIVER.get())
            return this.getDeliverTasks();
        if (activity == Activity.AVOID)
            return this.getAvoidTasks();
        if (activity == FowlPlayActivities.PICK_UP.get())
            return this.getPickupFoodTasks();
        if (activity == FowlPlayActivities.FORAGE.get())
            return this.getForageTasks();
        if (activity == FowlPlayActivities.SOAR.get())
            return this.getSoarTasks();
        if (activity == FowlPlayActivities.PERCH.get())
            return this.getPerchTasks();
        if (activity == Activity.REST)
            return this.getRestTasks();

        return ActivityBuilder.create(activity);
    }


    @Override
    default Activity @NonNull [] getActivityActivationPriority() {
        return new Activity[]{
                FowlPlayActivities.DELIVER.get(),
                Activity.AVOID,
                Activity.FIGHT,
                FowlPlayActivities.PICK_UP.get(),
                FowlPlayActivities.FORAGE.get(),
                FowlPlayActivities.SOAR.get(),
                FowlPlayActivities.PERCH.get(),
                Activity.IDLE,
                Activity.REST
        };
    }

    @Override
    default @NonNull Set<Activity> getScheduleIgnoringActivities() {
        return ObjectArraySet.of(
                FowlPlayActivities.DELIVER.get(),
                Activity.AVOID,
                Activity.FIGHT,
                FowlPlayActivities.PICK_UP.get()
        );
    }

    @Override
    default @NonNull Activity getDefaultActivity(@NonNull T owner) {
        return Activity.REST;
    }
}