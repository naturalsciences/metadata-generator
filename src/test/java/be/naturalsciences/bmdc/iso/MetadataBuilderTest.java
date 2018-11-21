/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.iso;

import be.naturalsciences.bmdc.metadata.iso.ISO19115DatasetBuilder;
import be.naturalsciences.bmdc.metadata.model.ProtocolEnum;
import be.naturalsciences.bmdc.metadata.model.FormatEnum;
import be.naturalsciences.bmdc.metadata.model.IDatasource;
import be.naturalsciences.bmdc.metadata.model.IDistributionResource;
import be.naturalsciences.bmdc.metadata.model.IKeyword;
import be.naturalsciences.bmdc.metadata.model.IDistributionFormat;
import be.naturalsciences.bmdc.metadata.model.IDataset;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import be.naturalsciences.bmdc.metadata.model.impl.InstituteRole;
import be.naturalsciences.bmdc.metadata.model.impl.DistributionResource;
import be.naturalsciences.bmdc.metadata.model.impl.Datasource;
import be.naturalsciences.bmdc.metadata.model.impl.Dataset;
import be.naturalsciences.bmdc.metadata.model.impl.DistributionFormat;
import be.naturalsciences.bmdc.metadata.model.OnlinePossibilityEnum;
import be.naturalsciences.bmdc.metadata.model.impl.Keyword;
import be.naturalsciences.bmdc.utils.FileUtils;
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
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author thomas
 */
public class MetadataBuilderTest {

