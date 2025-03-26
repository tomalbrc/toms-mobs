package de.tomalbrc.toms_mobs.entity.move;

import de.tomalbrc.toms_mobs.entity.goal.flying.FlyingMobCircleAroundAnchorGoal;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class FlyingMobCircleMoveControl extends MoveControl {
    private float speed = 0.1F;

    private final FlyingMobCircleAroundAnchorGoal.FlyCirclingMob flyingMob;

    public FlyingMobCircleMoveControl(final FlyingMobCircleAroundAnchorGoal.FlyCirclingMob mob) {
        super((Mob) mob);
        this.flyingMob = mob;
    }

    public void tick() {
        if (!this.flyingMob.canFlyCurrently()) {
            super.tick();
            return;
        }

        if (this.mob.horizontalCollision) {
            this.mob.setYRot(this.mob.getYRot() + 180.0F);
            this.speed = 0.1F;
        }

        double dx = this.flyingMob.getMoveTargetPoint().x - this.flyingMob.getX();
        double dy = this.flyingMob.getMoveTargetPoint().y - this.flyingMob.getY();
        double dz = this.flyingMob.getMoveTargetPoint().z - this.flyingMob.getZ();
        double horizontalDeltaDist = Math.sqrt(dx * dx + dz * dz);
        if (Math.abs(horizontalDeltaDist) > 0.00001) {
            double h = 1.0 - Math.abs(dy * 0.9) / horizontalDeltaDist;
            dx *= h;
            dz *= h;
            horizontalDeltaDist = Math.sqrt(dx * dx + dz * dz);
            double deltaLen = Math.sqrt(dx * dx + dz * dz + dy * dy);
            float yRotOld = this.mob.getYRot();
            float dirAngRad = (float) Mth.atan2(dz, dx);
            float l = Mth.wrapDegrees(this.mob.getYRot() + 90.0F);
            float m = Mth.wrapDegrees(dirAngRad * 57.295776F);
            this.mob.setYRot(Mth.approachDegrees(l, m, 4.0F) - 90.0F);
            this.mob.yBodyRot = this.mob.getYRot();
            if (Mth.degreesDifferenceAbs(yRotOld, this.mob.getYRot()) < 3.0F) {
                this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
            } else {
                this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
            }

            float n = (float)(-(Mth.atan2(-dy, horizontalDeltaDist) * 57.295776F));
            this.mob.setXRot(n);
            float yaw = this.mob.getYRot() + 90.0F;
            double moveX = (double)(this.speed * Mth.cos(yaw * 0.017453292F)) * Math.abs(dx / deltaLen);
            double moveZ = (double)(this.speed * Mth.sin(yaw * 0.017453292F)) * Math.abs(dz / deltaLen);
            double moveY = (double)(this.speed * Mth.sin(n * 0.017453292F)) * Math.abs(dy / deltaLen);
            Vec3 vec3 = this.mob.getDeltaMovement();
            this.mob.setDeltaMovement(vec3.add((new Vec3(moveX, moveY, moveZ)).subtract(vec3).scale(0.2)));
        }
    }
}