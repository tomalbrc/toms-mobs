package de.tomalbrc.toms_mobs.entity.goal.aquatic;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class AquaticRangedAttackGoal extends RangedAttackGoal {
    private final LivingEntity mob;

    public AquaticRangedAttackGoal(LivingEntity rangedAttackMob, double d, int i, float f) {
        super((RangedAttackMob) rangedAttackMob, d, i, f);
        this.mob = rangedAttackMob;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.mob.isInWater();
    }
}
