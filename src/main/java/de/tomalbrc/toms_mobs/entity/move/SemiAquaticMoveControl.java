package de.tomalbrc.toms_mobs.entity.move;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class SemiAquaticMoveControl extends MoveControl {
    private final PathfinderMob animal;

    public SemiAquaticMoveControl(PathfinderMob animal) {
        super(animal);
        this.animal = animal;
    }

    private void updateSpeed() {
        if (this.animal.isInWater()) {
            this.animal.setDeltaMovement(this.animal.getDeltaMovement().add(0.0, 0.005, 0.0));
        }

        this.animal.setSpeed((float)(this.speedModifier*this.animal.getAttributeValue(Attributes.MOVEMENT_SPEED)*(this.animal.isBaby()?0.5:1.0)));
    }

    public void tick() {
        this.updateSpeed();
        if (this.animal.isInWater()) {
            if (this.operation == Operation.MOVE_TO && !this.animal.getNavigation().isDone()) {
                double xDiff = this.wantedX - this.animal.getX();
                double yDiff = this.wantedY - this.animal.getY();
                double zDiff = this.wantedZ - this.animal.getZ();
                double lenSquared = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
                if (lenSquared < 0.00001) {
                    this.mob.setSpeed(0.0F);
                } else {
                    yDiff /= lenSquared;
                    float h = (float)(Mth.atan2(zDiff, xDiff) * 57.2957763671875) - 90.0F;
                    this.animal.setYRot(this.rotlerp(this.animal.getYRot(), h, 90.0F));
                    this.animal.yBodyRot = this.animal.getYRot();
                    float i = (float)(this.speedModifier * this.animal.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    this.animal.setSpeed(Mth.lerp(0.125F, this.animal.getSpeed(), i));
                    this.animal.setDeltaMovement(this.animal.getDeltaMovement().add(0.0, (double)this.animal.getSpeed() * yDiff * 0.1, 0.0));
                }
            } else {
                this.animal.setSpeed(0.0F);
            }
        } else {
            super.tick();
        }
    }
}
