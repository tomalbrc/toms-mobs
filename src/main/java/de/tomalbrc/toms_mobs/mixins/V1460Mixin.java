package de.tomalbrc.toms_mobs.mixins;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import de.tomalbrc.toms_mobs.util.Util;
import net.minecraft.util.datafix.schemas.V1460;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
    public void registerEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        var map = cir.getReturnValue();
        registerMob(schema, map, mod("ice_cluster"));
        registerMob(schema, map, mod("ice_spike"));
        registerMob(schema, map, mod("ice_spike_small"));
        registerMob(schema, map, mod("iceologer"));
        registerMob(schema, map, mod("sculkling"));
        registerMob(schema, map, mod("showmaster"));
        registerMob(schema, map, mod("snake"));
        registerMob(schema, map, mod("butterfly"));
        registerMob(schema, map, mod("capybara"));
        registerMob(schema, map, mod("elephant"));
        registerMob(schema, map, mod("firemoth"));
        registerMob(schema, map, mod("lobster"));
        registerMob(schema, map, mod("mantaray"));
        registerMob(schema, map, mod("nautilus"));
        registerMob(schema, map, mod("penguin"));
        registerMob(schema, map, mod("seagull"));
        registerMob(schema, map, mod("tuna"));
    }

    @Unique
    private static String mod(String path) {
        return Util.id(path).toString();
    }
}
