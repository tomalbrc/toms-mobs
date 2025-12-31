package de.tomalbrc.toms_mobs.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/LayeredRegistryAccess;Ljava/util/List;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;Lnet/minecraft/server/permissions/PermissionSet;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private static <D, R> void filament$almostDone(WorldLoader.InitConfig initConfig, WorldLoader.WorldDataSupplier<@NotNull D> worldDataSupplier, WorldLoader.ResultFactory<@NotNull D, @NotNull R> resultFactory, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<R>> cir, @Local(ordinal = 1) LayeredRegistryAccess<@NotNull RegistryLayer> layeredRegistryAccess) {
        MobRegistry.registerMobs(layeredRegistryAccess);
    }
}