package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Pose;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class NoDeathRotationLivingEntityHolder<T extends LivingEntity & AnimatedEntity> extends de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder<T> {

    public NoDeathRotationLivingEntityHolder(T parent, Model model) {
        super(parent, model);
    }

    @Override
    protected void applyPose(ServerPlayer serverPlayer, Pose pose, DisplayWrapper<?> display) {
        Vector3f translation = pose.translation();
        boolean isHead = display.isHead();
        boolean isDead = this.parent.deathTime > 0;
        if (!isHead && !isDead) {
            display.element().setLeftRotation(serverPlayer, pose.readOnlyLeftRotation());
        } else {
            Quaternionf bodyRotation = new Quaternionf();

            if (isHead) {
                bodyRotation.rotateY(((float)Math.PI / 180F) * -Mth.rotLerp(0.5F, this.parent.yHeadRotO - this.parent.yBodyRotO, this.parent.yHeadRot - this.parent.yBodyRot));
                bodyRotation.rotateX(((float)Math.PI / 180F) * Mth.lerp(0.5F, this.parent.xRotO, this.parent.getXRot()));
            }

            display.element().setLeftRotation(serverPlayer, bodyRotation.mul(pose.readOnlyLeftRotation()));
        }

        if (this.entityScale != 1.0F) {
            translation.mul(this.entityScale);
            display.element().setScale(serverPlayer, pose.scale().mul(this.entityScale));
        } else {
            display.element().setScale(serverPlayer, pose.readOnlyScale());
        }

        display.element().setTranslation(serverPlayer, translation.sub(0.0F, this.dimensions.height() - 0.01F, 0.0F));
        display.element().setRightRotation(serverPlayer, pose.readOnlyRightRotation());
        display.element().startInterpolationIfDirty(serverPlayer);
    }
}
