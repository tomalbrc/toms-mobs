package de.tomalbrc.toms_mobs.entity.navigation;

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
        this.maxDistanceToWaypoint = this.mob.getBbWidth();
        Vec3i nextNodePos = this.path.getNextNodePos();
        double d = Math.abs(this.mob.getX() - ((double) nextNodePos.getX() + 0.5));
        double e = Math.abs(this.mob.getY() - (double) nextNodePos.getY());
        double f = Math.abs(this.mob.getZ() - ((double) nextNodePos.getZ() + 0.5));
        boolean bl = d < (double) this.maxDistanceToWaypoint && f < (double) this.maxDistanceToWaypoint && e < 1.0;
        if (bl || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vec3)) {
            this.path.advance();
        }

        this.doStuckDetection(vec3);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 dir) {
        if (path == null)
            return false;

        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        } else {
            Vec3 nextNodePos = Vec3.atBottomCenterOf(this.path.getNextNodePos());
            if (!dir.closerThan(nextNodePos, 2.0)) {
                return false;
            } else if (this.canMoveDirectly(dir, this.path.getNextEntityPos(this.mob))) {
                return true;
            } else {
                Vec3 nextNextNodePos = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                Vec3 diffA = nextNodePos.subtract(dir);
                Vec3 diffB = nextNextNodePos.subtract(dir);
                double distA = diffA.lengthSqr();
                double distB = diffB.lengthSqr();
                boolean followingNodeDistanceIsCloser = distB < distA;
                boolean reachedNext = distA < .5;
                if (!followingNodeDistanceIsCloser && !reachedNext) {
                    return false;
                } else {
                    Vec3 aDir = diffA.normalize();
                    Vec3 bDir = diffB.normalize();
                    return bDir.dot(aDir) < 0.0;
                }
            }
        }
    }
}
