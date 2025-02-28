package de.tomalbrc.toms_mobs.entity.goal.aquatic;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

public class AquaticRandomLookAroundGoal extends RandomLookAroundGoal {
    private final Mob mob;
    public AquaticRandomLookAroundGoal(Mob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return !this.mob.isInWater() && super.canUse();
    }
}
