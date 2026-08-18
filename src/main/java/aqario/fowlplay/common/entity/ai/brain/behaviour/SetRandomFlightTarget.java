package aqario.fowlplay.common.entity.ai.brain.behaviour;

import aqario.fowlplay.common.entity.ai.navigation.BirdRandomPos;
import aqario.fowlplay.common.entity.bird.FlyingBirdEntity;
import aqario.fowlplay.common.util.CylindricalRadius;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryCondition;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.base.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtil;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class SetRandomFlightTarget<E extends FlyingBirdEntity> extends ExtendedBehaviour<E> {
    private static final Set<MemoryCondition<?,?>> MEMORIES = Set.of(
            new MemoryCondition.Absent<>(MemoryModuleType.WALK_TARGET)
    );

    private static final CylindricalRadius RANGE = new CylindricalRadius(64, 16);

    @Override
    public @NonNull Set<MemoryCondition<?, ?>> getMemoryRequirements() {
        return MEMORIES;
    }

    @Override
    protected void start(E entity) {
        Brain<?> brain = entity.getBrain();
        Vec3 target = BirdRandomPos.getAir(entity, RANGE);
        if (target != null) {
            BrainUtil.setMemory(brain, MemoryModuleType.WALK_TARGET, new WalkTarget(target, 1.0f, 0));
        } else {
            BrainUtil.clearMemory(brain, MemoryModuleType.WALK_TARGET);
        }
    }
}