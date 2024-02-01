package de.tomalbrc.toms_mobs.entities.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FlyingWanderGoal extends Goal {
    PathfinderMob mob;

    public FlyingWanderGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        return this.mob.getNavigation().isDone();
    }

    public boolean canContinueToUse() {
        return this.mob.getNavigation().isInProgress();
    }

    public void start() {
        Vec3 vec3 = this.findPos();
        if (vec3 != null) {
            this.mob.getNavigation().moveTo(this.mob.getNavigation().createPath(BlockPos.containing(vec3), 1), 1.1);
        }
    }

    @Nullable
    private Vec3 findPos() {
        Vec3 viewVector = this.mob.getViewVector(0.0F);
        //Vec3 pos = HoverRandomPos.getPos(this.mob, 8, 7, viewVector.x, viewVector.z, Mth.HALF_PI, 3, 1);
        return AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, viewVector.x, viewVector.z, Mth.HALF_PI);
    }
}