/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.iso;

import be.naturalsciences.bmdc.metadata.datacite.PostIsoMetadataTest;
import be.naturalsciences.bmdc.metadata.datacite.ISO19115toDataCitePublisher;
import static be.naturalsciences.bmdc.metadata.iso.ISO19115DatasetBuilder.LANGUAGES;
import static java.util.Collections.singleton;
import be.naturalsciences.bmdc.metadata.model.FormatEnum;
import be.naturalsciences.bmdc.metadata.model.IDataset;
import be.naturalsciences.bmdc.metadata.model.IDatasource;
import be.naturalsciences.bmdc.metadata.model.IDistributionFormat;
import be.naturalsciences.bmdc.metadata.model.IDistributionResource;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import be.naturalsciences.bmdc.metadata.model.IKeyword;
import be.naturalsciences.bmdc.metadata.model.OnlinePossibilityEnum;
import be.naturalsciences.bmdc.metadata.model.ProtocolEnum;
import be.naturalsciences.bmdc.metadata.model.Thesaurus;
import be.naturalsciences.bmdc.metadata.model.impl.Dataset;
import be.naturalsciences.bmdc.metadata.model.impl.Datasource;
import be.naturalsciences.bmdc.metadata.model.impl.DistributionFormat;
import be.naturalsciences.bmdc.metadata.model.impl.DistributionResource;
import be.naturalsciences.bmdc.metadata.model.impl.InstituteRole;
import be.naturalsciences.bmdc.metadata.model.impl.Keyword;
import be.naturalsciences.bmdc.utils.FileUtils;
import be.naturalsciences.bmdc.utils.LocalizedString;
import be.naturalsciences.bmdc.utils.StringUtils;
import gnu.trove.set.hash.THashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author thomas
 */
public class ISO19115DatasetPrinterTest {

    public static final Map<String, Set<LocalizedString>> TRANSLATIONS = new LinkedHashMap<>(); //Maintain insertion order!

    public static IInstituteRole BMDC_CONTACT = null;
    public static IInstituteRole BMDC_AUTHOR = null;
    public static IInstituteRole BMDC_CUSTODIAN = null;
    public static IInstituteRole BMDC_DISTRIBUTOR = null;

    public static String BMDC_EN = "Royal Belgian Institute for Natural Sciences, Directorate Natural Environment, Belgian Marine Data Centre (BMDC)";
    public static String BMDC_NL = "Koninklijk Belgisch Instituut voor Natuurwetenschappen, Operationele Directie Natuurlijk Milieu, Belgian Marine Data Centre (BMDC)";
    public static String BMDC_FR = "Institut royal des Sciences naturelles de Belgique, Direction opérationnelle Milieux naturels, Belgian Marine Data Centre (BMDC)";
    public static String BMDC_DE = "Königliches Belgisches Institut für Naturwissenschaften, Operationelle Direktion Natürliche Umwelt, Belgian Marine Data Centre (BMDC)";
    public static String BMDC_PHONE = "02/773.21.44";
    public static String BMDC_EMAIL = "bmdc@naturalsciences.be";
    public static String BMDC_WEBSITE = "http://www.bmdc.be";
    public static String RBINS_ADRESS = "Vautierstraat 29";
    public static String BRUSSELS_NAME = "Brussel";
    public static String BRUSSELS_POSTAL_CODE = "1000";
    public static String BELGIUM_ISO_3166 = "BE";

    public static String BELGIUM_EN = "Belgium";
    public static String BELGIUM_NL = "België";
    public static String BELGIUM_FR = "Belgique";
    public static String BELGIUM_DE = "Belgien";

    public static String MSFD_IMAGE = "http://geonetwork.bmdc.be/geonetwork/images/logos/MSFD-logo.jpg";

