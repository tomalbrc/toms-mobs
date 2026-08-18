package aqario.fowlplay.common.entity.ai.brain.behaviour;

import aqario.fowlplay.common.entity.bird.BirdEntity;
import aqario.fowlplay.core.FowlPlayMemoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryCondition;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.base.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtil;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class TeleportToTarget extends ExtendedBehaviour<BirdEntity> {
    private static final Set<MemoryCondition<?,?>> MEMORIES = Set.of(new MemoryCondition.Present<>(FowlPlayMemoryTypes.TELEPORT_TARGET.get()));

    @Override
    public @NonNull Set<MemoryCondition<?, ?>> getMemoryRequirements() {
        return MEMORIES;
    }

    @Override
    protected boolean shouldKeepRunning(@NonNull BirdEntity entity) {
        return BrainUtil.hasMemory(entity, FowlPlayMemoryTypes.TELEPORT_TARGET.get());
    }

    @Override
    protected void tick(BirdEntity entity) {
        Brain<?> brain = entity.getBrain();
        if (this.tryTeleport(entity, brain)) {
            BrainUtil.clearMemory(brain, FowlPlayMemoryTypes.TELEPORT_TARGET.get());
        }
    }

    private boolean tryTeleport(BirdEntity entity, Brain<?> brain) {
        if (!BrainUtil.hasMemory(brain, FowlPlayMemoryTypes.TELEPORT_TARGET.get())) {
            return false;
        }
        // noinspection ConstantConditions
        Entity target = BrainUtil.getMemory(brain, FowlPlayMemoryTypes.TELEPORT_TARGET.get()).entity();
        BlockPos pos = target.blockPosition();

        for (int i = 0; i < 10; i++) {
            int j = entity.getRandom().nextIntBetweenInclusive(-3, 3);
            int k = entity.getRandom().nextIntBetweenInclusive(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = entity.getRandom().nextIntBetweenInclusive(-1, 1);
                if (this.tryTeleportTo(entity, pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean tryTeleportTo(BirdEntity entity, int x, int y, int z) {
        if (!this.canTeleportTo(entity, new BlockPos(x, y, z))) {
            return false;
        }

        entity.moveOrInterpolateTo(new Vec3(x + 0.5, y, z + 0.5), entity.getYRot(), entity.getXRot());
        entity.getNavigation().stop();
        return true;
    }

    private boolean canTeleportTo(BirdEntity entity, BlockPos pos) {
        PathType pathNodeType = WalkNodeEvaluator.getPathTypeStatic(entity, pos.mutable());
        if (pathNodeType != PathType.WALKABLE) {
            return false;
        }
        BlockPos distance = pos.subtract(entity.blockPosition());
        return entity.level().noCollision(entity, entity.getBoundingBox().move(distance));
    }
}