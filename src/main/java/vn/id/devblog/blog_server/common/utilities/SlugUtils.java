package vn.id.devblog.blog_server.common.utilities;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Normalize
        String noWhiteSpace = WHITESPACE.matcher(input).replaceAll("-");

        // Convert to english character by NFD
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");

        //Other case
        return slug.toLowerCase(Locale.ENGLISH)
                .replaceAll("đ", "d")
                .replaceAll("-{2,}", "-")  // Remove "--"
                .replaceAll("^-", "")      // Remove - at first
                .replaceAll("-$", "");     // Remove - at last
    }
}