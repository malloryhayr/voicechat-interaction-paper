package dev.igalaxy.voicechatinteraction;

import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import org.bukkit.GameEvent;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceChatInteractionPlugin implements VoicechatPlugin {

    public static VoicechatApi voicechatApi;
    private static ConcurrentHashMap<UUID, Long> cooldowns;

    @Nullable
    public static VoicechatServerApi voicechatServerApi;

    @Nullable
    private OpusDecoder decoder;

    @Override
    public String getPluginId() {
        return VoiceChatInteraction.PLUGIN_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        voicechatApi = api;
        cooldowns = new ConcurrentHashMap<>();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicPacket);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        voicechatServerApi = event.getVoicechat();
    }

    private void onMicPacket(MicrophonePacketEvent event) {
        VoicechatConnection senderConnection = event.getSenderConnection();
        if (senderConnection == null) {
            return;
        }

        if (event.getPacket().getOpusEncodedData().length <= 0) {
            return;
        }

        if (!VoiceChatInteraction.SERVER_CONFIG.groupInteraction) {
            if (senderConnection.isInGroup()) {
                return;
            }
        }

        if (!VoiceChatInteraction.SERVER_CONFIG.whisperInteraction) {
            if (event.getPacket().isWhispering()) {
                return;
            }
        }

        ServerPlayer player = senderConnection.getPlayer();
        if (!(senderConnection.getPlayer().getPlayer() instanceof Player bukkitPlayer)) {
            VoiceChatInteraction.LOGGER.warn("Received microphone packet from non-player");
            return;
        }

        if (!VoiceChatInteraction.SERVER_CONFIG.sneakInteraction) {
            if (bukkitPlayer.isSneaking()) {
                return;
            }
        }

        if (decoder == null) {
            decoder = event.getVoicechat().createDecoder();
        }

        decoder.resetState();
        short[] decoded = decoder.decode(event.getPacket().getOpusEncodedData());

        if (AudioUtils.calculateAudioLevel(decoded) < VoiceChatInteraction.SERVER_CONFIG.minActivationThreshold) {
            return;
        }

        bukkitPlayer.getServer().getScheduler().runTask(VoiceChatInteraction.INSTANCE, () -> {
            if (activate(player)) {
                bukkitPlayer.getWorld().sendGameEvent(null, VoiceChatInteraction.VOICE_GAME_EVENT, bukkitPlayer.getLocation().toVector());
            }
        });
    }

    private boolean activate(ServerPlayer player) {
        Long lastTimestamp = cooldowns.get(player.getUuid());
        long currentTime = VoiceChatInteraction.SERVER.getPlayer(player.getUuid()).getWorld().getGameTime();
        if (lastTimestamp == null || currentTime - lastTimestamp > 20L) {
            cooldowns.put(player.getUuid(), currentTime);
            return true;
        }
        return false;
    }

}
