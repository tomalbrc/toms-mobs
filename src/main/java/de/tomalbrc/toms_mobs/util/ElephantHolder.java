package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.wrapper.Bone;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import de.tomalbrc.bil.util.Utils;
import de.tomalbrc.toms_mobs.mixins.LivingEntityAccessor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ElephantHolder<T extends LivingEntity & AnimatedEntity> extends de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder<T> {
    public ElephantHolder(T parent, Model model) {
        super(parent, model);
    }

    @Override
    public void updateElement(ServerPlayer serverPlayer, DisplayWrapper<?> display, @Nullable Pose pose) {
        display.element().setYaw(this.parent.getYRot());
        super.updateElement(serverPlayer, display, pose);
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
    protected void applyPose(ServerPlayer serverPlayer, Pose pose, DisplayWrapper<?> display) {
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

        matrix4f.scale(pose.readOnlyScale());

        display.element().setTransformation(serverPlayer, matrix4f);
        display.element().startInterpolationIfDirty(serverPlayer);
    }

    @Override
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<ClientGamePacketListener>> consumer) {
        super.startWatchingExtraPackets(player, consumer);

        List<AttributeInstance> attributeInstances = new ArrayList<>();
        var cameraDist = this.parent.getAttribute(Attributes.CAMERA_DISTANCE);
        if (cameraDist != null) attributeInstances.add(cameraDist);

        var health = this.parent.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) attributeInstances.add(health);

        var scaleAttr = new AttributeInstance(Attributes.SCALE, (instance) -> {});
        scaleAttr.setBaseValue(this.parent.isBaby() ? 1.795 : 1.449);
        attributeInstances.add(scaleAttr);

        ClientboundUpdateAttributesPacket attributesPacket = new ClientboundUpdateAttributesPacket(this.getVehicleId(), attributeInstances);
        consumer.accept(attributesPacket);
    }

    @Override
    public int getVehicleId() {
        return this.collisionElement.getEntityId();
    }

    @Override
    public int getDisplayVehicleId() {
        return this.getVehicleId();
    }

    @Override
    public void onDimensionsUpdated(EntityDimensions dimensions) {
        this.updateEntityScale(this.scale);
        this.dimensions = dimensions;
        this.updateCullingBox();

        var attributeInstance = new AttributeInstance(Attributes.SCALE, (instance) -> {});
        attributeInstance.setBaseValue(this.parent.isBaby() ? 1.795 : 1.449);
        var attributesPacket = new ClientboundUpdateAttributesPacket(this.collisionElement.getEntityId(), List.of(attributeInstance));
        this.sendPacket(attributesPacket);

        var size = Utils.toSlimeSize(Math.min(dimensions.width(), dimensions.height()));
        this.collisionElement.setSize(size);
        this.sendPacket(new ClientboundBundlePacket(Utils.updateClientInteraction(this.hitboxInteraction, dimensions.scale(1.275f,1.05f))));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key, Object object) {
        super.onSyncedDataUpdated(key, object);

        if (key.equals(LivingEntityAccessor.getDATA_HEALTH_ID())) {
            this.collisionElement.getDataTracker().set(LivingEntityAccessor.getDATA_HEALTH_ID(), (Float)object);
        }
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();

        this.collisionElement.getDataTracker().set(LivingEntityAccessor.getDATA_HEALTH_ID(), this.parent.getEntityData().get(LivingEntityAccessor.getDATA_HEALTH_ID()));
    }

    public void slowRot() {
        for (Bone<?> bone : this.bones) {
            bone.element().setTeleportDuration(null, 3);
        }
    }

    public void fastRot() {
        for (Bone<?> bone : this.bones) {
            bone.element().setTeleportDuration(null, 1);
        }
    }
}
