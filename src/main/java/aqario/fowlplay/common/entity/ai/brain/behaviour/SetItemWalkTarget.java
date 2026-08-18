package aqario.fowlplay.common.entity.ai.brain.behaviour;

import aqario.fowlplay.common.entity.bird.BirdEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryCondition;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtil;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class SetItemWalkTarget<E extends BirdEntity> extends SpeedModifiableBehaviour<E> {
    private static final Set<MemoryCondition<?,?>> MEMORY_REQUIREMENTS = Set.of(
            new MemoryCondition.Registered<>(MemoryModuleType.WALK_TARGET),
            new MemoryCondition.Registered<>(MemoryModuleType.LOOK_TARGET),
            new MemoryCondition.Registered<>(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS),
            new MemoryCondition.Present<>(SBLMemoryTypes.NEARBY_ITEMS.get())
    );

    protected Function<E, Integer> radius = entity -> 32;

    public SetItemWalkTarget<E> radius(int radius) {
        return this.radius(entity -> radius);
    }

    public SetItemWalkTarget<E> radius(Function<E, Integer> function) {
        this.radius = function;

        return this;
    }

    @Override
    public @NonNull Set<MemoryCondition<?, ?>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected void start(E entity) {
        Brain<?> brain = entity.getBrain();
        List<ItemEntity> wantedItems = BrainUtil.getMemory(brain, SBLMemoryTypes.NEARBY_ITEMS.get());
        // noinspection ConstantConditions
        ItemEntity targetItem = wantedItems.getFirst();
        if (targetItem.closerThan(entity, this.radius.apply(entity))
                && entity.level().getWorldBorder().isWithinBounds(targetItem.blockPosition())
        ) {
            WalkTarget newWalkTarget = new WalkTarget(new EntityTracker(targetItem, false), this.speedModifier.apply(entity, targetItem.position()), 0);
            if (!BrainUtil.hasMemory(brain, MemoryModuleType.AVOID_TARGET)) {
                BrainUtil.setMemory(brain, MemoryModuleType.LOOK_TARGET, new EntityTracker(targetItem, true));
            }
            BrainUtil.setMemory(brain, MemoryModuleType.WALK_TARGET, newWalkTarget);
        }
    }
}