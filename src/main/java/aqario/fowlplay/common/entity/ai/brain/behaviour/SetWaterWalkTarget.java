package aqario.fowlplay.common.entity.ai.brain.behaviour;

import aqario.fowlplay.common.entity.ai.navigation.BirdRandomPos;
import aqario.fowlplay.common.entity.bird.BirdEntity;
import aqario.fowlplay.common.util.CylindricalRadius;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryCondition;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.BrainUtil;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.function.BiPredicate;

public class SetWaterWalkTarget<E extends BirdEntity> extends SpeedModifiableBehaviour<E> {
    private static final Set<MemoryCondition<?,?>> MEMORIES = Set.of(
            new MemoryCondition.Absent<>(MemoryModuleType.WALK_TARGET)
    );

    protected CylindricalRadius radius = new CylindricalRadius(32, 16);
    protected BiPredicate<E, Vec3> positionPredicate = (entity, pos) -> true;

    public SetWaterWalkTarget<E> radius(int radius) {
        return this.radius(radius, radius);
    }

    public SetWaterWalkTarget<E> radius(int xz, int y) {
        this.radius = new CylindricalRadius(xz, y);

        return this;
    }

    public SetWaterWalkTarget<E> walkTargetPredicate(BiPredicate<E, Vec3> predicate) {
        this.positionPredicate = predicate;

        return this;
    }

    @Override
    public @NonNull Set<MemoryCondition<?, ?>> getMemoryRequirements() {
        return MEMORIES;
    }

    @Override
    protected void start(@NonNull E entity) {
        Vec3 targetPos = this.getTargetPos(entity);

        if (!this.positionPredicate.test(entity, targetPos)) {
            targetPos = null;
        }

        if (targetPos != null) {
            BrainUtil.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, this.speedModifier.apply(entity, targetPos), 1));
        } else {
            BrainUtil.clearMemory(entity, MemoryModuleType.WALK_TARGET);
        }
    }

    @Nullable
    protected Vec3 getTargetPos(E entity) {
        return BirdRandomPos.getWater(entity, this.radius);
    }
}