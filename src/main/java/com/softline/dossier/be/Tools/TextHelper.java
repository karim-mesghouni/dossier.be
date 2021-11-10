package com.softline.dossier.be.Tools;

import org.apache.logging.log4j.message.ParameterizedMessage;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextHelper {
    private TextHelper() {
    }

    public static String search(@Language("RegExp") String pattern, String haystack) {
        Matcher m = Pattern.compile(pattern).matcher(haystack);
        if (m.find()) {
            return m.toMatchResult().group();
        }
        return "";
    }

    /**
     * replace any matched pattern in the subject with the return value of the callback
     *
     * @param pattern  the pattern finder
     * @param subject  the string to apply the pattern on
     * @param callback a function which will be supplied with each match result of the pattern in the subject, the match result will be replaced by the return value of this function
     */
    @NotNull
    public static String replace(Pattern pattern, Function<String, String> callback, CharSequence subject) {
        Matcher m = pattern.matcher(subject);
        StringBuilder newSubject = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(newSubject, callback.apply(m.toMatchResult().group()));
        }
        m.appendTail(newSubject);
        return newSubject.toString();
    }

    /**
     * Format the given text with the given arguments,
     * argument format in text is {} like in Slf4j logger
     */
    public static String format(String format, Object... args) {
        var sb = new StringBuilder();
        new ParameterizedMessage(format, args).formatTo(sb);
        return sb.toString();
    }
}
