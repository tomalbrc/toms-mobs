package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Pose;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SimpleMovementRotatingHolder<T extends LivingEntity & AnimatedEntity> extends LivingEntityHolder<T> {
    private float lastYaw = 0.f;
    private float lastPitch = 0.f;

    public SimpleMovementRotatingHolder(T parent, Model model) {
        super(parent, model);
    }

    @Override
    protected void applyPose(ServerPlayer serverPlayer, Pose pose, DisplayWrapper<?> display) {
        var translation = new Vector3f(0, -parent.getBbHeight(), 0);
        Matrix4f matrix4f = pose.matrix().get(new Matrix4f()).translateLocal(translation);

        Vector3f movement = parent.getDeltaMovement().toVector3f();
        if (movement.lengthSquared() > 0.0001f) {
            movement.normalize();
            float movementYaw = (float) Math.atan2(-movement.x, movement.z);
            float movementPitch = (float) Math.asin(movement.y);

            lastPitch = Mth.rotLerpRad(0.8f, movementPitch, lastPitch);
            lastYaw = Mth.rotLerpRad(0.8f, movementYaw, lastYaw);

            matrix4f
                    .rotateLocalZ(0)
                    .rotateLocalX(-lastPitch)
                    .rotateLocalY(-lastYaw);

            display.element().setTransformation(serverPlayer, matrix4f);
            display.element().startInterpolationIfDirty(serverPlayer);
        }
    }

    @Override
    public void updateElement(ServerPlayer serverPlayer, DisplayWrapper<?> display, @Nullable Pose pose) {
        if (pose == null) {
            this.applyPose(serverPlayer, display.getLastPose(serverPlayer), display);
        } else {
            this.applyPose(serverPlayer, pose, display);
        }
    }

    @Override
    protected void updateElement(ServerPlayer serverPlayer, DisplayWrapper<?> display) {
        var queryResult = this.animationComponent.findPose(serverPlayer, display);
        if (queryResult != null) {
            if (queryResult.owner() != serverPlayer && display.element().getDataTracker().isDirty()) {
                return;
            }

            this.updateElement(null, display, queryResult.pose());
        } else {
            this.updateElement(null, display, display.getDefaultPose());
        }
    }
}