    public MetadataBuilderTest() {
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

    public static Dataset createFakeDataset1() {
        Date publicationDate1 = new GregorianCalendar(2008, Calendar.DECEMBER, 10).getTime();
        Date publicationDate2 = new GregorianCalendar(2009, Calendar.JANUARY, 10).getTime();

        Date creationDate = new GregorianCalendar(2016, Calendar.DECEMBER, 1).getTime();
        Date revisionDate = new GregorianCalendar(2016, Calendar.DECEMBER, 10).getTime();

        Date startDate = new GregorianCalendar(2005, Calendar.JANUARY, 1).getTime();
        Date endDate = new GregorianCalendar(2008, Calendar.JANUARY, 1).getTime();

        IInstituteRole distributor = new InstituteRole(IDataset.Role.DISTRIBUTOR, "BMDC", "02/545542", null, "Gulledelle 100", "Woluwe", "1200", "BE", "info@bmdc.be", "http://www.naturalsciences.be");
        List<IInstituteRole> distributors = new ArrayList();
        distributors.add(distributor);

        IInstituteRole author = new InstituteRole(IDataset.Role.AUTHOR, "MARECO", "02/545542", null, "Gulledelle 100", "Woluwe", "1200", "BE", "info@bmdc.be", "http://www.naturalsciences.be");
        IInstituteRole originator = new InstituteRole(IDataset.Role.ORIGINATOR, "VLIZ", "059/65466546", null, "Wandelaarkaai 7", "Oostende", "8400", "BE", "info@vliz.be", "http://www.vliz.be");
        List<IInstituteRole> parties = new ArrayList();
        parties.add(author);
        parties.add(originator);
        IDatasource dso = new Datasource("Advanced modelling and research on...", null, "bmdc:dso::2200", publicationDate1, "Degraer, Haelters, Reubens", parties, new HashSet(Arrays.asList("NL", "EN")), null, null, null, null);
        IDatasource dso2 = new Datasource("Final report on modelling and research on...", null, "bmdc:dso::2201", publicationDate2, "Degraer, Rumes, Fettweis", parties, new HashSet(Arrays.asList("EN")), null, null, null, null);
        List sources = new ArrayList();
        sources.add(dso);
        sources.add(dso2);
        DistributionFormat format = new DistributionFormat("geo-json", FormatEnum.JSON, "geo-json", null, null);
        DistributionFormat format2 = new DistributionFormat("NetCDF", FormatEnum.NETCDF, "NetCDF", "1.2", null);
        List<IDistributionFormat> formats = new ArrayList();
        formats.add(format);
        formats.add(format2);
        Set<IDistributionResource> resources = new HashSet();
        try {
            IInstituteRole BMDC_DISTRIBUTOR = new InstituteRole(IDataset.Role.DISTRIBUTOR, "BMDC", "02/773.21.44", null, "Gulledelle 100", "Sint-Lambrechts-Woluwe", "1200", "BE", "bmdc@naturalsciences.be", "http://bmdc.naturalsciences.be");

            DistributionResource rsr = new DistributionResource(OnlinePossibilityEnum.DOWNLOAD, new URL("http://dits.bmdc.be/dits/datasource/1287/20141030_0423231287-QUASH Step 1 1997.xls"), ProtocolEnum.WWW_LINK_10_http_link, "Data from the dataset in the format xx", "Here you can find variuous datasets", formats, new Double(1235463), BMDC_DISTRIBUTOR);
            resources.add(rsr);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MetadataBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Set<IDataset.InspireTheme> themes = new THashSet();
        //themes.add(IDataset.InspireTheme.OCEANOGRAPHIC_GEOGRAPHICAL_FEATURES);

        Dataset dst = new Dataset("bmdc:dst::5020", "Data from 'Advanced modelling and research on...'", creationDate, revisionDate, new HashSet(Arrays.asList("NL", "EN")), "4326", null, null, sources, "Dataset combined from two publications. Contains parameters on salinity, temperature, biota composition and mainly biodiversity. Gathered from 2005 to 2008. Belgica research vessel during cruises...", startDate, endDate, 50.0, 52.0, 2.0, 3.0, resources, "Dataset combined from sources. Descriptive staistical analysis performed.", themes, null, IDataset.SpatialType.VECTOR, createKeywords());

        return dst;
    }

    public static IKeyword k1 = new Keyword(0L, "http://www.eionet.europa.eu/gemet/concept/15228", "Marine Strategy Framework Directive", "theme", "GEMET - Concepts, version 4.1.2", new Date(), "4.1.2", "https://www.eionet.europa.eu/gemet/en/themes/");

    public static IKeyword k3 = new Keyword(0L, "https://www.eionet.europa.eu/gemet/en/inspire-theme/sr", "Sea regions", "theme", "GEMET - INSPIRE themes, version 1.0", new Date(), "1.0", "https://www.eionet.europa.eu/gemet/inspire-themes/");
    public static IKeyword k4 = new Keyword(0L, "http://cdr.eionet.europa.eu/help/msfd/Schemas/MSFDCommon_2018.xsd#D11C1", "MD11C1 - Anthropogenic impulsive sound", "theme", "Eionet Central Data Repository", new Date(), null, "http://cdr.eionet.europa.eu/help/msfd/Schemas/MSFDCommon_2018.xsd");

    public static IKeyword k5 = new Keyword(0L, "http://marineregions.org/mrgid/26567", "Belgian part of the North Sea", "place", "Marine Regions", new Date(), "10", "http://marineregions.org");
    public static IKeyword k6 = new Keyword(0L, "http://marineregions.org/mrgid/50155", "Greater North Sea, incl. the Kattegat and the English Channel", "place", "Marine Regions", new Date(), "10", "http://marineregions.org");

    private static Set<IKeyword> createKeywords() {
        Set<IKeyword> keywords = new HashSet<>();
        keywords.add(k1);
        keywords.add(k3);
        keywords.add(k4);
        keywords.add(k5);
        keywords.add(k6);
        return keywords;
    }

    private static String getKeywordXML(IKeyword keyword) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return ("<gmd:keyword>"
                + "            <gmx:Anchor xlink:href=\"" + keyword.getUrl() + "\">" + keyword.getPrefLabel() + "</gmx:Anchor>"
                + "          </gmd:keyword>"
                + "          <gmd:type>"
                + "            <gmd:MD_KeywordTypeCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_KeywordTypeCode\" codeListValue=\"" + keyword.getType() + "\">" + keyword.getType().substring(0, 1).toUpperCase() + keyword.getType().substring(1).toLowerCase() + "</gmd:MD_KeywordTypeCode>\n"
                + "          </gmd:type>"
                + "          <gmd:thesaurusName>"
                + "            <gmd:CI_Citation>"
                + "              <gmd:title>"
                + "                <gco:CharacterString>" + keyword.getThesaurusTitle() + "</gco:CharacterString>"
                + "              </gmd:title>"
                + "              <gmd:date>"
                + "                <gmd:CI_Date>"
                + "                  <gmd:date>"
                + "                    <gco:DateTime>" + df.format(keyword.getThesaurusPublicationDate()) + "</gco:DateTime>"
                + "                  </gmd:date>"
                + "                  <gmd:dateType>"
                + "                    <gmd:CI_DateTypeCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_DateTypeCode\" codeListValue=\"publication\">Publication</gmd:CI_DateTypeCode>"
                + "                  </gmd:dateType>"
                + "                </gmd:CI_Date>"
                + "              </gmd:date>"
                + (keyword.getThesaurusVersion() != null
                ? "              <gmd:edition>"
                + "                <gco:CharacterString>" + keyword.getThesaurusVersion() + "</gco:CharacterString>"
                + "              </gmd:edition>" : "")
                + "            </gmd:CI_Citation>"
                + "          </gmd:thesaurusName>").replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "");
    }

