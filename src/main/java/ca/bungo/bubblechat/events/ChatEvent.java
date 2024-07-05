package ca.bungo.bubblechat.events;

import ca.bungo.bubblechat.BubbleChat;
import ca.bungo.bubblechat.commands.CommandMuteUnmute;
import ca.bungo.bubblechat.types.ChatBubble;
import ca.bungo.bubblechat.utility.ChatUtility;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatEvent implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event){
        Player player = event.getPlayer();
        String message = ((TextComponent)event.message()).content();

        //event.setCancelled(true);


        ChatRenderer renderer = event.renderer();
        event.renderer((source, displayName, content, viewer) ->{
            Component modifiedText = renderer.render(source, displayName, content, viewer);

            if(!(viewer instanceof Player viewingPlayer)) return modifiedText;

            //ToDo: Read Modified Text & Create Localized Chat Bubble for the viewingPlayer

            return modifiedText;
        });


        if(message.startsWith("((") ||
            (message.startsWith("(") && message.endsWith(")")) ||
            message.startsWith("&8") ||
            message.startsWith("<dark_gray>")) 
            return;

        if(CommandMuteUnmute.isPlayerMuted(player)){
            player.sendMessage(ChatUtility.formatMessage("&4You have been muted!"));
            return;
        }

        if(!BubbleChat.instance.chatManager.playerChatBubbles.containsKey(player)){
            ChatBubble chatBubble = new ChatBubble(player, message);
            BubbleChat.instance.chatManager.playerChatBubbles.put(player, chatBubble);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BubbleChat.instance, chatBubble::spawn);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BubbleChat.instance, () -> {
                if (chatBubble.removeMessage(0)) BubbleChat.instance.chatManager.removePlayer(player, chatBubble);
            }, Math.max(message.length()*2, 120));
        }
        else{
            ChatBubble chatBubble = BubbleChat.instance.chatManager.playerChatBubbles.get(player);

            int messageID = chatBubble.addMessage(message);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BubbleChat.instance, ()->{
                if (chatBubble.removeMessage(messageID)) BubbleChat.instance.chatManager.removePlayer(player, chatBubble);
            }, Math.max(message.length()*2, 120));
        }

        logSpy(player.getName(), message);
    }

    private void logSpy(String user, String message){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.hasPermission("bubblechat.chatspy"))
                player.sendMessage(ChatUtility.formatMessage("&7[&eChat-Bubble&7]: &e" + user + "&7: " + message));
        }
    }

}
