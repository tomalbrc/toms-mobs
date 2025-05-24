package de.tomalbrc.toms_mobs.mixins;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.toms_mobs.util.GeyserCompat;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.packettweaker.PacketContext;

@Mixin(value = AnimatedEntity.class, remap = false)
public interface AnimatedEntityMixin {
    @Inject(method = "getPolymerEntityType", at = @At("RETURN"), cancellable = true)
    private void tm$cursedGeyserCompat(PacketContext context, CallbackInfoReturnable<EntityType<?>> cir) {
        if (context.getPlayer() != null && GeyserCompat.isGeyserPlayer(context.getPlayer())) {
            cir.setReturnValue(EntityType.PIG);
        }

    }
}