    public static Dataset createFakeDataset2() {
        Date publicationDate1 = new GregorianCalendar(2008, Calendar.DECEMBER, 10).getTime();
        Date publicationDate2 = new GregorianCalendar(2009, Calendar.JANUARY, 10).getTime();

        Date creationDate = new GregorianCalendar(2016, Calendar.DECEMBER, 1).getTime();
        Date revisionDate = new GregorianCalendar(2016, Calendar.DECEMBER, 10).getTime();

        Date startDate = new GregorianCalendar(2005, Calendar.JANUARY, 1).getTime();
        Date endDate = new GregorianCalendar(2008, Calendar.JANUARY, 1).getTime();

        IInstituteRole author = new InstituteRole(IDataset.Role.AUTHOR, "MARECO", "02/545542", null, "Gulledelle 100", "Woluwe", "1200", "BE", "info@bmdc.be", "http://www.naturalsciences.be");
        IInstituteRole originator = new InstituteRole(IDataset.Role.ORIGINATOR, "VLIZ", "059/65466546", null, "Wandelaarkaai 7", "Oostende", "8400", "BE", "info@vliz.be", "http://www.vliz.be");
        List<IInstituteRole> parties = new ArrayList();
        parties.add(author);
        parties.add(originator);
        IDatasource dso = new Datasource("Advanced modelling and research on...", null, "bmdc:dso::2200", publicationDate1, "Degraer, Haelters, Reubens", parties, new HashSet(Arrays.asList("NL", "EN")), null, null, null, null);
        IDatasource dso2 = new Datasource("Final report on modelling and research on...", null, "bmdc:dso::2201", publicationDate2, "Degraer, Rumes, Fettweis", parties, new HashSet(Arrays.asList("EN")), null, null, null, null);
        List sources = new ArrayList();
        sources.add(dso);
        sources.add(dso2);

        Set<IDataset.InspireTheme> themes = new THashSet();
        //themes.add(IDataset.InspireTheme.OCEANOGRAPHIC_GEOGRAPHICAL_FEATURES);
        Dataset dst = new Dataset("bmdc:dst::5021", "Data from 'Advanced modelling and research on...'", creationDate, revisionDate, new HashSet(Arrays.asList("NL", "EN")), "4326", null, null, sources, "Dataset combined from two publications. Contains parameters on salinity, temperature, biota composition and mainly biodiversity. Gathered from 2005 to 2008. Belgica research vessel during cruises...", startDate, endDate, 50.0, 52.0, 2.0, 3.0, null, "Dataset combined from sources. Descriptive staistical analysis performed.", themes, null, IDataset.SpatialType.TEXT_TABLE, createKeywords());
        return dst;
    }

