package ca.bungo.bubblechat;

import ca.bungo.bubblechat.commands.CommandCleanTextDisplays;
import ca.bungo.bubblechat.commands.CommandMuteUnmute;
import ca.bungo.bubblechat.events.ChatEvent;
import ca.bungo.bubblechat.managers.ChatManager;
import ca.bungo.bubblechat.types.ChatBubble;
import org.bukkit.plugin.java.JavaPlugin;

public final class BubbleChat extends JavaPlugin {

    public static BubbleChat instance;

    public ChatManager chatManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        this.saveDefaultConfig();

        this.chatManager = new ChatManager();

        this.getServer().getPluginManager().registerEvents(new ChatEvent(), this);
        //this.getServer().getCommandMap().register("bubblechat", new CommandCleanTextDisplays("cleanchat"));
        this.getServer().getCommandMap().register("bubblechat", new CommandMuteUnmute("mute"));
    }

    @Override
    public void onDisable() {
        for (ChatBubble chatBubble: chatManager.playerChatBubbles.values()) {
            chatBubble.remove();
        }
        chatManager.playerChatBubbles.clear();
    }
}
