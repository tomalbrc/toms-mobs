package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.util.Utils;
import de.tomalbrc.toms_mobs.mixins.accessor.LivingEntityAccessor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ElephantHolder<T extends LivingEntity & AnimatedEntity> extends LivingEntityHolder<T> {
    public ElephantHolder(T parent, Model model) {
        super(parent, model);
    }

    @Override
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<@NotNull ClientGamePacketListener>> consumer) {
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
}
