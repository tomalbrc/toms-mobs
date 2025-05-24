package de.tomalbrc.toms_mobs.util;

import me.zimzaza4.geyserutils.fabric.api.EntityUtils;
import me.zimzaza4.geyserutils.geyser.replace.JavaAddEntityTranslatorReplace;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.geysermc.geyser.api.GeyserApi;

import java.util.Collection;

public class GeyserCompat {
    private static Boolean GEYSER_LOADED = null;

    public static boolean isGeyserLoaded() {
        if (GEYSER_LOADED == null) {
            GEYSER_LOADED = FabricLoader.getInstance().isModLoaded("geyser-fabric") && FabricLoader.getInstance().isModLoaded("geyserutils");
        }
        return GEYSER_LOADED;
    }

    public static boolean isGeyserPlayer(ServerPlayer serverPlayer) {
        return isGeyserLoaded() && GeyserApi.api().isBedrockPlayer(serverPlayer.getUUID());
    }

    public static void setCustomEntity(ServerPlayer player, int entityId, String id) {
        if (isGeyserLoaded() && isGeyserPlayer(player)) {
            EntityUtils.setCustomEntity(player, entityId, id);
        }
    }

    public static <T extends Player> void setCustomEntity(Collection<T> players, int entityId, String id) {
        if (isGeyserLoaded()) {
            for (T player : players) {
                if (player instanceof ServerPlayer serverPlayer && isGeyserPlayer(serverPlayer)) {
                    EntityUtils.setCustomEntity(serverPlayer, entityId, id);
                }
            }
        }
    }
}
