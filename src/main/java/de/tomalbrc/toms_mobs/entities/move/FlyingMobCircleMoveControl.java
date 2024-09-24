package de.tomalbrc.toms_mobs.entities.move;

import de.tomalbrc.toms_mobs.entities.goals.FlyingMobCircleAroundAnchorGoal;
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
        if (this.mob.horizontalCollision) {
            this.mob.setYRot(this.mob.getYRot() + 180.0F);
            this.speed = 0.1F;
        }

        double d = this.flyingMob.getMoveTargetPoint().x - this.flyingMob.getX();
        double e = this.flyingMob.getMoveTargetPoint().y - this.flyingMob.getY();
        double f = this.flyingMob.getMoveTargetPoint().z - this.flyingMob.getZ();
        double g = Math.sqrt(d * d + f * f);
        if (Math.abs(g) > 9.999999747378752E-6) {
            double h = 1.0 - Math.abs(e * 0.699999988079071) / g;
            d *= h;
            f *= h;
            g = Math.sqrt(d * d + f * f);
            double i = Math.sqrt(d * d + f * f + e * e);
            float j = this.mob.getYRot();
            float k = (float) Mth.atan2(f, d);
            float l = Mth.wrapDegrees(this.mob.getYRot() + 90.0F);
            float m = Mth.wrapDegrees(k * 57.295776F);
            this.mob.setYRot(Mth.approachDegrees(l, m, 4.0F) - 90.0F);
            this.mob.yBodyRot = this.mob.getYRot();
            if (Mth.degreesDifferenceAbs(j, this.mob.getYRot()) < 3.0F) {
                this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
            } else {
                this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
            }

            float n = (float)(-(Mth.atan2(-e, g) * 57.2957763671875));
            this.mob.setXRot(n);
            float o = this.mob.getYRot() + 90.0F;
            double p = (double)(this.speed * Mth.cos(o * 0.017453292F)) * Math.abs(d / i);
            double q = (double)(this.speed * Mth.sin(o * 0.017453292F)) * Math.abs(f / i);
            double r = (double)(this.speed * Mth.sin(n * 0.017453292F)) * Math.abs(e / i);
            Vec3 vec3 = this.mob.getDeltaMovement();
            this.mob.setDeltaMovement(vec3.add((new Vec3(p, r, q)).subtract(vec3).scale(0.2)));
        }
    }
}