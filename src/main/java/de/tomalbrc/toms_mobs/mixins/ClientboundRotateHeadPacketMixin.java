package de.tomalbrc.toms_mobs.mixins;

import de.tomalbrc.toms_mobs.entity.passive.Elephant;
import de.tomalbrc.toms_mobs.util.ElephantHolder;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundRotateHeadPacket.class)
public class ClientboundRotateHeadPacketMixin {
    @Mutable
    @Shadow @Final private int entityId;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;B)V", at = @At("TAIL"))
    private void tm$customRot(Entity entity, byte b, CallbackInfo ci) {
        if (entity instanceof Elephant elephant) {
            ElephantHolder holder = (ElephantHolder) elephant.getHolder();
            entityId = holder.getVehicleId();
        }
    }
}
