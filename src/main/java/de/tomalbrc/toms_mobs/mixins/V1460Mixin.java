package de.tomalbrc.toms_mobs.mixins;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import de.tomalbrc.toms_mobs.entity.hostile.*;
import de.tomalbrc.toms_mobs.entity.passive.*;
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
        registerMob(schema, map, IceCluster.ID.toString());
        registerMob(schema, map, IceSpike.ID.toString());
        registerMob(schema, map, IceSpikeSmall.ID.toString());
        registerMob(schema, map, Iceologer.ID.toString());
        registerMob(schema, map, Sculkling.ID.toString());
        registerMob(schema, map, Showmaster.ID.toString());
        registerMob(schema, map, Snake.ID.toString());
        registerMob(schema, map, Butterfly.ID.toString());
        registerMob(schema, map, Capybara.ID.toString());
        registerMob(schema, map, Elephant.ID.toString());
        registerMob(schema, map, Firemoth.ID.toString());
        registerMob(schema, map, Lobster.ID.toString());
        registerMob(schema, map, Mantaray.ID.toString());
        registerMob(schema, map, Nautilus.ID.toString());
        registerMob(schema, map, Penguin.ID.toString());
        registerMob(schema, map, Seagull.ID.toString());
        registerMob(schema, map, Tuna.ID.toString());
    }
}
