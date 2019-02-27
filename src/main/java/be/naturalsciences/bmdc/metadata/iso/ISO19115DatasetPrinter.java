/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.iso;

import static be.naturalsciences.bmdc.metadata.iso.ISO19115DatasetBuilder.LANGUAGES;
import be.naturalsciences.bmdc.utils.xml.XMLElement;
import be.naturalsciences.bmdc.metadata.model.IDataset;
import be.naturalsciences.bmdc.utils.CollectionUtils;
import be.naturalsciences.bmdc.utils.FileUtils;
import be.naturalsciences.bmdc.utils.LocalizedString;
import be.naturalsciences.bmdc.utils.StringUtils;
import be.naturalsciences.bmdc.utils.xml.XMLUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.xml.XML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A class responsible to create a complete ISO 19115:2003/19139 Metadata String
 * or File representation of a DefaultMetadata metadata document.
 *
 * @author thomas
 */
public class ISO19115DatasetPrinter implements Serializable {

    private IDataset dataset;
    private StringBuilder datasetText;
    private Set<String> keywordLanguages;
    private ISO19115StringTranslator translator;
    private Map<String, Set<LocalizedString>> extraTranslations;

    private static Document GEMET_DOCUMENT;
    
    private static final int CONNECTION_TIME_OUT = 1000;
    private static final int READ_TIME_OUT = 1000;

    private static final File INSPIRE_RDF_DIR;

    private static Map<String, Document> INSPIRE_VOCABULARY = new HashMap();

    public static final Map<String, String> CSW_NAMESPACES = new HashMap<>();
    public static final Map<String, String> GML_NAMESPACES = new HashMap<>();
    public static final Map<String, String> MD_NAMESPACES = new HashMap<>();
    public static final Map<String, String> RDF_NAMESPACES = new HashMap<>();

    private static Map<String, Document> GEMET_FILTERED_RESULTS = new HashMap();
    private static Map<String, Set<LocalizedString>> ANCHOR_TRANSLATIONS_MAP = new LinkedHashMap<>(); //maintain insertion order!

    static {
        String tmp = System.getProperty("java.io.tmpdir");
        INSPIRE_RDF_DIR = new File(tmp, "inspire-rdf");

        try {
            FileUtils.copyURLToFile(new URL("https://www.eionet.europa.eu/gemet/latest/gemet.rdf.gz"), new File(INSPIRE_RDF_DIR, "gemet.rdf.gz"), CONNECTION_TIME_OUT, READ_TIME_OUT);
            FileUtils.decompressGzipFile(new File(INSPIRE_RDF_DIR, "gemet.rdf.gz"), INSPIRE_RDF_DIR);
            GEMET_DOCUMENT = XMLUtils.toDocument(new File(INSPIRE_RDF_DIR, "gemet.rdf"));
        } catch (Exception ex) {
            Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.SEVERE, "An exception occured.", ex);
        }

        CSW_NAMESPACES.put("csw", "http://www.opengis.net/cat/csw/2.0.2");
        CSW_NAMESPACES.put("dc", "http://purl.org/dc/elements/1.1/");

        GML_NAMESPACES.put("xlink", "http://www.w3.org/1999/xlink");
        GML_NAMESPACES.put("gmd", "http://www.isotc211.org/2005/gmd");
        GML_NAMESPACES.put("gml", "http://www.opengis.net/gml");
        GML_NAMESPACES.put("srv", "http://www.isotc211.org/2005/srv");

        MD_NAMESPACES.put("gmd", "http://www.isotc211.org/2005/gmd");
        MD_NAMESPACES.put("gco", "http://www.isotc211.org/2005/gco");
        MD_NAMESPACES.put("xlink", "http://www.w3.org/1999/xlink");
        MD_NAMESPACES.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        MD_NAMESPACES.put("gml", "http://www.opengis.net/gml/3.2");
        MD_NAMESPACES.put("geonet", "http://www.fao.org/geonetwork");
        MD_NAMESPACES.put("gmx", "http://www.isotc211.org/2005/gmx");

