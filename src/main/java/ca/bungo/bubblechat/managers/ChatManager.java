package ca.bungo.bubblechat.managers;

import ca.bungo.bubblechat.types.ChatBubble;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChatManager {
	public Map<Player, ChatBubble> playerChatBubbles = new HashMap<>();


    public void removePlayer(Player player, ChatBubble chatBubble) {
    	chatBubble.remove();
    	playerChatBubbles.remove(player);
    }

    public Collection<ChatBubble> getChatBubbles() {
		return playerChatBubbles.values();
	}

}