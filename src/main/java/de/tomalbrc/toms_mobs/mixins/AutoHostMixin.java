package de.tomalbrc.toms_mobs.mixins;

import de.tomalbrc.toms_mobs.ModConfig;
import eu.pb4.polymer.autohost.impl.AutoHost;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AutoHost.class)
public class AutoHostMixin {
    @Inject(remap = false, method = "init", at = @At(value = "HEAD"))
    private static void tomsmobs$onInitAutoHost(MinecraftServer server, CallbackInfo ci) {
        AutoHost.config.enabled = ModConfig.getInstance().forceAutohost; // we need it enabled by default..
    }
}
