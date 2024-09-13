package com.example.scraper.mapper;



import org.jsoup.nodes.Element;

public class HtmlUtil {

    // Extracts text content from an HTML element
    public static String extractText(Element element) {
        if (element != null) {
            return element.text();
        }
        return "";
    }

    // Extracts attribute value from an HTML element
    public static String extractAttribute(Element element, String attributeName) {
        if (element != null && attributeName != null && !attributeName.isEmpty()) {
            return element.attr(attributeName);
        }
        return "";
    }
}