    static {
        BMDC_CONTACT = new InstituteRole(IDataset.Role.POINT_OF_CONTACT, BMDC_EN, BMDC_PHONE, null, RBINS_ADRESS, BRUSSELS_NAME, BRUSSELS_POSTAL_CODE, BELGIUM_ISO_3166, BMDC_EMAIL, BMDC_WEBSITE);
        BMDC_AUTHOR = new InstituteRole(IDataset.Role.AUTHOR, BMDC_EN, BMDC_PHONE, null, RBINS_ADRESS, BRUSSELS_NAME, BRUSSELS_POSTAL_CODE, BELGIUM_ISO_3166, BMDC_EMAIL, BMDC_WEBSITE);
        BMDC_CUSTODIAN = new InstituteRole(IDataset.Role.CUSTODIAN, BMDC_EN, BMDC_PHONE, null, RBINS_ADRESS, BRUSSELS_NAME, BRUSSELS_POSTAL_CODE, BELGIUM_ISO_3166, BMDC_EMAIL, BMDC_WEBSITE);
        BMDC_DISTRIBUTOR = new InstituteRole(IDataset.Role.DISTRIBUTOR, BMDC_EN, BMDC_PHONE, null, RBINS_ADRESS, BRUSSELS_NAME, BRUSSELS_POSTAL_CODE, BELGIUM_ISO_3166, BMDC_EMAIL, BMDC_WEBSITE);

        TRANSLATIONS.put(RBINS_ADRESS, new HashSet<LocalizedString>(Arrays.asList(new LocalizedString[]{new LocalizedString("Vautierstraat 29", LANGUAGES.get("NL").get(0)), new LocalizedString("Rue Vautier 29", LANGUAGES.get("FR").get(0))})));
        TRANSLATIONS.put(BRUSSELS_NAME, new HashSet<LocalizedString>(Arrays.asList(new LocalizedString[]{new LocalizedString("Brussel", LANGUAGES.get("NL").get(0)), new LocalizedString("Bruxelles", LANGUAGES.get("FR").get(0))})));
        TRANSLATIONS.put(BMDC_EN, new HashSet<LocalizedString>(Arrays.asList(new LocalizedString[]{new LocalizedString(BMDC_EN, LANGUAGES.get("EN").get(0)), new LocalizedString(BMDC_FR, LANGUAGES.get("FR").get(0)), new LocalizedString(BMDC_NL, LANGUAGES.get("NL").get(0)), new LocalizedString(BMDC_DE, LANGUAGES.get("DE").get(0))})));
        TRANSLATIONS.put(BELGIUM_EN, new HashSet<LocalizedString>(Arrays.asList(new LocalizedString[]{new LocalizedString(BELGIUM_EN, LANGUAGES.get("EN").get(0)), new LocalizedString(BELGIUM_FR, LANGUAGES.get("FR").get(0)), new LocalizedString(BELGIUM_NL, LANGUAGES.get("NL").get(0)), new LocalizedString(BELGIUM_DE, LANGUAGES.get("DE").get(0))})));
    }

