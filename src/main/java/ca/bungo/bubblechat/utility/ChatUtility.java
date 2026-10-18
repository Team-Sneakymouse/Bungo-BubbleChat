package ca.bungo.bubblechat.utility;

import com.sneakyutils.util.TextUtility;
import net.kyori.adventure.text.Component;

public final class ChatUtility {

    public static Component formatMessage(String message) {
        if (message == null || message.isEmpty())
            return null;

        return TextUtility.INSTANCE.convertToComponent(message);
    }

    public String koboldifyer(String message) {
        return message
                .replaceAll("[\\w']+", "kobold")
                .replaceAll("\\. k", ". K")
                .replaceFirst("^k", "K");
    }

}
