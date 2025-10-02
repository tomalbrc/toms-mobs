package de.tomalbrc.toms_mobs.mixins;

import de.tomalbrc.toms_mobs.entity.effect.CustomMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> holder);

    @Inject(method = "decreaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void tm$nautilusEffect(int i, CallbackInfoReturnable<Integer> cir) {
        if (this.hasEffect(CustomMobEffects.NAUTILUS_BREATH))
            cir.setReturnValue(i);
    }
}
