package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class LivingEntityHolder<T extends LivingEntity & AnimatedEntity> extends GeyserCompatHolder<T> {

    public LivingEntityHolder(T parent, Model model, String geyserId) {
        super(parent, model, geyserId);
    }

    @Nullable
    private Node getRotationParent(Node node) {
        var currentNode = node;
        while (currentNode != null) {
            if (currentNode.headTag())
                return currentNode;

            currentNode = currentNode.parent();
        }

        return null;
    }

    @Override
    protected void applyPose(Pose pose, DisplayWrapper<?> display) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate(pose.readOnlyTranslation().sub(0f, parent.getBbHeight()/this.entityScale, 0f, new Vector3f()));
        matrix4f.rotate(pose.readOnlyLeftRotation());
        matrix4f.scale(new Vector3f(1.f));
        matrix4f.rotate(pose.readOnlyRightRotation());

        var node = getRotationParent(display.node());

        boolean isHead = node != null;
        boolean isDead = this.parent.deathTime > 0;

        if (isHead) {
            var y = (Mth.DEG_TO_RAD * Mth.rotLerp(0.5F, -this.parent.yHeadRotO + this.parent.yBodyRotO, -this.parent.yHeadRot + this.parent.yBodyRot));
            var x = (Mth.DEG_TO_RAD * Mth.lerp(0.5F, this.parent.xRotO, this.parent.getXRot()));

            Vector3f pivotOffset = node.transform().origin().get(new Vector3f()).mul(1, 0, 1);
            matrix4f.translateLocal(pivotOffset);

            matrix4f.rotateLocalX(x);
            matrix4f.rotateLocalY(y);

            matrix4f.translateLocal(pivotOffset.negate());
        }

        if (isDead) {
            matrix4f.translateLocal(0, this.parent.getBbHeight(), 0);
            matrix4f.rotateLocalZ(-this.deathAngle * ((float)Math.PI / 2F));
            matrix4f.translateLocal(0, -this.parent.getBbHeight(), 0);
        }
        matrix4f.scaleLocal(this.entityScale);

        if (this.entityScale != 1.0F) {
            matrix4f.scale(pose.readOnlyScale());
        } else {
            matrix4f.scale(pose.readOnlyScale());
        }


        display.element().setTransformation(matrix4f);
        display.element().startInterpolationIfDirty();
    }
}