    /**
     * Test of buildMetadata method, of class ISO19115DatasetBuilder.
     */
    @Test
    public void testBuildMetadata() throws IOException {
        System.out.println("buildMetadata");
        Dataset ds = createFakeDataset1();
        //testXML(ds);
        testNoAnchor(ds);
        testAnchor(ds);
    }

    /**
     * Test of buildMetadata method, of class ISO19115DatasetBuilder.
     */
    @Test
    public void testBuildMetadata2() throws IOException {
        System.out.println("buildMetadata2");
        Dataset ds = createFakeDataset2();
        testXML(ds);
        testNoAnchor(ds);
        testAnchor(ds);
    }

    private void testXML(IDataset dst) throws IOException {
        ISO19115DatasetBuilder instance = new ISO19115DatasetBuilder(dst);
        DefaultMetadata metadata = instance.buildMetadata(false, true, null);
        String xml = instance.createXMLString(metadata,Arrays.asList(new String[]{"en", "nl", "fr", "de"}));
        System.out.println(xml);
        assertTrue(xml.contains("xml version=\"1.0\""));
        assertTrue(xml.contains("MD_Metadata"));
        assertTrue(xml.contains("<gco:Decimal>50.0</gco:Decimal>"));
        assertTrue(xml.contains("<gco:Decimal>52.0</gco:Decimal>"));
        assertTrue(xml.contains("<gco:Decimal>2.0</gco:Decimal>"));
        assertTrue(xml.contains("<gco:Decimal>3.0</gco:Decimal>"));
        //assertTrue(xml.contains("<gco:CharacterString>Oceanographic geographical features</gco:CharacterString>"));
        assertTrue(xml.contains("<gmd:MD_SpatialRepresentationTypeCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_SpatialRepresentationTypeCode\" codeListValue=\"textTable\">Text table</gmd:MD_SpatialRepresentationTypeCode>"));
        System.out.println("------------------------------------------");
        System.out.println(getKeywordXML(k3));

        //System.out.println(xml.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", ""));
        assertTrue(xml.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "").contains(getKeywordXML(k1)));
        assertTrue(xml.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "").contains(getKeywordXML(k3)));
        assertTrue(xml.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "").contains(getKeywordXML(k4)));
    }

    private void testNoAnchor(IDataset dst) throws IOException {
        ISO19115DatasetBuilder instance = new ISO19115DatasetBuilder(dst);
        DefaultMetadata metadata = instance.buildMetadata(false, false, null);
        File innerTestFile = innerTest(instance, metadata);
        boolean foundAnchor = FileUtils.findStringInFile(innerTestFile.toPath(), "gmx:Anchor");
        assertFalse(foundAnchor);
    }

    private void testAnchor(IDataset dst) throws IOException {
        ISO19115DatasetBuilder instance = new ISO19115DatasetBuilder(dst);
        DefaultMetadata metadata = instance.buildMetadata(false, true, null);
        File innerTestFile = innerTest(instance, metadata);
        boolean foundAnchor = FileUtils.findStringInFile(innerTestFile.toPath(), "gmx:Anchor");
        assertTrue(foundAnchor);
    }

    private File innerTest(ISO19115DatasetBuilder instance, DefaultMetadata metadata) throws IOException {
        String buildMetadataFileName = instance.buildMetadataFileName();

        File file = File.createTempFile(buildMetadataFileName, ".xml");
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Test file stored at " + file.getPath());
        try {
            instance.createFile(file, metadata,Arrays.asList(new String[]{"en", "nl", "fr", "de"}));
        } catch (FileNotFoundException ex) {
            fail();
        }
        return file;
    }

}
