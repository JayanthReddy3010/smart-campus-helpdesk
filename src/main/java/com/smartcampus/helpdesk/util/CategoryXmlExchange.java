package com.smartcampus.helpdesk.util;

import com.smartcampus.helpdesk.model.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class CategoryXmlExchange {

    public static final String NAMESPACE = "https://smartcampus.example/xml/categories";
    private static final String SCHEMA_RESOURCE = "/xml/categories.xsd";

    private CategoryXmlExchange() {
    }

    public static byte[] exportCategories(List<Category> categories) throws XmlExchangeException {
        try {
            Document document = newDocumentBuilder(false).newDocument();
            Element root = document.createElementNS(NAMESPACE, "categories");
            root.setAttribute("version", "1");
            document.appendChild(root);

            for (Category category : categories) {
                Element categoryElement = document.createElementNS(NAMESPACE, "category");
                appendText(document, categoryElement, "name", category.getCategoryName());
                appendText(document, categoryElement, "description", category.getDescription());
                appendText(document, categoryElement, "active", Boolean.toString(category.isActive()));
                root.appendChild(categoryElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(document), new StreamResult(output));
            return output.toByteArray();
        } catch (Exception exception) {
            throw new XmlExchangeException("Unable to generate category XML.", exception);
        }
    }

    public static List<Category> importCategories(InputStream input) throws XmlExchangeException {
        try {
            DocumentBuilder builder = newDocumentBuilder(true);
            Document document = builder.parse(input);
            Element root = document.getDocumentElement();
            if (!NAMESPACE.equals(root.getNamespaceURI()) || !"categories".equals(root.getLocalName())) {
                throw new XmlExchangeException("The XML root must be a categories document.");
            }

            List<Category> categories = new ArrayList<>();
            NodeList nodes = root.getChildNodes();
            for (int index = 0; index < nodes.getLength(); index++) {
                Node node = nodes.item(index);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element categoryElement = (Element) node;
                if (!NAMESPACE.equals(categoryElement.getNamespaceURI())
                        || !"category".equals(categoryElement.getLocalName())) {
                    throw new XmlExchangeException("Unexpected element in categories document.");
                }
                String name = childText(categoryElement, "name");
                String description = childText(categoryElement, "description");
                boolean active = Boolean.parseBoolean(childText(categoryElement, "active"));
                categories.add(new Category(null, name, description, active));
            }
            return categories;
        } catch (XmlExchangeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new XmlExchangeException("The category XML could not be read or validated.", exception);
        }
    }

    private static DocumentBuilder newDocumentBuilder(boolean validating) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        if (validating) {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            try (InputStream schemaInput = CategoryXmlExchange.class.getResourceAsStream(SCHEMA_RESOURCE)) {
                if (schemaInput == null) {
                    throw new IOException("Category XML schema is missing.");
                }
                Schema schema = schemaFactory.newSchema(new javax.xml.transform.stream.StreamSource(schemaInput));
                factory.setSchema(schema);
            }
        }
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
        return builder;
    }

    private static void appendText(Document document, Element parent, String name, String value) {
        Element child = document.createElementNS(NAMESPACE, name);
        child.setTextContent(value == null ? "" : value);
        parent.appendChild(child);
    }

    private static String childText(Element parent, String name) throws XmlExchangeException {
        NodeList nodes = parent.getElementsByTagNameNS(NAMESPACE, name);
        if (nodes.getLength() != 1) {
            throw new XmlExchangeException("Each category must contain one " + name + " element.");
        }
        return nodes.item(0).getTextContent().trim();
    }
}
