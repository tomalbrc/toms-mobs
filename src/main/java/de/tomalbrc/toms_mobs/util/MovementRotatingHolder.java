package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Pose;
import de.tomalbrc.toms_mobs.entity.passive.Seagull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MovementRotatingHolder<T extends LivingEntity & AnimatedEntity> extends LivingEntityHolder<T> {
    public float roll;

    private float lastRoll = 0.f;
    private float lastYaw = 0.f;
    private float lastPitch = 0.f;

    private boolean init = false;

    public MovementRotatingHolder(T parent, Model model) {
        super(parent, model);
    }

    @Override
    protected void applyPose(ServerPlayer serverPlayer, Pose pose, DisplayWrapper display) {
        if ((this.parent instanceof Seagull seagull && !seagull.isFlying()) || !init) {
            super.applyPose(serverPlayer, pose, display);
            init = true;
            return;
        }

        Matrix4f matrix4f = pose.matrix().get(new Matrix4f());
        Vector3f movement = parent.getDeltaMovement().toVector3f();
        if (movement.lengthSquared() > 0.0001f) {
            movement.normalize();
            float movementYaw = (float) Math.atan2(-movement.x, movement.z);
            float movementPitch = (float) Math.asin(movement.y);

            lastPitch = Mth.rotLerpRad(.5f, movementPitch, lastPitch);
            lastYaw = Mth.rotLerpRad(.5f, movementYaw, lastYaw);
            lastRoll = Mth.rotLerp(.5f, roll, lastRoll);

            matrix4f.rotateLocalX(-lastPitch)
                    .rotateLocalZ(-lastRoll*Mth.DEG_TO_RAD);
        }

        display.element().setTransformation(serverPlayer, matrix4f.scaleLocal(this.scale).translateLocal(0, -this.parent.getBbHeight(), 0));
        display.element().startInterpolationIfDirty(serverPlayer);
    }
}