    public static Dataset createFakeDataset1() {
        Date publicationDate1 = new GregorianCalendar(2008, Calendar.DECEMBER, 10).getTime();
        Date publicationDate2 = new GregorianCalendar(2009, Calendar.JANUARY, 10).getTime();

        Date creationDate = new GregorianCalendar(2016, Calendar.DECEMBER, 1).getTime();
        Date revisionDate = new GregorianCalendar(2016, Calendar.DECEMBER, 10).getTime();

        Date startDate = new GregorianCalendar(2005, Calendar.JANUARY, 1).getTime();
        Date endDate = new GregorianCalendar(2008, Calendar.JANUARY, 1).getTime();

        IInstituteRole distributor = new InstituteRole(IDataset.Role.DISTRIBUTOR, "BMDC", "02/545542", null, "Vautierstraat 29", "Brussel", "1000", "BE", "info@bmdc.be", "http://www.naturalsciences.be");
        List<IInstituteRole> distributors = new ArrayList<>();
        distributors.add(distributor);

        IInstituteRole author = new InstituteRole(IDataset.Role.AUTHOR, "MARECO", "02/545542", null, "Vautierstraat 29", "Brussel", "1000", "BE", "info@bmdc.be", "http://www.naturalsciences.be");
        IInstituteRole originator = new InstituteRole(IDataset.Role.ORIGINATOR, "VLIZ", "059/65466546", null, "Wandelaarkaai 7", "Oostende", "8400", "BE", "info@vliz.be", "http://www.vliz.be");
        List<IInstituteRole> parties = new ArrayList<>();
        parties.add(author);
        parties.add(originator);
        IDatasource dso = new Datasource("Advanced modelling and research on...", null, "bmdc:dso::2200", publicationDate1, "Degraer, Haelters, Reubens", parties, new HashSet<>(Arrays.asList("NL", "EN")), null, null, null, null, null);
        IDatasource dso2 = new Datasource("Final report on modelling and research on...", null, "bmdc:dso::2201", publicationDate2, "Degraer, Rumes, Fettweis", parties, new HashSet<>(Arrays.asList("EN")), null, null, null, null, null);
        List<IDatasource> sources = new ArrayList<>();
        sources.add(dso);
        sources.add(dso2);
        DistributionFormat format = new DistributionFormat("geo-json", FormatEnum.JSON, "geo-json", null, null);
        DistributionFormat format2 = new DistributionFormat("NetCDF", FormatEnum.NETCDF, "NetCDF", "1.2", null);
        List<IDistributionFormat> formats = new ArrayList<>();
        formats.add(format);
        formats.add(format2);
        Set<IDistributionResource> resources = new LinkedHashSet<>();
        try {
            IInstituteRole BMDC_DISTRIBUTOR = new InstituteRole(IDataset.Role.DISTRIBUTOR, "BMDC", "02/773.21.44", null, "Vautierstraat 29", "Brussel", "1000", "BE", "bmdc@naturalsciences.be", "http://www.bmdc.be");

            DistributionResource rsr = new DistributionResource(OnlinePossibilityEnum.DOWNLOAD, new URL("http://dits.bmdc.be/dits/datasource/1287/20141030_0423231287-QUASH Step 1 1997.xls"), ProtocolEnum.WWW_LINK_10_http_link, "Identifier", "Data from the dataset in the format xx", "Data from the dataset in the format xx. Here you can find variuous datasets", formats, new Double(1235463), BMDC_DISTRIBUTOR);
            resources.add(rsr);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ISO19115DatasetPrinterTest.class.getName()).log(Level.SEVERE, "An exception occured.", ex);
        }

        Set<IDataset.InspireTheme> themes = new THashSet<>();
        //themes.add(IDataset.InspireTheme.OCEANOGRAPHIC_GEOGRAPHICAL_FEATURES);

