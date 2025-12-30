package de.tomalbrc.toms_mobs.mixins;

import de.tomalbrc.bil.api.AnimatedEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract boolean hasControllingPassenger();

    @Shadow
    private Level level;

    @Inject(method = "isLocalInstanceAuthoritative", at = @At(value = "RETURN"), cancellable = true)
    public void tomsmobs$isLocalInstanceAuthoritative(CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof AnimatedEntity && this.hasControllingPassenger()) {
            cir.setReturnValue(true);
        }
    }
}
