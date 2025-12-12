package de.tomalbrc.toms_mobs.mixins;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import de.tomalbrc.toms_mobs.util.Util;
import net.minecraft.util.datafix.schemas.V1460;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(V1460.class)
public abstract class V1460Mixin {

    @Shadow
    protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
    }

    @Inject(
        method = "registerEntities",
        at = @At("RETURN")
    )
    public void tm$registerEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        var map = cir.getReturnValue();
        registerMob(schema, map, Util.id("ice_cluster").toString());
        registerMob(schema, map, Util.id("ice_spike").toString());
        registerMob(schema, map, Util.id("ice_spike_small").toString());
        registerMob(schema, map, Util.id("iceologer").toString());
        registerMob(schema, map, Util.id("sculkling").toString());
        registerMob(schema, map, Util.id("showmaster").toString());
        registerMob(schema, map, Util.id("snake").toString());
        registerMob(schema, map, Util.id("butterfly").toString());
        registerMob(schema, map, Util.id("capybara").toString());
        registerMob(schema, map, Util.id("elephant").toString());
        registerMob(schema, map, Util.id("firemoth").toString());
        registerMob(schema, map, Util.id("lobster").toString());
        registerMob(schema, map, Util.id("mantaray").toString());
        registerMob(schema, map, Util.id("nautilus").toString());
        registerMob(schema, map, Util.id("penguin").toString());
        registerMob(schema, map, Util.id("seagull").toString());
        registerMob(schema, map, Util.id("tuna").toString());
    }
}
