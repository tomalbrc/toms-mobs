package de.tomalbrc.toms_mobs.mixins;

import net.tslat.smartbrainlib.SBLFabric;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SBLFabric.class, remap = false)
public class SBLFabricMixin {
    @Inject(method = "registerEntities", at = @At("HEAD"), cancellable = true)
    private static void whyWouldYouDoThat(CallbackInfo ci) {
        ci.cancel();
    }
}
