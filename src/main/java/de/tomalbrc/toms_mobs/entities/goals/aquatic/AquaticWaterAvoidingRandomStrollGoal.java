package de.tomalbrc.toms_mobs.entities.goals.aquatic;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class AquaticWaterAvoidingRandomStrollGoal extends WaterAvoidingRandomStrollGoal {
    private final PathfinderMob mob;
    public AquaticWaterAvoidingRandomStrollGoal(PathfinderMob pathfinderMob, double d) {
        super(pathfinderMob, d);
        this.mob = pathfinderMob;
    }

    public boolean canUse() {
        return super.canUse() && !this.mob.isInWater();
    }
}
