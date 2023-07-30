package ca.bungo.bubblechat.events;

import ca.bungo.bubblechat.BubbleChat;
import ca.bungo.bubblechat.types.ChatBubble;
import ca.bungo.bubblechat.utility.ChatUtility;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatEvent implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event){
        Player player = event.getPlayer();
        String message = ((TextComponent)event.message()).content();
        event.setCancelled(true);

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