        RDF_NAMESPACES.put("dc", "http://purl.org/dc/elements/1.1/");
        RDF_NAMESPACES.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        RDF_NAMESPACES.put("dct", "http://purl.org/dc/terms/");
        RDF_NAMESPACES.put("dcterms", "http://purl.org/dc/terms/");
        RDF_NAMESPACES.put("skos", "http://www.w3.org/2004/02/skos/core#");

    }

    public String getResult() {
        return this.datasetText.toString();
    }

    public ISO19115DatasetPrinter(ISO19115DatasetBuilder builder, Set<String> keywordLanguages, Map<String, Set<LocalizedString>> extraTranslations) throws JAXBException {
        this(builder.getDataset(), builder.getMetadataDocument(), keywordLanguages, extraTranslations);
    }

    /**
     * @param dataset
     * @param metadata
     * @param keywordLanguages The languages that terms (keywords etc.) need
     * translation to. Provide languages in upper case (EN, DE, FR, NL, ES,IT,
     * etc.). If provided as null, will resort to the default ((EN, DE, FR, NL).
     * @throws JAXBException
     */
    private ISO19115DatasetPrinter(IDataset dataset, DefaultMetadata metadata, Set<String> keywordLanguages, Map<String, Set<LocalizedString>> extraTranslations) throws JAXBException {
        if (dataset == null) {
            throw new IllegalArgumentException("Provided dataset argument is null.");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("Provided metadata argument is null.");
        }
        this.keywordLanguages = keywordLanguages;
        if (!Files.exists(INSPIRE_RDF_DIR.toPath())) {
            try {
                Files.createDirectories(INSPIRE_RDF_DIR.toPath());
            } catch (IOException e) {
                Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.SEVERE, "Couldn't create directory.", e);
            }
        }
        URL themeRdfUrl = null;

        for (String keywordLanguage : getKeywordLanguages()) {
            if (INSPIRE_VOCABULARY.get(keywordLanguage) == null) { //if it's not yet downloaded
                String fileName = "theme." + keywordLanguage.toLowerCase() + ".rdf";
                try {
                    themeRdfUrl = new URL("http://inspire.ec.europa.eu/theme/" + fileName);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, null, ex); //won't happen
                }
                File file = new File(INSPIRE_RDF_DIR, fileName);
                try {

                    FileUtils.copyURLToFile(themeRdfUrl, file, CONNECTION_TIME_OUT, READ_TIME_OUT);
                    Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.INFO, "Downloaded " + themeRdfUrl + ".");
                    INSPIRE_VOCABULARY.put(keywordLanguage, XMLUtils.toDocument(file));
                } catch (IOException ex) {//i.e. the theme file couldn't be downloaded, re-use the local one
                    Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.SEVERE, "An exception occured while trying to download " + themeRdfUrl + ".", ex);
                    if (file.isFile()) { //if it's there already and it's a file
                        try {
                            INSPIRE_VOCABULARY.put(keywordLanguage, XMLUtils.toDocument(file));
                        } catch (Exception ex1) {
                            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                } catch (Exception ex) {//i.e. the theme file couldn't be downloaded
                    Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.SEVERE, "An exception occured while trying to download " + themeRdfUrl + ".", ex);

                }
            }
        }
        this.dataset = dataset;

        long startTime = System.currentTimeMillis();
        Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.INFO, "Marshalling XML for dataset " + dataset.getIdentifier());
        String xml = XML.marshal(metadata);
        long endTime = System.currentTimeMillis();
        Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.INFO, "Marshalling XML took " + (endTime - startTime) + " milliseconds");
        this.datasetText = new StringBuilder(xml);
        this.translator = new ISO19115StringTranslator();
        this.extraTranslations = extraTranslations;

        Set<LocalizedString> multilingualTitles = dataset.getMultilingualTitles();
        Set<LocalizedString> multilingualAbstracts = dataset.getMultilingualAbstracts();
        Set<LocalizedString> multilingualLineages = dataset.getMultilingualLineages();

        if (multilingualTitles != null && !multilingualTitles.isEmpty()) {
            extraTranslations.put(dataset.getTitle(), multilingualTitles);
        }
        if (multilingualAbstracts != null && !multilingualAbstracts.isEmpty()) {
            extraTranslations.put(dataset.getAbstract(), multilingualAbstracts);
        }
        if (multilingualLineages != null && !multilingualLineages.isEmpty()) {
            extraTranslations.put(dataset.getLineage(), multilingualLineages);
        }

        cleanupResult();
    }

    /**
     *
     * Return the languages that this metadata document should contain. Based on
     * the config.properties file.
     *
     * @return
     */
    private Set<String> getKeywordLanguages() {
        if (keywordLanguages == null || keywordLanguages.isEmpty()) {
            InputStream input = null;
            Properties prop = new Properties();
            try {
                input = ISO19115DatasetBuilder.class.getClassLoader().getResourceAsStream("config.properties");
                prop.load(input);
                keywordLanguages = new HashSet<String>(Arrays.asList(prop.getProperty("metadata.iso.inspire.keyword.languages").toUpperCase().split(",")));

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return keywordLanguages;
    }

    /**
     * Translate the occurences of Anchors (eg. in Keywords) in an ISO 19115 XML
     * String. Looks at the INSPIRE themes at http://inspire.ec.europa.eu/theme/
     * < gmx:Anchor xlink:href="http://inspire.ec.europa.eu/theme/lu">Land
     * use</gmx:Anchor>
     * becomes
     * <gmx:Anchor xlink:href="http://inspire.ec.europa.eu/theme/lu">Land
     * use</gmx:Anchor>
     * <gmd:PT_FreeText>
     * <gmd:textGroup>
     * <gmd:LocalisedCharacterString locale="#FR">Usage des
     * sols</gmd:LocalisedCharacterString>
     * </gmd:textGroup>
     * <gmd:textGroup>
     * <gmd:LocalisedCharacterString locale="#NL">Landgebruik</gmd:LocalisedCharacterString>
     * </gmd:textGroup>
     * <gmd:textGroup>
     * <gmd:LocalisedCharacterString locale="#DE">Bodennutzung</gmd:LocalisedCharacterString>
     * </gmd:textGroup>
     * </gmd:PT_FreeText>
     *
     * @param XMLText
     * @param languages The languages that terms (keywords etc.) need
     * translation to. Provide languages in upper case (EN, DE, FR, NL, ES,IT,
     * etc.). If provided as null, will resort to the default ((EN, DE, FR, NL).
     * @return
     * @throws IOException
     */
    private void processAnchorTranslations() {
        String xml = datasetText.toString();
        if (xml.contains("gmx:Anchor")) {
            List<List<String>> urlMatches = StringUtils.getRegexResultFromMultilineString(xml, Pattern.compile("<gmx:Anchor xlink:href=\"(.*?)\">"), null);
            if (!urlMatches.isEmpty()) {
                List<String> urlMatches2 = StringUtils.flattenListOfLists(urlMatches);
                Set<String> urlMatches3 = new HashSet(urlMatches2);
                if (!urlMatches3.isEmpty()) {
                    for (String urlMatch : urlMatches3) {
                        try {
                            if (urlMatch.contains("gemet/concept/")) { //instead of later do an xpath looking in the whole file for each different language, just look in the fragments of it that matter.
                                if (!GEMET_FILTERED_RESULTS.containsKey(urlMatch)) {
                                    String xmlForResult = XMLUtils.xpathQueryNodeXML(GEMET_DOCUMENT, "/rdf:RDF/rdf:Description[@rdf:about='" + urlMatch.replace("http://www.eionet.europa.eu/gemet/", "").replaceAll("/$", "") + "']", RDF_NAMESPACES);
                                    if (xmlForResult != null) {
                                        GEMET_FILTERED_RESULTS.put(urlMatch, XMLUtils.toDocument(xmlForResult));
                                    }
                                }
                            }
                            if (ANCHOR_TRANSLATIONS_MAP.get(urlMatch) == null || ANCHOR_TRANSLATIONS_MAP.get(urlMatch).isEmpty()) {
                                for (String language : getKeywordLanguages()) {
                                    if (urlMatch.contains("inspire.ec.europa.eu/theme/")) {//INSPIRE Themes
                                        Document gemetInspireDocument = INSPIRE_VOCABULARY.get(language);
                                        List<String> translations = XMLUtils.xpathQueryString(gemetInspireDocument, "/rdf:RDF/rdf:Description[@rdf:about='" + urlMatch + "']/dct:title/text()", RDF_NAMESPACES);
                                        if (translations != null && !translations.isEmpty()) {
                                            Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.INFO, "Looking up " + urlMatch + " in " + language + " in INSPIRE theme vocab.");
                                            CollectionUtils.upsertMapOfSet(ANCHOR_TRANSLATIONS_MAP, urlMatch, new LocalizedString(translations.get(0), LANGUAGES.get(language).get(0)));
                                        }
                                    } else if (urlMatch.contains("gemet/concept/")) {//GEMET themes
                                        String xPath = "/rdf:RDF/rdf:Description/skos:prefLabel[@xml:lang='" + language.toLowerCase() + "']/text()";
                                        List<String> translations = XMLUtils.xpathQueryString(GEMET_FILTERED_RESULTS.get(urlMatch), xPath, RDF_NAMESPACES);
                                        if (translations != null && !translations.isEmpty()) {
                                            Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.INFO, "Looking up " + urlMatch + " in " + language + " in GEMET theme vocab.");
                                            CollectionUtils.upsertMapOfSet(ANCHOR_TRANSLATIONS_MAP, urlMatch, new LocalizedString(translations.get(0), LANGUAGES.get(language).get(0)));
                                        }

                                    }
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.SEVERE, "An exception occured. " + urlMatch + " could not be found.", ex);
                        }

                        if (ANCHOR_TRANSLATIONS_MAP.get(urlMatch) != null && !ANCHOR_TRANSLATIONS_MAP.get(urlMatch).isEmpty()) {
                            translator.translate(new XMLElement("gmx:Anchor", null, "xlink:href", urlMatch), ANCHOR_TRANSLATIONS_MAP.get(urlMatch));
                        }
                    }
                }
            }
        }
    }

    /**
     * *
     * Serialize to the ISO 19115 XML representation of the given ISO 19115
     * metadata content.
     *
     * @return The ISO XML
     */
    private void cleanupResult() throws JAXBException {
        Date start = dataset.getStartDate();
        Date end = dataset.getEndDate();

        /*replaceAll("\n", " "); //flatten everything!
        replaceAll("\t", " ");
        replaceAll("> *<", "><");*/
        if (start != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String endString;
            if (end != null) {
                endString = "<gml:endPosition>" + dateFormatter.format(end) + "</gml:endPosition>";
            } else {
                endString = "<gml:endPosition indeterminatePosition=\"now\" ></gml:endPosition>";
            }

            UUID uuid = UUID.randomUUID();

            replaceAll("<gmd:EX_TemporalExtent/>", "<gmd:EX_TemporalExtent><gmd:extent>"
                    + "<gml:TimePeriod  gml:id=\"ID" + uuid.toString() + "\" xsi:type=\"gml:TimePeriodType\">"
                    + "<gml:beginPosition>" + dateFormatter.format(start)
                    + "</gml:beginPosition>"
                    + endString
                    + "</gml:TimePeriod>"
                    + "</gmd:extent></gmd:EX_TemporalExtent>");
        }

        String header = "<gmd:MD_Metadata xmlns:gmd=\"http://www.isotc211.org/2005/gmd\"\n"
                + "                 xmlns:gco=\"http://www.isotc211.org/2005/gco\"\n"
                + "                 xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"
                + "                 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "                 xmlns:gml=\"http://www.opengis.net/gml/3.2\"\n"
                + "                 xmlns:geonet=\"http://www.fao.org/geonetwork\"\n"
                + "                 xmlns:gmx=\"http://www.isotc211.org/2005/gmx\""
                + "                 xsi:schemaLocation=\"http://www.isotc211.org/2005/gmd http://schemas.opengis.net/iso/19139/20070417/gmd/gmd.xsd http://www.isotc211.org/2005/gmx http://schemas.opengis.net/iso/19139/20070417/gmx/gmx.xsd\">";

        replaceAll("<gmd:MD_Metadata(.|\\n)*?>", header);
        replace("<gco:CharacterString>ISO 19115-1:2014(E)</gco:CharacterString>", "<gco:CharacterString>ISO 19115:2003/19139</gco:CharacterString>");
        replace("<gco:CharacterString>Geographic Information — Metadata Part 1: Fundamentals</gco:CharacterString>", "<gco:CharacterString>Geographic information -- Metadata</gco:CharacterString>");
        replace(" codeSpace=\"eng\"", "");
        replace("codeListValue=\"nld\">nld", "codeListValue=\"nld\">Dutch");
        replace("codeListValue=\"fra\"", "codeListValue=\"fre\"");
        replace("codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#LanguageCode\"", "codeList=\"http://www.loc.gov/standards/iso639-2/\"");
        replace("codeListValue=\"publication\">Publication", "codeListValue=\"publication\">publication"); //not liked by the INSPIRE validator

        String pattern1 = "<gco:CharacterString>&lt;gmx:Anchor xlink:href=\"(.*?)\"/&gt;</gco:CharacterString>";
        String pattern2 = "<gco:CharacterString>&lt;gmx:Anchor xlink:href=\"(.*?)\"&gt;(.*?)&lt;/gmx:Anchor&gt;</gco:CharacterString>";

        replaceAll(pattern1, "<gmx:Anchor xlink:href=\"$1\"></gmx:Anchor>");
        replaceAll(pattern2, "<gmx:Anchor xlink:href=\"$1\">$2</gmx:Anchor>");

        long startTime = System.currentTimeMillis();

        String xml = datasetText.toString();
        if (xml.contains("gmx:Anchor") || (extraTranslations != null && !extraTranslations.isEmpty())) {
            translator.setDocument(xml);
            processAnchorTranslations();
            if (extraTranslations != null && !extraTranslations.isEmpty()) {
                translator.translate(extraTranslations);
            }
            translator.finalizeTranslations();
            datasetText = new StringBuilder(XMLUtils.toXML(translator.getDocument()));
            long endTime = System.currentTimeMillis();
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.INFO, "Translating elements for dataset " + dataset.getIdentifier() + " took " + (endTime - startTime) + " milliseconds");
        }
        dataset = null;
        translator = null;
        extraTranslations = null;
    }

    private static Pattern tabs = Pattern.compile("\t");
    private static Pattern spaces = Pattern.compile(" +");
    private static Pattern newlines = Pattern.compile("\r\n");
    private static Pattern newlines2 = Pattern.compile("\n");
    private static Pattern emptyLines = Pattern.compile("([\\n\\r]+\\s*)*$");
    private static Pattern dates = Pattern.compile("<gmd:dateStamp><gco:DateTime>.*?<\\/gco:DateTime><\\/gmd:dateStamp>");
    private static Pattern gmlId = Pattern.compile(" gml:id=\".*?\"");

    /**
     * *
     * Verify whether two ISO 19115:2004 xml documents equal each other,
     * ignoring the creation dates
     *
     * @param xml1
     * @param xml2
     * @return
     */
    private boolean resultEquals(String xml1, String xml2) {
        Map<Pattern, String> repl = new LinkedHashMap(); //maintain insertion order!
        repl.put(tabs, "");
        repl.put(spaces, "");
        repl.put(newlines, "");
        repl.put(newlines2, "");
        repl.put(emptyLines, "");
        repl.put(dates, "");
        repl.put(gmlId, "");
        String comp1 = StringUtils.replaceAll(xml1, repl);
        String comp2 = StringUtils.replaceAll(xml2, repl);
        // String comp1 = xml1.replaceAll("\t", "").replaceAll("  ", "").replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("([\\n\\r]+\\s*)*$", "").replaceAll("<gmd:dateStamp><gco:DateTime>.*?<\\/gco:DateTime><\\/gmd:dateStamp>", "").replaceAll(" gml:id=\".*?\"", "");
        // String comp2 = xml2.replaceAll("\t", "").replaceAll("  ", "").replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("([\\n\\r]+\\s*)*$", "").replaceAll("<gmd:dateStamp><gco:DateTime>.*?<\\/gco:DateTime><\\/gmd:dateStamp>", "").replaceAll(" gml:id=\".*?\"", "");
        return comp1.equals(comp2);
    }

    /**
     * *
     * Verify whether two ISO 19115:2004 xml documents equal each other,
     * ignoring the gmd:datestamp dates and different values for gml:ids
     *
     * @param xml1
     * @param xml2
     * @return
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (other instanceof ISO19115DatasetPrinter) {
            ISO19115DatasetPrinter otherPrinter = (ISO19115DatasetPrinter) other;
            return resultEquals(this.getResult(), otherPrinter.getResult());
        }
        return false;
    }

    public void replace(String from, String to) {
        datasetText = StringUtils.stringBuilderReplace(datasetText, from, to);
    }

    /**
     * c
     * *
     * Replace a Regex pattern with a replacement pattern.
     *
     * @param from
     * @param to
     */
    public void replaceAll(String from, String to) {
        datasetText = StringUtils.stringBuilderReplaceAll(datasetText, Pattern.compile(from), to);
    }

    /**
     * *
     * Write the given metadata content to the given File if the original file
     * should be overwritten. File should not be a path. alwaysOverwrite=true: a
     * new copy is always created. alwaysOverwrite=false: Only if a file
     * contains any changes (disregarding gmd:dateStamp) a previous copy will be
     * overwritten.
     *
     * @param file
     * @param alwaysOverwrite If set to true, will overwrite even if it replaces
     * an identical file (This ignores gmd:dateStamp)
     * @throws FileNotFoundException
     */
    public void createFile(File file, boolean alwaysOverwrite) throws FileNotFoundException {
        String newXml = getResult();
        boolean needsUpdate = false;
        String existingXml = null;
        StringBuilder message = new StringBuilder("File ");
        message.append(file.getName());
        try {
            existingXml = FileUtils.readFileToString(file, "UTF-8");
            needsUpdate = !resultEquals(existingXml, newXml);
        } catch (IOException ex) {
            needsUpdate = true;
        }

        if (alwaysOverwrite || needsUpdate) {
            if (alwaysOverwrite) {
                message.append(" overwritten because method called with alwaysOverwrite=true.");
            }
            if (needsUpdate) {
                if (existingXml == null) {
                    message.append(" created for the first time.");
                } else {
                    List<String> diffs = StringUtils.findNotMatching(existingXml, newXml);
                    message.append(" overwritten because updates where needed. All differences (some were ignored):");
                    message.append("\n");
                    for (String diff : diffs) {
                        message.append("----\n");
                        message.append(diff);
                        message.append("----\n");
                    }
                }

            }
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(newXml);
            }

        } else {
            message.append(" not overwritten because the new file is identical (except for gml:id and the metadata timestamp)");
        }
        Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.INFO, message.toString());
    }

    @Override
    public String toString() {
        return getResult();
    }
}