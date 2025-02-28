package de.tomalbrc.toms_mobs.entity.goal.aquatic;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

public class AquaticRandomStrollGoal extends RandomStrollGoal {
    private final PathfinderMob mob;
    public AquaticRandomStrollGoal(PathfinderMob pathfinderMob, double d) {
        super(pathfinderMob, d);
        this.mob = pathfinderMob;
    }

    public boolean canUse() {
        return super.canUse() && !this.mob.isInWater();
    }
}
