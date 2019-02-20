/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.iso;

import static be.naturalsciences.bmdc.metadata.iso.ISO19115DatasetPrinter.MD_NAMESPACES;
import be.naturalsciences.bmdc.utils.LocalizedString;
import be.naturalsciences.bmdc.utils.xml.XMLElement;
import be.naturalsciences.bmdc.utils.xml.XMLUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A class to translate parts (regex matches) of an stringBuilder String
 *
 * @author thomas
 */
public class ISO19115StringTranslator {

    private StringBuilder stringBuilder;

    private final Set<String> translatedLanguages = new HashSet<>();

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    void setStringBuilder(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    public Set<String> getTranslatedLanguages() {
        return translatedLanguages;
    }

    /**
     * *
     * Translate each occurence of patternToTranslate with the provided set of
     * languages and translations.
     *
     * @param XMLText The XML String that needs some elements translated
     * @param patternToTranslate A pattern completely identifying an element to
     * be translated be translated (no wildcards and with ONE capturing group)
     * @param translations A set of translations for each language
     * @return
     */
    public void processTranslations(Map<String, Set<LocalizedString>> translationString) {
        if (translationString != null) {
            for (Map.Entry<String, Set<LocalizedString>> entry : translationString.entrySet()) {
                /*String searchFor = entry.getKey();
                if (searchFor.startsWith("(") && searchFor.endsWith(")")) {
                    searchFor = searchFor.substring(1).substring(0, searchFor.length() - 1);
                }
                searchFor = "(" + Pattern.quote(searchFor) + ")";

                Set<LocalizedString> value = entry.getValue();
                processTranslations(Pattern.compile(searchFor), value);*/
                String searchFor = entry.getKey();
                Set<LocalizedString> translations = entry.getValue();
                XMLElement element = new XMLElement("gco:CharacterString", searchFor, null, null);
                processTranslations2(element, translations);
                element = new XMLElement("gmx:Anchor", searchFor, null, null);
                processTranslations2(element, translations);
            }
        }
    }

    /**
     * *
     * Translate each occurence of patternToTranslate with the provided set of
     * languages and translations.
     *
     * @param toBeTranslated The stringBuilder String that needs some elements
     * translated
     * @param patternToTranslate A pattern completely identifying an element to
     * be translated be translated (no wildcards and with ONE capturing group)
     * @param translations A set of translations for each language
     * @return The stringBuilder string with all strings translated.
     */
    /*private void processTranslations(Pattern patternToTranslate, Set<LocalizedString> translations) {
        if (translations != null) {
            if (patternToTranslate != null && translations != null && !translations.isEmpty()) {
                Map<String, String> translationsMap = new HashMap<>();
                translationsMap = translations.stream().filter(t -> t.getUnderlyingString() != null && t.getLanguageString() != null).collect(Collectors.toMap(t -> t.getLanguageString().toUpperCase(), t -> t.getUnderlyingString()));
                processTranslations(patternToTranslate, translationsMap);
            }
        }
    }*/
    /**
     * *
     *
     * @param toBeTranslated The stringBuilder String that needs some elements
     * translated
     * @param patternToTranslate A pattern completely identifying an element to
     * be translated
     * @param translations A Map of String, String elements containing a set
     * language, translations searchFor-value pairs
     * @return The stringBuilder string with all strings translated.
     */
    /*private void processTranslations(Pattern patternToTranslate, Map<String, String> translations) {
        if (translations != null && !translations.isEmpty()) {
            StringBuilder freeTextBuilder = new StringBuilder("<gmd:PT_FreeText>");

            for (String language : translations.keySet()) {
                language = language.toUpperCase();
                String translation = translations.get(language);
                if (translation != null) {
                    translatedLanguages.add(language);
                    freeTextBuilder.append("<gmd:textGroup><gmd:LocalisedCharacterString locale=\"#").append(language).append("\">").append(translation).append("</gmd:LocalisedCharacterString></gmd:textGroup>");
                }
            }
            freeTextBuilder.append("</gmd:PT_FreeText>");

//            XMLUtils.appendAttribute(stringBuilder, "//gco:CharacterString[text()='Pierre-Yves Declercq']", "xsi:type", "gmd:PT_FreeText_PropertyType", ISO19115DatasetPrinter.MD_NAMESPACES);
            stringBuilder = StringUtils.stringBuilderReplaceAll(stringBuilder, patternToTranslate, "$1" + freeTextBuilder.toString());
            //stringBuilder = StringUtils.stringBuilderReplaceAll(stringBuilder, "<(.*?)>\\n*[\\t| ]*" + patternToTranslate, "<$1 xsi:type=\"gmd:PT_FreeText_PropertyType\">$2");
            Pattern from = Pattern.compile("<(.*?)><.*?>" + patternToTranslate);
            stringBuilder = StringUtils.stringBuilderReplaceAll(stringBuilder, from, "<$1 xsi:type=\"gmd:PT_FreeText_PropertyType\"><gco:CharacterString>$2$3");
            //  stringBuilder = StringUtils.stringBuilderReplaceAll(stringBuilder, "<(.*?)>(<gmx:Anchor.*?>)" + patternToTranslate, "<$1 xsi:type=\"gmd:PT_FreeText_PropertyType\">$2$3");
            System.out.println(stringBuilder);
        }
    }*/
    /**
     * *
     * Translate each occurence of patternToTranslate with the provided set of
     * languages and translations.
     *
     * @param XMLText The XML String that needs some elements translated
     * @param patternToTranslate A pattern completely identifying an element to
     * be translated be translated (no wildcards and with ONE capturing group)
     * @param translations A set of translations for each language
     * @return
     */
    public void processTranslations2(Map<XMLElement, Set<LocalizedString>> translationMap) {
        if (translationMap != null) {
            for (Map.Entry<XMLElement, Set<LocalizedString>> entry : translationMap.entrySet()) {
                XMLElement searchFor = entry.getKey();
                Set<LocalizedString> translations = entry.getValue();
                processTranslations2(searchFor, translations);
            }
        }
    }

    public void processTranslations2(XMLElement element, Set<LocalizedString> translations) {
        if (translations != null) {
            if (element != null && translations != null && !translations.isEmpty()) {
                Map<String, String> translationsMap = new HashMap<>();
                translationsMap = translations.stream().filter(t -> t.getUnderlyingString() != null && t.getLanguageString() != null).collect(Collectors.toMap(t -> t.getLanguageString().toUpperCase(), t -> t.getUnderlyingString()));
                processTranslations2(element, translationsMap);
            }
        }
    }

    private void processTranslations2(XMLElement element, Map<String, String> translations) {
        if (translations != null && !translations.isEmpty()) {
            StringBuilder freeTextBuilder = new StringBuilder("<gmd:PT_FreeText xmlns:gmd=\"http://www.isotc211.org/2005/gmd\">"); //the namespace is needed!

            for (String language : translations.keySet()) {
                language = language.toUpperCase();
                String translation = translations.get(language);
                if (translation != null) {
                    translatedLanguages.add(language);
                    freeTextBuilder.append("<gmd:textGroup><gmd:LocalisedCharacterString locale=\"#").append(language).append("\">").append(translation).append("</gmd:LocalisedCharacterString></gmd:textGroup>");
                }
            }
            freeTextBuilder.append("</gmd:PT_FreeText>");

            XMLUtils.pasteAfter(stringBuilder, element.toXPath(), freeTextBuilder.toString(), ISO19115DatasetPrinter.MD_NAMESPACES);
            XMLUtils.appendAttribute(stringBuilder, element.toXPath() + "/parent::*", "http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "gmd:PT_FreeText_PropertyType", ISO19115DatasetPrinter.MD_NAMESPACES);

            // stringBuilder = StringUtils.stringBuilderReplaceAll(stringBuilder, patternToTranslate, "$1" + freeTextBuilder.toString());
            //stringBuilder = StringUtils.stringBuilderReplaceAll(stringBuilder, "<(.*?)>\\n*[\\t| ]*" + patternToTranslate, "<$1 xsi:type=\"gmd:PT_FreeText_PropertyType\">$2");
            //  Pattern from = Pattern.compile("<(.*?)><.*?>" + patternToTranslate);
            // stringBuilder = StringUtils.stringBuilderReplaceAll(stringBuilder, from, "<$1 xsi:type=\"gmd:PT_FreeText_PropertyType\"><gco:CharacterString>$2$3");
            //  stringBuilder = StringUtils.stringBuilderReplaceAll(stringBuilder, "<(.*?)>(<gmx:Anchor.*?>)" + patternToTranslate, "<$1 xsi:type=\"gmd:PT_FreeText_PropertyType\">$2$3");
            // System.out.println(stringBuilder);
        }
    }
}
