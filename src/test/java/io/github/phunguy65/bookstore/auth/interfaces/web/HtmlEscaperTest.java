package io.github.phunguy65.bookstore.auth.interfaces.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlEscaperTest {
    @Test
    void returnsEmptyStringForNull() {
        assertEquals("", HtmlEscaper.escape(null));
    }

    @Test
    void escapesSpecialHtmlCharacters() {
        assertEquals("&lt;tag attr=&quot;x&quot;&gt;&amp;&#39;", HtmlEscaper.escape("<tag attr=\"x\">&'"));
    }
}