        Dataset dst = new Dataset("bmdc:dst::5020", "Data from 'Advanced modelling and research on...'", creationDate, revisionDate, new HashSet<>(Arrays.asList("NL", "EN")), "4326", null, null, sources, "Dataset combined from two publications. Contains parameters on salinity, temperature, biota composition and mainly biodiversity. Gathered from 2005 to 2008. Belgica research vessel during cruises...", startDate, endDate, 50.0, 52.0, 2.0, 3.0, resources, "Dataset combined from sources. Descriptive staistical analysis performed.", themes, null, IDataset.SpatialType.VECTOR, createKeywords());
        dst.setBrowseGraphicUrl(MSFD_IMAGE);
        return dst;
    }

    public static IDataset createFakeDataset2() {
        Date publicationDate1 = new GregorianCalendar(2008, Calendar.DECEMBER, 10).getTime();
        Date publicationDate2 = new GregorianCalendar(2009, Calendar.JANUARY, 10).getTime();

        Date creationDate = new GregorianCalendar(2016, Calendar.DECEMBER, 1).getTime();
        Date revisionDate = new GregorianCalendar(2016, Calendar.DECEMBER, 10).getTime();

        Date startDate = new GregorianCalendar(2005, Calendar.JANUARY, 1).getTime();
        Date endDate = new GregorianCalendar(2008, Calendar.JANUARY, 1).getTime();

        IInstituteRole author = new InstituteRole(IDataset.Role.AUTHOR, "MARECO", "02/545542", null, "Vautierstraat 29", "Brussel", "1000", "BE", "info@bmdc.be", "http://www.naturalsciences.be");
        IInstituteRole originator = new InstituteRole(IDataset.Role.ORIGINATOR, "VLIZ", "059/65466546", null, "Wandelaarkaai 7", "Oostende", "8400", "BE", "info@vliz.be", "http://www.vliz.be");
        List<IInstituteRole> parties = new ArrayList<>();
        parties.add(author);
        parties.add(originator);
        IDatasource dso = new Datasource("Advanced modelling and research on...", null, "bmdc:dso::2200", publicationDate1, "Degraer, Haelters, Reubens", parties, new HashSet<>(Arrays.asList("NL", "EN")), null, null, null, null, null);
        IDatasource dso2 = new Datasource("Final report on modelling and research on...", null, "bmdc:dso::2201", publicationDate2, "Degraer, Rumes, Fettweis", parties, new HashSet<>(Arrays.asList("EN")), null, null, null, null, null);
        List<IDatasource> sources = new ArrayList<>();
        sources.add(dso);
        sources.add(dso2);

        Set<IDataset.InspireTheme> themes = new THashSet<>();
        Dataset dst = new Dataset("bmdc:dst::5021", "Data from 'Advanced modelling and research on...'", creationDate, revisionDate, new HashSet<>(Arrays.asList("NL", "EN")), "4326", null, null, sources, "Dataset combined from two publications. Contains parameters on salinity, temperature, biota composition and mainly biodiversity. Gathered from 2005 to 2008. Belgica research vessel during cruises...", startDate, endDate, 50.0, 52.0, 2.0, 3.0, null, "Dataset combined from sources. Descriptive staistical analysis performed.", themes, null, IDataset.SpatialType.TEXT_TABLE, createKeywords());
        Set<LocalizedString> titles = new HashSet<>();
        titles.add(new LocalizedString("Les données du dataset 'Advanced modelling and research on...'", Locale.FRENCH));
        titles.add(new LocalizedString("Die daten datasets 'Advanced modelling and research on...'", Locale.GERMAN));
        dst.setMultilingualTitles(titles);
        dst.setPointsOfContact(singleton(BMDC_CUSTODIAN));
        dst.setBrowseGraphicUrl(MSFD_IMAGE);
        return dst;
    }

    public static Thesaurus gemet = new Thesaurus("GEMET - Concepts, version 4.1.2", null, "https://www.eionet.europa.eu/gemet/en/themes/", new Date(), "4.1.2", Thesaurus.PUBLICATION);
    public static Thesaurus mr = new Thesaurus("Marine Regions", null, "http://marineregions.org", new Date(), "10", Thesaurus.PUBLICATION);
    public static Thesaurus eionet = new Thesaurus("Eionet Central Data Repository", null, "http://cdr.eionet.europa.eu/help/msfd/Schemas/MSFDCommon_2018.xsd", new Date(), null, Thesaurus.PUBLICATION);

    public static IKeyword k1 = new Keyword(0L, "http://www.eionet.europa.eu/gemet/concept/15228", "Marine Strategy Framework Directive", "theme", gemet);
    public static IKeyword k3 = new Keyword(0L, "http://inspire.ec.europa.eu/theme/sr", "Sea regions", "theme", gemet);
    public static IKeyword k4 = new Keyword(0L, "http://cdr.eionet.europa.eu/help/msfd/Schemas/MSFDCommon_2018.xsd#D11C1", "MD11C1 - Anthropogenic impulsive sound", "theme", eionet);
    public static IKeyword k5 = new Keyword(0L, "http://marineregions.org/mrgid/26567", "Belgian part of the North Sea", "place", mr);
    public static IKeyword k6 = new Keyword(0L, "http://marineregions.org/mrgid/50155", "Greater North Sea, incl. the Kattegat and the English Channel", "place", mr);

    private static Set<IKeyword> createKeywords() {
        Set<IKeyword> keywords = new HashSet<>();
        keywords.add(k1);
        keywords.add(k3);
        keywords.add(k4);
        keywords.add(k5);
        keywords.add(k6);
        return keywords;
    }

    private static String getUntranslatedKeywordXML(IKeyword keyword) {

        Thesaurus keywordThesaurus = keyword.getThesaurus();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return ("<gmd:keyword>"
                + "            <gmx:Anchor xlink:href=\"" + keyword.getUrl() + "\">" + keyword.getPrefLabel() + "</gmx:Anchor>"
                + "          </gmd:keyword>"
                + "          <gmd:type>"
                + "            <gmd:MD_KeywordTypeCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_KeywordTypeCode\" codeListValue=\"" + keyword.getType() + "\">" + keyword.getType().substring(0, 1).toUpperCase() + keyword.getType().substring(1).toLowerCase() + "</gmd:MD_KeywordTypeCode>\n"
                + "          </gmd:type>"
                + "          <gmd:thesaurusName>"
                + "            <gmd:CI_Citation>"
                + "              <gmd:title>"
                + keywordThesaurus.getThesaurusUrl() == null ? "<gco:CharacterString >" + keywordThesaurus.getThesaurusTitle() + "</gco:CharacterString>" : "<gmx:Anchor xlink:href=\"" + keywordThesaurus.getThesaurusUrl() + "\">" + keywordThesaurus.getThesaurusTitle() + "</gmx:Anchor>"
                + "              </gmd:title>"
                + "              <gmd:date>"
                + "                <gmd:CI_Date>"
                + "                  <gmd:date>"
                + "                    <gco:Date>" + df.format(keywordThesaurus.getThesaurusDate()) + "</gco:Date>"
                + "                  </gmd:date>"
                + "                  <gmd:dateType>"
                + "                    <gmd:CI_DateTypeCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_DateTypeCode\" codeListValue=\"publication\">publication</gmd:CI_DateTypeCode>"
                + "                  </gmd:dateType>"
                + "                </gmd:CI_Date>"
                + "              </gmd:date>"
                + (keywordThesaurus.getThesaurusVersion() != null
                ? "              <gmd:edition>"
                + "                <gco:CharacterString>" + keywordThesaurus.getThesaurusVersion() + "</gco:CharacterString>"
                + "              </gmd:edition>" : "")
                + "            </gmd:CI_Citation>"
                + "          </gmd:thesaurusName>").replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "");
    }

    public ISO19115DatasetPrinterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getResult method, of class ISO19115DatasetPrinter.
     */
    @Test
    public void testGetResult() throws IOException, FileNotFoundException, JAXBException {
        System.out.println("getResult");
        Dataset ds1 = createFakeDataset1();
        //testXML(ds1);
        //testNoAnchor(ds1);
        testAnchor(ds1);

        IDataset ds2 = createFakeDataset2();
        testXML(ds2);
        testDOI(ds2);
        //testNoAnchor(ds2);
        testAnchor(ds2);
        testTranslatedTitle(ds2);
        testAnchorForLaw(ds2);
    }

    /**
     * Test of equals method, of class ISO19115DatasetPrinter.
     */
    @Test
    @Ignore
    public void testEquals() throws JAXBException, IOException {
        System.out.println("equals");
        IDataset ds = createFakeDataset2();
        ISO19115DatasetBuilder builder = null;
        ISO19115DatasetBuilder builder2 = null;
        ISO19115DatasetPrinter printer = null;
        ISO19115DatasetPrinter printer2 = null;
        if (ds != null) {
            builder = new ISO19115DatasetBuilder(ds, false, true, false, false, BMDC_CONTACT, "Belgian Marine Data Centre");
            printer = new ISO19115DatasetPrinter(builder, new HashSet<>(Arrays.asList(new String[]{"EN", "NL", "FR", "DE"})), TRANSLATIONS, null, false);
        }

        IDataset ds2 = createFakeDataset2();
        if (ds2 != null) {
            builder2 = new ISO19115DatasetBuilder(ds2, false, true, false, false, BMDC_CONTACT, "Belgian Marine Data Centre");
            printer2 = new ISO19115DatasetPrinter(builder2, new HashSet<>(Arrays.asList(new String[]{"EN", "NL", "FR", "DE"})), TRANSLATIONS, null, false);
        }
        if (printer != null && !printer.equals(printer2)) {
            innerTestCreateFile(builder, "TEST_EQUALS_" + builder.getFileName());
            innerTestCreateFile(builder2, "TEST_EQUALS_" + builder2.getFileName());
            System.out.println("<---DIFFS---->");
            for (String string : StringUtils.findNotMatching(printer.print(), printer2.print())) {
                System.out.println(string);
            }
            System.out.println("<---DIFFS---->");

        }
        assertTrue(printer.equals(printer2));
    }

    private void testDOI(IDataset dst) throws IOException, JAXBException {
        ISO19115DatasetBuilder builder = new ISO19115DatasetBuilder(dst, false, true, false, false, null, "Belgian Marine Data Centre");
        ISO19115DatasetPrinter printer = new ISO19115DatasetPrinter(builder, new HashSet<>(Arrays.asList(new String[]{"EN", "NL", "FR", "DE"})), TRANSLATIONS, new ISO19115toDataCitePublisher(PostIsoMetadataTest.TEST_ACCOUNT), false);
        String xml = printer.print();
        String id = dst.getIdentifier();
        //assertTrue(xml.contains("<gmx:Anchor xlink:href=\"https://doi.org/10.5072/" + id + "\">10.5072/" + id + "</gmx:Anchor>"));
    }

    private void testXML(IDataset dst) throws IOException, JAXBException {
        ISO19115DatasetBuilder builder = new ISO19115DatasetBuilder(dst, false, true, false, false, null, "Belgian Marine Data Centre");
        ISO19115DatasetPrinter printer = new ISO19115DatasetPrinter(builder, new HashSet<>(Arrays.asList(new String[]{"EN", "NL", "FR", "DE"})), TRANSLATIONS, null, false);
        String xml = printer.print();
        System.out.println(xml);
        assertTrue(xml.contains("xml version=\"1.0\""));
        assertTrue(xml.contains("MD_Metadata"));

        assertFalse(Pattern.compile("</gmx:Anchor>\n?</gco:CharacterString>").matcher(xml).find());
        assertFalse(xml.contains("Geographic Information — Metadata Part 1: Fundamentals"));  //"Geographic Information — Metadata Part 1: Fundamentals"
        assertTrue(xml.contains("Geographic information -- Metadata"));
        assertTrue(xml.contains("<gco:Decimal>50.0</gco:Decimal>"));
        assertTrue(xml.contains("<gco:Decimal>52.0</gco:Decimal>"));
        assertTrue(xml.contains("<gco:Decimal>2.0</gco:Decimal>"));
        assertTrue(xml.contains("<gco:Decimal>3.0</gco:Decimal>"));

        assertTrue(xml.contains("Koninklijk Belgisch Instituut voor Natuurwetenschappen, Operationele Directie Natuurlijk Milieu, Belgian Marine Data Centre (BMDC)"));

        assertTrue(xml.contains("<gmd:LocalisedCharacterString locale=\"#EN\">Royal Belgian Institute for Natural Sciences,"));
        assertTrue(xml.contains("<gmd:LocalisedCharacterString locale=\"#FR\">Rue Vautier 29"));
        assertTrue(xml.contains("<gmd:LocalisedCharacterString locale=\"#NL\">Vautierstraat 29"));

        assertTrue(xml.contains("<gmd:deliveryPoint xsi:type=\"gmd:PT_FreeText_PropertyType\">"));

        assertTrue(xml.contains("<gmd:PT_Locale id=\"NL\">"));
        assertTrue(xml.contains("<gmd:PT_Locale id=\"FR\">"));
        assertTrue(xml.contains("Royal Belgian Institute for Natural Sciences, Directorate Natural Environment, Belgian Marine Data Centre (BMDC)"));
        // assertTrue(xml.contains("<gmd:MD_SpatialRepresentationTypeCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_SpatialRepresentationTypeCode\" codeListValue=\"textTable\">Text table</gmd:MD_SpatialRepresentationTypeCode>"));

        assertTrue(xml.contains(MSFD_IMAGE));

        xml = xml.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "");

        // assertTrue(xml.contains("<gmd:graphicOverview><gmd:MD_BrowseGraphic><gmd:fileName><gco:CharacterString>" + MSFD_IMAGE + "</gco:CharacterString></gmd:fileName><gmd:fileDescription><gco:CharacterString>large_thumbnail</gco:CharacterString></gmd:fileDescription><gmd:fileType><gmx:MimeFileTypetype=\"jpg\">jpg</gmx:MimeFileType></gmd:fileType></gmd:MD_BrowseGraphic></gmd:graphicOverview>"));
        // assertTrue(xml.contains(getUntranslatedKeywordXML(k1)));
        // assertTrue(xml.contains(getUntranslatedKeywordXML(k3)));
        // assertTrue(xml.contains(getUntranslatedKeywordXML(k4)));
    }

    private void testNoAnchor(IDataset dst) throws IOException, FileNotFoundException, JAXBException {
        ISO19115DatasetBuilder builder = new ISO19115DatasetBuilder(dst, false, false, false, false, null, "Belgian Marine Data Centre");
        File innerTestFile = innerTestCreateFile(builder, builder.getFileName());
        boolean foundAnchor = FileUtils.findStringInFile(innerTestFile.toPath(), "gmx:Anchor");
        assertFalse(foundAnchor);
    }

    private void testAnchor(IDataset dst) throws IOException, FileNotFoundException, JAXBException {
        ISO19115DatasetBuilder builder = new ISO19115DatasetBuilder(dst, false, true, false, false, null, "Belgian Marine Data Centre");
        File innerTestFile = innerTestCreateFile(builder, builder.getFileName());
        boolean foundAnchor = FileUtils.findStringInFile(innerTestFile.toPath(), "gmx:Anchor") && FileUtils.findStringInFile(innerTestFile.toPath(), "gmx:Anchor");
        assertTrue(foundAnchor);
    }

    private File innerTestCreateFile(ISO19115DatasetBuilder instance, String fileName) throws IOException, FileNotFoundException, JAXBException {
        //String getMetadataFileName = instance.getMetadataFileName();
        ISO19115DatasetPrinter printer = new ISO19115DatasetPrinter(instance, new HashSet<>(Arrays.asList(new String[]{"EN", "NL", "FR", "DE"})), TRANSLATIONS, new ISO19115toDataCitePublisher(PostIsoMetadataTest.TEST_ACCOUNT), false);
        File file = File.createTempFile(fileName, ".xml");
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Test file stored at " + file.getPath());

        printer.createFile(file, false);

        return file;
    }

    private void testTranslatedTitle(IDataset ds) throws JAXBException {
        ISO19115DatasetBuilder builder = new ISO19115DatasetBuilder(ds, false, true, false, false, null, "Belgian Marine Data Centre");
        ISO19115DatasetPrinter printer = new ISO19115DatasetPrinter(builder, new HashSet<>(Arrays.asList(new String[]{"EN", "NL", "FR", "DE"})), TRANSLATIONS, null, false);
        String xml = printer.print();
        assertTrue(xml.contains("<gmd:LocalisedCharacterString locale=\"#DE\">Die daten datasets 'Advanced modelling and research on...'</gmd:LocalisedCharacterString>"));
    }

    private void testAnchorForLaw(IDataset ds) throws JAXBException {
          ISO19115DatasetBuilder builder = new ISO19115DatasetBuilder(ds, true, true, false, false, null, "Belgian Marine Data Centre");
        ISO19115DatasetPrinter printer = new ISO19115DatasetPrinter(builder, new HashSet<>(Arrays.asList(new String[]{"EN", "NL", "FR", "DE"})), TRANSLATIONS, null, false);
        String xml = printer.print();
        System.out.println(xml);
        assertTrue(xml.contains("<gmx:Anchor xlink:href=\"https://eur-lex.europa.eu/eli/reg/2010/1089\">"));

    }

}
