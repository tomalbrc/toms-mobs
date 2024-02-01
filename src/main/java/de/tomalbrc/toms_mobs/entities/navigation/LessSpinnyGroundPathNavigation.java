package de.tomalbrc.toms_mobs.entities.navigation;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LessSpinnyGroundPathNavigation extends GroundPathNavigation {
    public LessSpinnyGroundPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected void followThePath() {
        Vec3 vec3 = this.getTempMobPos();
        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 1.3 ? this.mob.getBbWidth() : this.mob.getBbWidth()/4.f; // basically copy&paste but fixed maxDistanceToWaypoint
        Vec3i vec3i = this.path.getNextNodePos();
        double d = Math.abs(this.mob.getX() - ((double) vec3i.getX() + 0.5));
        double e = Math.abs(this.mob.getY() - (double) vec3i.getY());
        double f = Math.abs(this.mob.getZ() - ((double) vec3i.getZ() + 0.5));
        boolean bl = d < (double) this.maxDistanceToWaypoint && f < (double) this.maxDistanceToWaypoint && e < 1.0;
        if (bl || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vec3)) {
            this.path.advance();
        }

        this.doStuckDetection(vec3);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 vec3) {
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        } else {
            Vec3 vec32 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
            if (!vec3.closerThan(vec32, 2.0)) {
                return false;
            } else if (this.canMoveDirectly(vec3, this.path.getNextEntityPos(this.mob))) {
                return true;
            } else {
                Vec3 vec33 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                Vec3 vec34 = vec32.subtract(vec3);
                Vec3 vec35 = vec33.subtract(vec3);
                double d = vec34.lengthSqr();
                double e = vec35.lengthSqr();
                boolean bl = e < d;
                boolean bl2 = d < 0.5;
                if (!bl && !bl2) {
                    return false;
                } else {
                    Vec3 vec36 = vec34.normalize();
                    Vec3 vec37 = vec35.normalize();
                    return vec37.dot(vec36) < 0.0;
                }
            }
        }
    }
}
