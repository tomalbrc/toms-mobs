package de.tomalbrc.toms_mobs.mixins;

import net.minecraft.world.entity.EntityType;
import net.tslat.smartbrainlib.SBLFabric;
import net.tslat.smartbrainlib.example.boilerplate.SBLExampleCommon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SBLExampleCommon.class, remap = false)
public class SBLFabricMixin {
    @Inject(method = "registerEntity", at = @At("HEAD"), cancellable = true)
    private static void whyWouldYouDoThat(String name, EntityType.Builder<?> builder, CallbackInfoReturnable<EntityType<?>> cir) {
        cir.cancel();
    }
}
