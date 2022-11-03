package dev.igalaxy.voicechatinteraction.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ServerConfig {
    public boolean groupInteraction;
    public boolean whisperInteraction;
    public boolean sneakInteraction;
    public int minActivationThreshold;
    public boolean defaultInteractionToggle;

    public ServerConfig(FileConfiguration config) {
        groupInteraction = config.getBoolean("group_interaction");
        whisperInteraction = config.getBoolean("whisper_interaction");
        sneakInteraction = config.getBoolean("sneak_interaction");
        minActivationThreshold = config.getInt("minimum_activation_threshold");
        defaultInteractionToggle = config.getBoolean("default_interaction_toggle");
    }
}
