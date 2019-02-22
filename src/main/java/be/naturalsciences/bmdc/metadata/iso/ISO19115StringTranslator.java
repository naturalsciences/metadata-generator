/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.iso;

import be.naturalsciences.bmdc.utils.LocalizedString;
import be.naturalsciences.bmdc.utils.xml.XMLElement;
import be.naturalsciences.bmdc.utils.xml.XMLUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A class to translate parts (regex matches) of an stringBuilder String
 *
 * @author thomas
 */
public class ISO19115StringTranslator {

    //private StringBuilder stringBuilder;

    /*public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    void setStringBuilder(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }*/
    private Document document;

    private Set<String> translatedLanguages;

    public ISO19115StringTranslator() {

    }

    public Document getDocument() {
        return document;
    }

    public final void setDocument(Document document) {
        this.document = document;
        translatedLanguages = new HashSet<>(); //start the translated languages anew
    }

    public Set<String> getTranslatedLanguages() {
        return translatedLanguages;
    }

    public StringBuilder translate(StringBuilder sb, Map<String, Set<LocalizedString>> translations) {
        try {
            setDocument(XMLUtils.toDocument(sb.toString()));

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
        translate(translations);

        StringBuilder stringBuilder = new StringBuilder(XMLUtils.toXML(getDocument()));
        return stringBuilder;
    }

    public StringBuilder translate(StringBuilder sb, XMLElement element, Set<LocalizedString> translations) {
        try {
            setDocument(XMLUtils.toDocument(sb.toString()));

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
        translate(element, translations);
        return new StringBuilder(XMLUtils.toXML(getDocument()));
    }

    /**
     * *
     * Translate each occurrence of map key with the map value which. The map
     * value is a set of languages and translations.
     *
     * @param XMLText The XML String (whole string) that needs some elements
     * translated
     * @param patternToTranslate A pattern completely identifying an element to
     * be translated be translated (no wildcards and with ONE capturing group)
     * @param translations A set of translations for each language
     * @return
     */
    private void translate(Map<String, Set<LocalizedString>> translationString) {
        if (translationString != null) {
            for (Map.Entry<String, Set<LocalizedString>> entry : translationString.entrySet()) {
                String searchFor = entry.getKey();
                Set<LocalizedString> translations = entry.getValue();
                XMLElement element = new XMLElement("gco:CharacterString", searchFor, null, null);
                translate(element, translations);
                element = new XMLElement("gmx:Anchor", searchFor, null, null);
                translate(element, translations);
            }
        }
    }

    private void translate(XMLElement element, Set<LocalizedString> translations) {
        if (element != null && translations != null && !translations.isEmpty()) {
            // Map<String, String> translationsMap = new HashMap<>();
            // translationsMap = translations.stream().filter(t -> t.getUnderlyingString() != null && t.getLanguageString() != null).collect(Collectors.toMap(t -> t.getLanguageString().toUpperCase(), t -> t.getUnderlyingString()));
            try {
                StringBuilder freeTextBuilder = new StringBuilder("<gmd:PT_FreeText xmlns:gmd=\"http://www.isotc211.org/2005/gmd\">"); //the namespace is needed!
                for (LocalizedString localizedString : translations) {
                    String language = localizedString.getLanguageString().toUpperCase();
                    String translation = localizedString.getUnderlyingString();
                    if (translation != null) {
                        translatedLanguages.add(language);
                        freeTextBuilder.append("<gmd:textGroup><gmd:LocalisedCharacterString locale=\"#").append(language).append("\">").append(translation).append("</gmd:LocalisedCharacterString></gmd:textGroup>");
                    }
                }
                /*for (String language : translations.keySet()) {
                    language = language.toUpperCase();
                    String translation = translations.get(language);
                    if (translation != null) {
                        translatedLanguages.add(language);
                        freeTextBuilder.append("<gmd:textGroup><gmd:LocalisedCharacterString locale=\"#").append(language).append("\">").append(translation).append("</gmd:LocalisedCharacterString></gmd:textGroup>");
                    }
                }*/
                freeTextBuilder.append("</gmd:PT_FreeText>");

                XMLUtils.pasteAfter(document, element.toXPath(), freeTextBuilder.toString(), ISO19115DatasetPrinter.MD_NAMESPACES);

                // <gmd:country xmlns:ns0="xsi:type" ns0:xsi="gmd:PT_FreeText_PropertyType">
                XMLUtils.appendAttribute(document, element.toXPath() + "/parent::*", "xsi", "type", "gmd:PT_FreeText_PropertyType", ISO19115DatasetPrinter.MD_NAMESPACES);
            } catch (Exception ex) {
                Logger.getLogger(ISO19115StringTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /*private void translate(XMLElement element, Map<String, String> translations) {
        if (translations != null && !translations.isEmpty()) {

        }
    }*/
}
