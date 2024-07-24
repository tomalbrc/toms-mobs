package de.tomalbrc.toms_mobs.mixins;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MeleeAttackGoal.class)
public interface MeleeAttackGoalAccessor {
    @Invoker("getTicksUntilNextAttack")
    int tomsmobs_getTicksUntilNextAttack();
}
