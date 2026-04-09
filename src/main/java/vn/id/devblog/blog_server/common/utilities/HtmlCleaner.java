package vn.id.devblog.blog_server.common.utilities;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlCleaner {
    public static String cleanHtml(String html) {
        return Jsoup.clean(html, Safelist.relaxed());
    }
}
