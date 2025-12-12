package de.tomalbrc.toms_mobs.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import de.tomalbrc.toms_mobs.util.NautilusRenameFix;
import net.minecraft.util.datafix.DataFixers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixers.class)
public abstract class DatafixersMixin {

    @Inject(
        method = "addFixers",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/datafix/fixes/OptionsMusicToastFix;<init>(Lcom/mojang/datafixers/schemas/Schema;Z)V")
    )
    private static void tm$addNaughtyFixer(DataFixerBuilder dataFixerBuilder, CallbackInfo ci, @Local(ordinal = 286) Schema schema287) {
        // heh, naughty, get it??

        dataFixerBuilder.addFixer(new NautilusRenameFix(schema287, true));
    }
}
