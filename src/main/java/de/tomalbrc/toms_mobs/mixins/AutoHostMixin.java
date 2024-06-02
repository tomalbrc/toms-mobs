package de.tomalbrc.toms_mobs.mixins;

import de.tomalbrc.toms_mobs.ModConfig;
import eu.pb4.polymer.autohost.impl.AutoHost;
import eu.pb4.polymer.autohost.impl.AutoHostConfig;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AutoHost.class)
public class AutoHostMixin {
    @Redirect(remap = false, method = "init", at = @At(value = "FIELD", target = "Leu/pb4/polymer/autohost/impl/AutoHost;config:Leu/pb4/polymer/autohost/impl/AutoHostConfig;", opcode = Opcodes.PUTSTATIC))
    private static void tomsmobs$forceAutoHost(AutoHostConfig obj) {
        obj.enabled = ModConfig.getInstance().forceAutohost; // we need it enabled by default..
    }
}
