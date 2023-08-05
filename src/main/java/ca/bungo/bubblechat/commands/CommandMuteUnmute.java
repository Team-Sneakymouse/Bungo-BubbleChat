package ca.bungo.bubblechat.commands;

import ca.bungo.bubblechat.BubbleChat;
import ca.bungo.bubblechat.utility.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class CommandMuteUnmute extends Command {

    public CommandMuteUnmute(@NotNull String name) {
        super(name);
        this.description = "Handle mute status of a player!";
        this.usageMessage = "/(un)mute <Player> [Reason]";
        this.setAliases(List.of("unmute"));
        this.setPermission("bubblechat.admin");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!(args.length >= 1)) {
            sender.sendMessage(ChatUtility.formatMessage("&4Invalid Arguments! &e" + this.usageMessage));
            return false;
        }

        String playerName = args[0];
        OfflinePlayer target = getPlayerFromName(playerName);

        if(target == null){
            sender.sendMessage(ChatUtility.formatMessage("&cInvalid player name! Did you type it correctly?"));
            return false;
        }

        if(label.equalsIgnoreCase("mute")){
            String reason = null;
            if(args.length >= 2){
                StringBuilder reasonBuilder = new StringBuilder();
                for(int i = 1; i < args.length; i++){
                    reasonBuilder.append(args[i]).append(" ");
                }
                reason = reasonBuilder.toString();
            }

            if(!mutePlayer(target, reason)){
                sender.sendMessage(ChatUtility.formatMessage("&4Failed to mute the player? Are they already muted?"));
                return false;
            }
            sender.sendMessage(ChatUtility.formatMessage("&aMuted player &e" + target.getName()));
        }
        else if(label.equalsIgnoreCase("unmute")){
            if(!unmutePlayer(target)){
                sender.sendMessage(ChatUtility.formatMessage("&cFailed to unmute the player! Are you sure they are muted?"));
                return false;
            }
            sender.sendMessage(ChatUtility.formatMessage("&aUnmuted &e" + target.getName()));
        }

        return false;
    }

    private OfflinePlayer getPlayerFromName(String playerName){
        OfflinePlayer player = Bukkit.getPlayerExact(playerName);
        if(player == null){
            ConfigurationSection mutedSection = BubbleChat.instance.getConfig().getConfigurationSection("muted-players");
            if(mutedSection == null)
                return null;
            for(String uuid : mutedSection.getKeys(false)){
                String name = mutedSection.getString(uuid + ".username");
                if(name != null && name.equalsIgnoreCase(playerName)){
                    player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    break;
                }
            }
        }
        return player;
    }

    private boolean mutePlayer(OfflinePlayer player, String reason){
        ConfigurationSection mutedSection = BubbleChat.instance.getConfig().getConfigurationSection("muted-players");
        if(mutedSection == null)
            mutedSection = BubbleChat.instance.getConfig().createSection("muted-players");
        ConfigurationSection playerSection = mutedSection.getConfigurationSection(player.getUniqueId().toString());
        if(playerSection == null)
            playerSection = mutedSection.createSection(player.getUniqueId().toString());
        else {
            return false;
        }
        playerSection.set("username", player.getName());
        playerSection.set("mute-reason", reason);
        BubbleChat.instance.saveConfig();
        return true;
    }

    private boolean unmutePlayer(OfflinePlayer player){
        ConfigurationSection mutedSection = BubbleChat.instance.getConfig().getConfigurationSection("muted-players");
        if(mutedSection == null)
            mutedSection = BubbleChat.instance.getConfig().createSection("muted-players");
        ConfigurationSection playerSection = mutedSection.getConfigurationSection(player.getUniqueId().toString());
        if(playerSection == null){
            return false;
        }
        mutedSection.set(player.getUniqueId().toString(), null);
        BubbleChat.instance.saveConfig();
        return true;
    }

    public static boolean isPlayerMuted(Player player){
        ConfigurationSection mutedSection = BubbleChat.instance.getConfig().getConfigurationSection("muted-players");
        if(mutedSection == null)
            return false;
        ConfigurationSection playerSection = mutedSection.getConfigurationSection(player.getUniqueId().toString());
        return  playerSection != null;
    }
}
