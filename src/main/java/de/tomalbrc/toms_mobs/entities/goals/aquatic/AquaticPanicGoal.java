package de.tomalbrc.toms_mobs.entities.goals.aquatic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

public class AquaticPanicGoal extends PanicGoal {

    public AquaticPanicGoal(PathfinderMob animal, double d) {
        super(animal, d);
    }

    public boolean canUse() {
        if (!this.shouldPanic()) {
            return false;
        } else {
            BlockPos blockPos = this.lookForWater(this.mob.level(), this.mob, 7);
            if (blockPos != null) {
                this.posX = blockPos.getX();
                this.posY = blockPos.getY();
                this.posZ = blockPos.getZ();
                return true;
            } else {
                return this.findRandomPosition();
            }
        }
    }
}
