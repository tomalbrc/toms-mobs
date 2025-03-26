package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Pose;
import de.tomalbrc.toms_mobs.entity.passive.Seagull;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MovementRotatingHolder<T extends LivingEntity & AnimatedEntity> extends LivingEntityHolder<T> {
    private float lastYaw = 0.f;
    private float lastPitch = 0.f;
    private float lastRoll = 0.f;

    private boolean init = false;

    public MovementRotatingHolder(T parent, Model model) {
        super(parent, model);
    }

    @Override
    protected void applyPose(Pose pose, DisplayWrapper display) {
        if (((this.parent instanceof Seagull seagull && !seagull.canFlyCurrently())) || !init) {
            super.applyPose(pose, display);
            init = true;
            return;
        }

        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate(pose.readOnlyTranslation());
        matrix4f.rotate(pose.readOnlyLeftRotation());
        matrix4f.scale(pose.readOnlyScale());
        matrix4f.rotate(pose.readOnlyRightRotation());

        matrix4f.translateLocal(0, -this.parent.getBbHeight(), 0);

        Vector3f movement = parent.getDeltaMovement().toVector3f();
        if (movement.lengthSquared() > 0.0001f) {
            movement.normalize();
            float movementYaw = (float) Math.atan2(-movement.x, movement.z);
            float movementPitch = (float) Math.asin(movement.y);

            float movementRoll = 0.f; // Default roll is zero

            if (Math.abs(movementPitch) < Math.PI / 4) {
                movementRoll = (float) Math.sin(movementYaw) * 0.1f;
            }
            lastPitch = Mth.rotLerpRad(0.5f, movementPitch, lastPitch);
            lastYaw = Mth.rotLerpRad(0.5f, movementYaw, lastYaw);
            lastRoll = Mth.rotLerpRad(0.5f, movementRoll, lastRoll);

            matrix4f
                    .rotateLocalX(-lastPitch)
                    .rotateLocalZ(lastRoll);
//                  .rotateLocalY(-lastYaw + Mth.PI);
        }

        display.element().setTransformation(matrix4f);
        display.element().startInterpolationIfDirty();
    }
}
