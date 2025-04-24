package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import me.zimzaza4.geyserutils.fabric.api.EntityUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.LivingEntity;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.function.Consumer;

public class GeyserCompatHolder<T extends LivingEntity & AnimatedEntity> extends LivingEntityHolder<T> {
    public GeyserCompatHolder(T parent, Model model) {
        super(parent, model);
    }

    @Override
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<ClientGamePacketListener>> consumer) {
        super.startWatchingExtraPackets(player, consumer);

        if (FloodgateApi.getInstance().isFloodgatePlayer(player.getPlayer().getUUID())) {
            //EntityUtils.setCustomEntity(player.player, this.parent.getId(), "modelengine:" + BuiltInRegistries.ENTITY_TYPE.getKey(this.parent.getType()).toString().replace(':', '.').replace("_", ""));

        }
    }

    @Override
    public boolean startWatching(ServerGamePacketListenerImpl player) {
        EntityUtils.setCustomEntity(player.player, this.parent.getId(), "modelengine:toms_mobs.elephant");

        return super.startWatching(player);
    }

}
