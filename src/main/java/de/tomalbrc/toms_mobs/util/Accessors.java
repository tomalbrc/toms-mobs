package de.tomalbrc.toms_mobs.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import de.tomalbrc.toms_mobs.mixins.LivingEntityAccessor;
import de.tomalbrc.toms_mobs.mixins.MeleeAttackGoalAccessor;

public class Accessors {
    public static int ticksUntilNextAttack(MeleeAttackGoal goal) {
        return ((MeleeAttackGoalAccessor) goal).am_getTicksUntilNextAttack();
    }

    public static boolean isJumping(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).am_isJumping();
    }
}
