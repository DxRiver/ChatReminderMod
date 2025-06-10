package net.dxriver.chatremindermod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@OnlyIn(Dist.CLIENT)
public class TagMatcher {

    /**
     * 检查id
     * @param text   待检测文本
     * @param target 目标文本
     * @return true-匹配成功，false-匹配失败
     */
    public static boolean isFirstTagMatch(String text, String target) {

        if (text == null || !text.startsWith("<")) {
            return false;
        }

        Pattern pattern = Pattern.compile("^<([^>]*)>");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String tagContent = matcher.group(1);
            return tagContent.equals(target);
        }
        return false;
    }


}