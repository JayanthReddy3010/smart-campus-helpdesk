package com.smartcampus.helpdesk.util;

import com.smartcampus.helpdesk.model.Category;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryXmlExchangeTest {

    @Test
    void exportsAndImportsCategoryConfiguration() throws XmlExchangeException {
        List<Category> source = List.of(
                new Category(null, "IT Support", "Computer support", true),
                new Category(null, "Facilities", "Campus maintenance", false));

        byte[] xml = CategoryXmlExchange.exportCategories(source);
        List<Category> imported = CategoryXmlExchange.importCategories(new ByteArrayInputStream(xml));

        assertEquals(2, imported.size());
        assertEquals("IT Support", imported.get(0).getCategoryName());
        assertEquals("Campus maintenance", imported.get(1).getDescription());
        assertEquals(false, imported.get(1).isActive());
    }

    @Test
    void rejectsXmlThatDoesNotMatchSchema() {
        String invalidXml = "<categories xmlns=\"https://smartcampus.example/xml/categories\" version=\"1\">"
                + "<category><name>Missing active field</name><description>Invalid</description></category>"
                + "</categories>";

        assertThrows(XmlExchangeException.class, () -> CategoryXmlExchange.importCategories(
                new ByteArrayInputStream(invalidXml.getBytes(StandardCharsets.UTF_8))));
    }
}
