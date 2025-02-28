package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.api.AnimatedHolder;
import de.tomalbrc.bil.api.Animator;
import de.tomalbrc.toms_mobs.entity.passive.Capybara;
import net.minecraft.world.entity.LivingEntity;

public class AnimationHelper {

    public static void updateWalkAnimation(LivingEntity entity, AnimatedHolder holder) {
        updateWalkAnimation(entity, holder, 0);
    }

    public static void updateWalkAnimation(LivingEntity entity, AnimatedHolder holder, int priority) {
        Animator animator = holder.getAnimator();
        if (entity.walkAnimation.isMoving() && entity.walkAnimation.speed() > 0.02) {
            animator.playAnimation("walk", priority);
            animator.pauseAnimation("idle");
        } else {
            animator.pauseAnimation("walk");
            animator.playAnimation("idle", priority, true);
        }
    }

    public static void updateAquaticWalkAnimation(LivingEntity entity, AnimatedHolder holder) {
        Animator animator = holder.getAnimator();
        if (entity.isInWater()) {
            if ((entity.getDeltaMovement().length() > 0.05 || entity.walkAnimation.speed() > 0.02)) {
                animator.pauseAnimation("idle");
                animator.pauseAnimation("walk");
                animator.playAnimation("swim");
            } else {
                animator.pauseAnimation("swim");
                animator.pauseAnimation("walk");
                animator.playAnimation("idle");
            }
        } else {
            if (entity.walkAnimation.isMoving() && entity.walkAnimation.speed() > 0.02) {
                animator.pauseAnimation("idle");
                animator.pauseAnimation("swim");
                animator.playAnimation("walk");
            } else {
                animator.pauseAnimation("swim");
                animator.pauseAnimation("walk");
                animator.playAnimation("idle");
            }
        }
    }

    public static void updateFishAnimation(LivingEntity entity, AnimatedHolder holder) {
        Animator animator = holder.getAnimator();
        if (entity.isInWater()) {
            animator.pauseAnimation("idle");
            animator.pauseAnimation("walk");
            animator.playAnimation("swim");
        } else {
            animator.pauseAnimation("idle");
            animator.pauseAnimation("swim");
            animator.playAnimation("walk");
        }
    }

    public static void updateCapybaraWalkAnimation(Capybara entity, AnimatedHolder holder) {
        if (entity.isRelaxing()) return;

        Animator animator = holder.getAnimator();
        if (entity.isInWater()) {
            if ((entity.getDeltaMovement().length() > 0.05 || entity.walkAnimation.speed() > 0.02)) {
                animator.pauseAnimation("idle");
                animator.pauseAnimation("relax");
                animator.pauseAnimation("walk");
                animator.playAnimation("swim");
            } else {
                animator.pauseAnimation("swim");
                animator.pauseAnimation("walk");
                animator.pauseAnimation("relax");
                animator.playAnimation("idle");
            }
        } else {
            if (entity.walkAnimation.isMoving() && entity.walkAnimation.speed() > 0.02) {
                animator.pauseAnimation("idle");
                animator.pauseAnimation("relax");
                animator.pauseAnimation("swim");
                animator.playAnimation("walk");
            } else {
                animator.pauseAnimation("swim");
                animator.pauseAnimation("walk");
                animator.pauseAnimation("relax");
                animator.pauseAnimation("idle");
                animator.playAnimation(entity.isRelaxing() ? "relax":"idle");
            }
        }
    }

    public static void updateHurtVariant(LivingEntity entity, AnimatedHolder holder) {
        updateHurtColor(entity, holder);
    }

    public static void updateHurtColor(LivingEntity entity, AnimatedHolder holder) {
        if (entity.hurtTime > 0 || entity.deathTime > 0)
            holder.setColor(0xff7e7e);
        else
            holder.clearColor();
    }
}