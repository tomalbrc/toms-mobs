package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.toms_mobs.mixins.LivingEntityAccessor;
import de.tomalbrc.toms_mobs.mixins.MeleeAttackGoalAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class Accessors {
    public static int ticksUntilNextAttack(MeleeAttackGoal goal) {
        return ((MeleeAttackGoalAccessor) goal).tomsmobs_getTicksUntilNextAttack();
    }

    public static boolean isJumping(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).tomsmobs_isJumping();
    }
}
