package de.tomalbrc.toms_mobs.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.tslat.smartbrainlib.example.boilerplate.SBLExampleCommon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = SBLExampleCommon.class, remap = false)
public class SBLFabricMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/registries/DeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/neoforged/neoforge/registries/DeferredHolder;"))
    private static <T> DeferredHolder whyWouldYouDoThat(DeferredRegister instance, String name, Supplier<? extends T> sup, Operation<DeferredHolder<T, T>> original) {
        return null;
    }

    @Inject(method = "registerEntityAttributes", at = @At("HEAD"), cancellable = true)
    private static void boo(EntityAttributeCreationEvent ev, CallbackInfo ci) {
        ci.cancel();
    }
}
