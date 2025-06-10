package net.dxriver.chatremindermod;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class Reminder {
    private static final Map<String, TriggerConfig> idTriggers = new HashMap<>();
    private static final Map<String, TriggerConfig> keywordTriggers = new HashMap<>();
    private static String id = "Mega32K";

    static {
        idTriggers.put("Mega32K", TriggerConfig.builder().build());
    }

    public static void msgCheck(ClientChatReceivedEvent event) {
        String message = event.getMessage().getString();

        idTriggers.forEach((id, config) -> {
            if (TagMatcher.isFirstTagMatch(message, id)) {
                Reminder(config, event);
            }
        });

        keywordTriggers.forEach((keyword, config) -> {
            if (message.contains(keyword)) {
                Reminder(config, event);
            }
        });
    }

    static void Reminder(TriggerConfig config, ClientChatReceivedEvent event) {
        SoundPlayer.playSound(config);
        event.setMessage(event.getMessage().copy().withStyle(
                Style.EMPTY.withColor(hexToRgb(config.getHexColor())).withBold(true)
        ));
    }

    private static int hexToRgb(String hex) {
        try {
            hex = hex.replace("#", "").trim();
            if (hex.length() != 6) {
                throw new IllegalArgumentException();
            }
            return Integer.parseInt(hex, 16);
        } catch (Exception e) {
            sendFeedback(getTranslatedString("invalid_hex"), ChatFormatting.RED);
            return 0xFF0000;
        }
    }

    public static void addKeywordTrigger(String keyword, TriggerConfig config) {
        boolean isUpdate = keywordTriggers.containsKey(keyword);
        keywordTriggers.put(keyword, config);
        String key = isUpdate ? "updated_keyword" : "added_keyword";
        sendFeedback(getTranslatedString(key, keyword), isUpdate ? ChatFormatting.GOLD : ChatFormatting.BLUE);
    }

    public static void addIdTrigger(String id, TriggerConfig config) {
        boolean isUpdate = idTriggers.containsKey(id);
        idTriggers.put(id, config);
        String key = isUpdate ? "updated_id" : "added_id";
        sendFeedback(getTranslatedString(key, id), isUpdate ? ChatFormatting.YELLOW : ChatFormatting.GREEN);
    }

    private static void sendFeedback(String text, ChatFormatting color) {
        Component message = Component.literal("[" + getTranslatedString("mod_name") + "] ")
                .withStyle(ChatFormatting.DARK_AQUA)
                .append(Component.literal(text).withStyle(color));
        Minecraft.getInstance().player.displayClientMessage(message, false);
    }

    public static boolean removeIdTrigger(String id) {
        boolean existed = idTriggers.remove(id) != null;
        if (existed) {
            sendFeedback(getTranslatedString("removed_id", id), ChatFormatting.GOLD);
        } else {
            sendFeedback(getTranslatedString("id_not_found", id), ChatFormatting.RED);
        }
        return existed;
    }

    public static boolean removeKeywordTrigger(String keyword) {
        boolean existed = keywordTriggers.remove(keyword) != null;
        if (existed) {
            sendFeedback(getTranslatedString("removed_keyword", keyword), ChatFormatting.GOLD);
        } else {
            sendFeedback(getTranslatedString("keyword_not_found", keyword), ChatFormatting.RED);
        }
        return existed;
    }

    public static void setTargetId(String target) {
        TriggerConfig config = idTriggers.get(id);
        idTriggers.remove(id);
        idTriggers.put(target, config);
        id = target;
        sendFeedback(getTranslatedString("set_target_id", id), ChatFormatting.GREEN);
    }

    public static void setSound(ResourceLocation soundId) {
        TriggerConfig config = idTriggers.get(id);
        idTriggers.remove(id);
        config.setSound(SoundPlayer.getSoundEventByName(soundId));
        idTriggers.put(id, config);
        sendFeedback(getTranslatedString("set_sound", soundId.toString()), ChatFormatting.AQUA);
    }

    public static void setNoteCount(int count) {
        TriggerConfig config = idTriggers.get(id);
        idTriggers.remove(id);
        config.setNoteCount(count);
        idTriggers.put(id, config);
        sendFeedback(getTranslatedString("set_note_count", String.valueOf(count)), ChatFormatting.GOLD);
    }

    public static void setPitchRange(float min, float max) {
        TriggerConfig config = idTriggers.get(id);
        idTriggers.remove(id);
        config.setPitchRange(min, max);
        idTriggers.put(id, config);
        sendFeedback(getTranslatedString("set_pitch_range", String.format("%.1f", min), String.format("%.1f", max)), ChatFormatting.LIGHT_PURPLE);
    }

    public static void setHexColor(String hex) {
        TriggerConfig config = idTriggers.get(id);
        idTriggers.remove(id);
        config.setColor(hex);
        idTriggers.put(id, config);
        sendFeedback(getTranslatedString("set_hex_color", hex), ChatFormatting.GREEN);
    }

    private static String getTranslatedString(String key, String... args) {
        return I18n.get("chat_reminder." + key, (Object[]) args);
    }
}