package com.smartcampus.helpdesk.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlUtilTest {

    @Test
    void escapesSpecialHtmlCharacters() {
        assertEquals("&lt;script&gt;alert(&#39;XSS&#39;)&amp;&quot;test&quot;&lt;/script&gt;",
                HtmlUtil.escape("<script>alert('XSS')&\"test\"</script>"));
    }

    @Test
    void handlesNullAndEmptyValuesSafely() {
        assertEquals("", HtmlUtil.escape(null));
        assertEquals("", HtmlUtil.escape(""));
        assertEquals("Clean text", HtmlUtil.escape("Clean text"));
    }
}
