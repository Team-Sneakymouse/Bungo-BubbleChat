package ca.bungo.bubblechat.types;

import ca.bungo.bubblechat.BubbleChat;
import ca.bungo.bubblechat.utility.ChatUtility;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatBubble {

    private final Player player;
    private final List<String> messages;
    private TextDisplay display = null;

    private String hexTextColor = "#000000";

    public ChatBubble(Player player, String message) {
        this.player = player;
        messages = new ArrayList<>(Collections.singletonList(message));
    }

    public void spawn() {

        Location location = this.player.getLocation().clone();
        location.setY(location.getY() + this.player.getEyeHeight() * 0.85);

        this.display = this.player.getWorld().spawn(location, TextDisplay.class);

        this.display.addScoreboardTag("ChatBubble");

        updateSettings();

        this.display.text(this.makeMessage());

        this.display.setBillboard(Display.Billboard.CENTER);
        this.display.setLineWidth(150);
        this.display.setSeeThrough(false);
        this.display.setDefaultBackground(false);
        this.display.setShadowed(false);
        this.display.setBrightness(new Display.Brightness(15, 15));

        this.display.setInterpolationDuration(0);
        this.display.setInterpolationDelay(-1);
        this.display.setTransformation(new Transformation(new Vector3f(0F,0.85F,0F), new AxisAngle4f(), new Vector3f(1), new AxisAngle4f()));

        this.player.addPassenger(this.display);
    }

    public void remove() {
        if (this.display != null && this.display.isValid()) {
            this.display.remove();
        }
    }

    public int addMessage(String message) {
        updateSettings();
        int messageId = this.messages.size();

        this.messages.add(message);
        this.display.text(this.makeMessage());

        return messageId;
    }

    public boolean removeMessage(int id) {
        for (int i = id - 1; i > -1; i--) {
            if (this.messages.get(i) != null) {
                this.messages.set(i, this.messages.get(i) + "\n" + this.messages.get(id));
                break;
            }
        }

        this.messages.set(id, null);

        Component message = this.makeMessage();

        if (message != null) {
            this.display.text(message);
            return false;
        } else {
            return true;
        }
    }

    private Component makeMessage() {
        String message = null;

        for (int i = 0; i < this.messages.size(); i ++) {
            if (this.messages.get(i) != null) {
                if (message == null) {
                    message = "&" + hexTextColor  + this.messages.get(i);
                } else {
                    message += "\n&" + hexTextColor + this.messages.get(i);
                }
            }
        }

        return ChatUtility.formatMessage(message);
    }

    private void updateSettings(){

        BubbleChat.instance.reloadConfig();
        FileConfiguration configuration = BubbleChat.instance.getConfig();
        int bR = configuration.getInt("display-settings.background.r", 255);
        int bG = configuration.getInt("display-settings.background.g", 255);
        int bB = configuration.getInt("display-settings.background.b", 255);
        hexTextColor = configuration.getString("display-settings.text-color.hex", "#000000");
        this.display.setBackgroundColor(Color.fromRGB(bR, bG, bB));


    }

}
