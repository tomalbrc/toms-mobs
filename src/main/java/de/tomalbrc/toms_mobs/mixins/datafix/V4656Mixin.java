package de.tomalbrc.toms_mobs.mixins.datafix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.V4656;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(V4656.class)
public abstract class V4656Mixin {

    @Inject(
        method = "registerEntities",
        at = @At("RETURN")
    )
    public void tm$registerEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        var map = cir.getReturnValue();
        Supplier<TypeTemplate> templateSupplier = map.get("minecraft:nautilus");
        if (templateSupplier != null) {
            map.put("toms_mobs:nautilus", templateSupplier);
        }
    }
}
