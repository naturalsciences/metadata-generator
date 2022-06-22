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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A class to translate parts (regex matches) of an stringBuilder String
 *
 * @author thomas
 */
public class ISO19115StringTranslator {

    private static final Map<String, String> LOCALE_MAP;

    static {
        LOCALE_MAP = new HashMap<>();
        LOCALE_MAP.put("FR", "<gmd:locale  xmlns:gmd=\"http://www.isotc211.org/2005/gmd\">\n"
                + "    <gmd:PT_Locale id=\"FR\">\n"
                + "      <gmd:languageCode>\n"
                + "        <gmd:LanguageCode codeList=\"http://www.loc.gov/standards/iso639-2/\" codeListValue=\"fre\" />\n"
                + "      </gmd:languageCode>\n"
                + "      <gmd:characterEncoding />\n"
                + "    </gmd:PT_Locale>\n"
                + "  </gmd:locale>");
        LOCALE_MAP.put("NL", "<gmd:locale xmlns:gmd=\"http://www.isotc211.org/2005/gmd\">\n"
                + "    <gmd:PT_Locale id=\"NL\">\n"
                + "      <gmd:languageCode>\n"
                + "        <gmd:LanguageCode codeList=\"http://www.loc.gov/standards/iso639-2/\" codeListValue=\"dut\" />\n"
                + "      </gmd:languageCode>\n"
                + "      <gmd:characterEncoding />\n"
                + "    </gmd:PT_Locale>\n"
                + "  </gmd:locale>");
        LOCALE_MAP.put("DE", "<gmd:locale xmlns:gmd=\"http://www.isotc211.org/2005/gmd\">\n"
                + "    <gmd:PT_Locale id=\"DE\">\n"
                + "      <gmd:languageCode>\n"
                + "        <gmd:LanguageCode codeList=\"http://www.loc.gov/standards/iso639-2/\" codeListValue=\"ger\" />\n"
                + "      </gmd:languageCode>\n"
                + "      <gmd:characterEncoding />\n"
                + "    </gmd:PT_Locale>\n"
                + "  </gmd:locale>");
        LOCALE_MAP.put("EN", "<gmd:locale xmlns:gmd=\"http://www.isotc211.org/2005/gmd\">\n"
                + "    <gmd:PT_Locale id=\"EN\">\n"
                + "      <gmd:languageCode>\n"
                + "        <gmd:LanguageCode codeList=\"http://www.loc.gov/standards/iso639-2/\" codeListValue=\"eng\" />\n"
                + "      </gmd:languageCode>\n"
                + "      <gmd:characterEncoding />\n"
                + "    </gmd:PT_Locale>\n"
                + "  </gmd:locale>");
    }

    private Document document;

    private Set<String> translatedLanguages;

    public Document getDocument() {
        return document;
    }

    public final void setDocument(Document document) {
        this.document = document;
        translatedLanguages = new LinkedHashSet<>(); //start the translated languages anew, and maintain insertion order
    }

    public final void setDocument(String document) {
        try {
            setDocument(XMLUtils.toDocument(document));
        } catch (JAXBException ex) {
            Logger.getLogger(ISO19115StringTranslator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ISO19115StringTranslator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ISO19115StringTranslator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ISO19115StringTranslator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Set<String> getTranslatedLanguages() {
        return translatedLanguages;
    }

    public void finalizeTranslations() {
        if (!getTranslatedLanguages().isEmpty()) {
            for (String translatedLanguage : getTranslatedLanguages()) {
                XMLElement element = new XMLElement("gmd:metadataStandardVersion", null, null, null);
                try {
                    XMLUtils.pasteAfter(document, element.toXPath(), LOCALE_MAP.get(translatedLanguage), ISO19115DatasetPrinter.MD_NAMESPACES);
                } catch (Exception ex) {
                    Logger.getLogger(ISO19115StringTranslator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * *
     * Translate each occurrence of map key with the map value which. The map
     * value is a set of languages and translations. It is recommended that the
     * Set maintains insertion order!
     *
     * @param XMLText The XML String (whole string) that needs some elements
     * translated
     * @param patternToTranslate A pattern completely identifying an element to
     * be translated be translated (no wildcards and with ONE capturing group)
     * @param translations A set of translations for each language
     * @return
     */
    public void translate(Map<String, Set<LocalizedString>> translationString) {
        if (translationString != null && !translationString.isEmpty()) {
            for (Map.Entry<String, Set<LocalizedString>> entry : translationString.entrySet()) {
                String searchFor = entry.getKey();
                if (searchFor != null) {
                    Set<LocalizedString> translations = entry.getValue();
                    XMLElement element = new XMLElement("gco:CharacterString", searchFor, null, null);
                    translate(element, translations);
                    element = new XMLElement("gmx:Anchor", searchFor, null, null);
                    translate(element, translations);
                } else {
                    //if we have an empty element that has translations, this would mean every single string needs to be translated.
                }
            }
        }
    }

    public void translate(XMLElement element, Set<LocalizedString> translations) {
        if (element != null && translations != null && !translations.isEmpty()) {//were any elements or translations provided?
            try {
                List<Node> nodes = XMLUtils.xpathQueryNodes(document, element.toXPath(), ISO19115DatasetPrinter.MD_NAMESPACES); //is the element even present in the document??
                if (nodes != null && !nodes.isEmpty()) {
                    StringBuilder freeTextBuilder = new StringBuilder("<gmd:PT_FreeText xmlns:gmd=\"http://www.isotc211.org/2005/gmd\">"); //the namespace is needed!
                    for (LocalizedString localizedString : translations) {
                        String language = localizedString.getLanguageString().toUpperCase();
                        String translation = XMLUtils.replaceXMLTextNode(localizedString.getUnderlyingString());
                        if (translation != null) {
                            translatedLanguages.add(language);
                            freeTextBuilder.append("<gmd:textGroup><gmd:LocalisedCharacterString locale=\"#").append(language).append("\">").append(translation).append("</gmd:LocalisedCharacterString></gmd:textGroup>");
                        }
                    }
                    freeTextBuilder.append("</gmd:PT_FreeText>");
                    XMLUtils.pasteAfter(document, nodes, freeTextBuilder.toString(), ISO19115DatasetPrinter.MD_NAMESPACES);
                    XMLUtils.appendAttribute(document, element.toXPath() + "/parent::*", "xsi", "type", "gmd:PT_FreeText_PropertyType", ISO19115DatasetPrinter.MD_NAMESPACES);
                }
            } catch (Exception ex) {
                Logger.getLogger(ISO19115StringTranslator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
