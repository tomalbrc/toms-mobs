package de.tomalbrc.toms_mobs.util;

import net.minecraft.world.entity.LivingEntity;
import de.tomalbrc.resin.api.AjHolder;
import de.tomalbrc.resin.api.Animator;
import de.tomalbrc.resin.api.VariantController;

public class AnimationHelper {

    public static void updateWalkAnimation(LivingEntity entity, AjHolder holder) {
        updateWalkAnimation(entity, holder, 0);
    }

    public static void updateWalkAnimation(LivingEntity entity, AjHolder holder, int priority) {
        Animator animator = holder.getAnimator();
        if (entity.walkAnimation.isMoving() && entity.walkAnimation.speed() > 0.02) {
            animator.playAnimation("walk", priority);
            animator.pauseAnimation("idle");
        } else {
            animator.pauseAnimation("walk");
            animator.playAnimation("idle", priority, true);
        }
    }

    public static void updateHurtVariant(LivingEntity entity, AjHolder holder) {
        VariantController controller = holder.getVariantController();
        if (entity.hurtTime > 0 || entity.deathTime > 0) {
            controller.setVariant("hurt");
        } else if (controller.isCurrentVariant("hurt")) {
            controller.setDefaultVariant();
        }
    }
}