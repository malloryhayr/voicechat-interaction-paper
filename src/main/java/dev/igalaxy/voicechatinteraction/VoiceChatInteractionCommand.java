package dev.igalaxy.voicechatinteraction;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoiceChatInteractionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (sender instanceof Player) {
                VoiceChatInteractionPlugin plugin = VoiceChatInteraction.voicechatPlugin;
                Player player = (Player) sender;

                if (args.length == 0) {
                    player.sendMessage("/voicechat_interaction toggle [<player>]");
                    return true;
                }

                if (args[0].equals("toggle")) {
                    if (args.length >= 2 && player.hasPermission("voicechat_interaction.toggle.others")) {
                        Player other = Bukkit.getPlayer(args[1]);
                        plugin.setInteractionToggle(other, !plugin.getInteractionToggle(other));
                        player.sendMessage("Interactions toggled to " + plugin.getInteractionToggle(other) + " for " + other.name());
                        other.sendMessage(player.name() + " toggled your interactions to " + plugin.getInteractionToggle(other));
                    } else if (player.hasPermission("voicechat_interaction.toggle")) {
                        plugin.setInteractionToggle(player, !plugin.getInteractionToggle(player));
                        player.sendMessage("Interactions toggled to " + plugin.getInteractionToggle(player) + " for yourself");
                    } else {
                        player.sendMessage("You do not have permission to use that command (voicechat_interaction.toggle)");
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
