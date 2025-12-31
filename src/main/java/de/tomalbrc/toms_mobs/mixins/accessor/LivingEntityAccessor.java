package de.tomalbrc.toms_mobs.mixins.accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("jumping")
    boolean tomsmobs_isJumping();

    @Accessor("DATA_HEALTH_ID")
    static EntityDataAccessor<@NotNull Float> getDATA_HEALTH_ID() {
        throw new AssertionError();
    }
}
