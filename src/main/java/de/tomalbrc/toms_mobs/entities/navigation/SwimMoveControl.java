package de.tomalbrc.toms_mobs.entities.navigation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.animal.Animal;

public class SwimMoveControl extends MoveControl {
    private final PathfinderMob animal;

    public SwimMoveControl(PathfinderMob animal) {
        super(animal);
        this.animal = animal;
    }

    private void updateSpeed() {
        if (this.animal.isInWater()) {
            this.animal.setDeltaMovement(this.animal.getDeltaMovement().add(0.0, 0.005, 0.0));

            if (this.animal.isBaby()) {
                this.animal.setSpeed(Math.max(this.animal.getSpeed() / 3.0F, 0.06F));
            }
        } else if (this.animal.onGround()) {
            this.animal.setSpeed(Math.max(this.animal.getSpeed() / 2.0F, 0.06F));
        }

    }

    public void tick() {
        this.updateSpeed();
        if (this.operation == Operation.MOVE_TO && !this.animal.getNavigation().isDone()) {
            double d = this.wantedX - this.animal.getX();
            double e = this.wantedY - this.animal.getY();
            double f = this.wantedZ - this.animal.getZ();
            double g = Math.sqrt(d * d + e * e + f * f);
            if (g < 9.999999747378752E-6) {
                this.mob.setSpeed(0.0F);
            } else {
                e /= g;
                float h = (float)(Mth.atan2(f, d) * 57.2957763671875) - 90.0F;
                this.animal.setYRot(this.rotlerp(this.animal.getYRot(), h, 90.0F));
                this.animal.yBodyRot = this.animal.getYRot();
                float i = (float)(this.speedModifier * this.animal.getAttributeValue(Attributes.MOVEMENT_SPEED));
                this.animal.setSpeed(Mth.lerp(0.125F, this.animal.getSpeed(), i));
                this.animal.setDeltaMovement(this.animal.getDeltaMovement().add(0.0, (double)this.animal.getSpeed() * e * 0.1, 0.0));
            }
        } else {
            this.animal.setSpeed(0.0F);
        }
    }
}
