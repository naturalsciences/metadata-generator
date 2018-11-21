/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.iso;

import be.naturalsciences.bmdc.metadata.model.OnlinePossibilityEnum;
import be.naturalsciences.bmdc.metadata.model.IDataset;
import be.naturalsciences.bmdc.metadata.model.IRegion;
import be.naturalsciences.bmdc.metadata.model.IDistributionFormat;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import be.naturalsciences.bmdc.metadata.model.IDatasource;
import be.naturalsciences.bmdc.metadata.model.IKeyword;
import be.naturalsciences.bmdc.metadata.model.IDistributionResource;
import be.naturalsciences.bmdc.metadata.model.comparator.DatasourceComparator;
import be.naturalsciences.bmdc.utils.FileUtils;
import be.naturalsciences.bmdc.utils.StringUtils;
import be.naturalsciences.bmdc.utils.XMLUtils;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import static java.util.Collections.singleton;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBException;
import org.apache.sis.internal.jaxb.gmx.Anchor;
import org.apache.sis.internal.jaxb.metadata.replace.ReferenceSystemMetadata;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.DefaultMetadataScope;
import org.apache.sis.metadata.iso.ImmutableIdentifier;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.citation.DefaultAddress;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.citation.DefaultCitationDate;
import org.apache.sis.metadata.iso.citation.DefaultContact;
import org.apache.sis.metadata.iso.citation.DefaultOnlineResource;
import org.apache.sis.metadata.iso.citation.DefaultResponsibleParty;
import org.apache.sis.metadata.iso.citation.DefaultTelephone;
import org.apache.sis.metadata.iso.constraint.DefaultLegalConstraints;
import org.apache.sis.metadata.iso.constraint.DefaultSecurityConstraints;
import org.apache.sis.metadata.iso.distribution.DefaultDigitalTransferOptions;
import org.apache.sis.metadata.iso.distribution.DefaultDistribution;
import org.apache.sis.metadata.iso.distribution.DefaultDistributor;
import org.apache.sis.metadata.iso.distribution.DefaultFormat;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.metadata.iso.extent.DefaultGeographicDescription;
import org.apache.sis.metadata.iso.extent.DefaultTemporalExtent;
import org.apache.sis.metadata.iso.identification.DefaultAggregateInformation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.metadata.iso.identification.DefaultKeywords;
import org.apache.sis.metadata.iso.lineage.DefaultLineage;
import org.apache.sis.metadata.iso.quality.DefaultConformanceResult;
import org.apache.sis.metadata.iso.quality.DefaultDataQuality;
import org.apache.sis.metadata.iso.quality.DefaultDomainConsistency;
import org.apache.sis.metadata.iso.quality.DefaultScope;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.xml.NilReason;
import org.apache.sis.xml.XML;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Role;
//import org.opengis.metadata.citation.TelephoneType;
import org.opengis.metadata.constraint.Classification;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.distribution.Distribution;
import org.opengis.metadata.distribution.Format;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.identification.AssociationType;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.InitiativeType;
import org.opengis.metadata.identification.KeywordType;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.quality.Scope;
import org.opengis.metadata.spatial.SpatialRepresentationType;
//import org.opengis.temporal.Period;
import org.opengis.util.InternationalString;

/**
 *
 * @author thomas
 */
public class ISO19115DatasetBuilder {

    private static final Map<String, String> countries;
    private static final Map<String, List<Locale>> languages;
    private static final Map<String, String> localeMap;
    public static final Map<IDataset.Role, Role> roles;
    public static final Map<OnlinePossibilityEnum, OnLineFunction> onLineFunctions;
    private static final Map<IDataset.InspireTheme, Keywords> inspireThemes;
    private static Set<String> keywordLanguages = new HashSet();

    public static DefaultMetadataScope DATASET_SCOPE = new DefaultMetadataScope(ScopeCode.DATASET, null);
    public static Scope DATASET_SCOPE_QUALITY = new DefaultScope(ScopeCode.DATASET);
    public static Date INSPIRE_IMPLEMENTING_RULES_PUBLICATION_DATE = new GregorianCalendar(2010, Calendar.DECEMBER, 8).getTime();
    public static Date GEMET_INSPIRE_THEMES_PUBLICATION_DATE = new GregorianCalendar(2008, Calendar.JUNE, 1).getTime();
    public static Date MARINE_REGIONS_PUBLICATION_DATE = new GregorianCalendar(2018, Calendar.FEBRUARY, 21).getTime();

    public static File INSPIRE_RDF_DIR;

    public static String LINE_SEPARATOR = System.lineSeparator();

    //   public static final Set<Locale> ENGLISH;
    private IDataset dataset;
    private boolean useAnchor;
    private boolean inspire;

    public IDataset getDataset() {
        return dataset;
    }

    public void setDataset(IDataset dataset) {
        this.dataset = dataset;
    }

    public enum location {
        BPNS
    }

    public static final Map<IDataset.Role, org.opengis.metadata.citation.Role> ROLE_MAPPING;

    public static final Map<IDataset.SpatialType, SpatialRepresentationType> SPATIAL_TYPE_MAPPING;

    public static final Map<String, KeywordType> KEYWORD_TYPE_MAPPING;

    static {

        ROLE_MAPPING = new HashMap();
        ROLE_MAPPING.put(IDataset.Role.DISTRIBUTOR, org.opengis.metadata.citation.Role.DISTRIBUTOR);
        ROLE_MAPPING.put(IDataset.Role.RESOURCEPROVIDER, org.opengis.metadata.citation.Role.RESOURCE_PROVIDER);
        ROLE_MAPPING.put(IDataset.Role.OWNER, org.opengis.metadata.citation.Role.OWNER);
        ROLE_MAPPING.put(IDataset.Role.USER, org.opengis.metadata.citation.Role.USER);
        ROLE_MAPPING.put(IDataset.Role.PUBLISHER, org.opengis.metadata.citation.Role.PUBLISHER);
        ROLE_MAPPING.put(IDataset.Role.AUTHOR, org.opengis.metadata.citation.Role.AUTHOR);
        ROLE_MAPPING.put(IDataset.Role.POINT_OF_CONTACT, org.opengis.metadata.citation.Role.POINT_OF_CONTACT);
        ROLE_MAPPING.put(IDataset.Role.PRINCIPAL_INVESTIGATOR, org.opengis.metadata.citation.Role.PRINCIPAL_INVESTIGATOR);
        ROLE_MAPPING.put(IDataset.Role.PROCESSOR, org.opengis.metadata.citation.Role.PROCESSOR);
        ROLE_MAPPING.put(IDataset.Role.ORIGINATOR, org.opengis.metadata.citation.Role.ORIGINATOR);
        ROLE_MAPPING.put(IDataset.Role.CUSTODIAN, org.opengis.metadata.citation.Role.CUSTODIAN);

        SPATIAL_TYPE_MAPPING = new HashMap();
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.GRID, SpatialRepresentationType.GRID);
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.STEREO_MODEL, SpatialRepresentationType.STEREO_MODEL);
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.TEXT_TABLE, SpatialRepresentationType.TEXT_TABLE);
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.TIN, SpatialRepresentationType.TIN);
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.VECTOR, SpatialRepresentationType.VECTOR);
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.VIDEO, SpatialRepresentationType.VIDEO);

        KEYWORD_TYPE_MAPPING = new HashMap();
        KEYWORD_TYPE_MAPPING.put("discipline", KeywordType.DISCIPLINE);
        KEYWORD_TYPE_MAPPING.put("place", KeywordType.PLACE);
        KEYWORD_TYPE_MAPPING.put("stratum", KeywordType.STRATUM);
        KEYWORD_TYPE_MAPPING.put("temporal", KeywordType.TEMPORAL);
        KEYWORD_TYPE_MAPPING.put("theme", KeywordType.THEME);

        countries = new THashMap();
        countries.put("99", "Unknown");
        countries.put("9X", "inapplicable");
        countries.put("AD", "Andorra");
        countries.put("AE", "United Arab Emirates");
        countries.put("AF", "Afghanistan");
        countries.put("AG", "Antigua and Barbuda");
        countries.put("AI", "Anguilla");
        countries.put("AL", "Albania");
        countries.put("AM", "Armenia");
        countries.put("AO", "Angola");
        countries.put("AQ", "Antarctica");
        countries.put("AR", "Argentina");
        countries.put("AS", "American Samoa");
        countries.put("AT", "Austria");
        countries.put("AU", "Australia");
        countries.put("AW", "Aruba");
        countries.put("AX", "Åland Islands");
        countries.put("AZ", "Azerbaijan");
        countries.put("BA", "Bosnia and Herzegovina");
        countries.put("BB", "Barbados");
        countries.put("BD", "Bangladesh");
        countries.put("BE", "Belgium");
        countries.put("BF", "Burkina Faso");
        countries.put("BG", "Bulgaria");
        countries.put("BH", "Bahrain");
        countries.put("BI", "Burundi");
        countries.put("BJ", "Benin");
        countries.put("BL", "Saint Barthélemy");
        countries.put("BM", "Bermuda");
        countries.put("BN", "Brunei Darussalam");
        countries.put("BO", "Bolivia, Plurinational State of");
        countries.put("BQ", "Bonaire, Sint Eustatius and Saba");
        countries.put("BR", "Brazil");
        countries.put("BS", "Bahamas");
        countries.put("BT", "Bhutan");
        countries.put("BV", "Bouvet Island");
        countries.put("BW", "Botswana");
        countries.put("BY", "Belarus");
        countries.put("BZ", "Belize");
        countries.put("CA", "Canada");
        countries.put("CC", "Cocos (Keeling) Islands");
        countries.put("CD", "Congo, The Democratic Republic of the");
        countries.put("CF", "Central African Republic");
        countries.put("CG", "Congo");
        countries.put("CH", "Switzerland");
        countries.put("CI", "Côte d'Ivoire");
        countries.put("CK", "Cook Islands");
        countries.put("CL", "Chile");
        countries.put("CM", "Cameroon");
        countries.put("CN", "China");
        countries.put("CO", "Colombia");
        countries.put("CR", "Costa Rica");
        countries.put("CU", "Cuba");
        countries.put("CV", "Cape Verde");
        countries.put("CW", "Curaçao");
        countries.put("CX", "Christmas Island");
        countries.put("CY", "Cyprus");
        countries.put("CZ", "Czech Republic");
        countries.put("DE", "Germany");
        countries.put("DJ", "Djibouti");
        countries.put("DK", "Denmark");
        countries.put("DM", "Dominica");
        countries.put("DO", "Dominican Republic");
        countries.put("DZ", "Algeria");
        countries.put("EC", "Ecuador");
        countries.put("EE", "Estonia");
        countries.put("EG", "Egypt");
        countries.put("EH", "Western Sahara");
        countries.put("ER", "Eritrea");
        countries.put("ES", "Spain");
        countries.put("ET", "Ethiopia");
        countries.put("FI", "Finland");
        countries.put("FJ", "Fiji");
        countries.put("FK", "Falkland Islands (Malvinas)");
        countries.put("FM", "Micronesia, Federated States of");
        countries.put("FO", "Faroe Islands");
        countries.put("FR", "France");
        countries.put("GA", "Gabon");
        countries.put("GB", "United Kingdom");
        countries.put("GD", "Grenada");
        countries.put("GE", "Georgia");
        countries.put("GF", "French Guiana");
        countries.put("GG", "Guernsey");
        countries.put("GH", "Ghana");
        countries.put("GI", "Gibraltar");
        countries.put("GL", "Greenland");
        countries.put("GM", "Gambia");
        countries.put("GN", "Guinea");
        countries.put("GP", "Guadeloupe");
        countries.put("GQ", "Equatorial Guinea");
        countries.put("GR", "Greece");
        countries.put("GS", "South Georgia and the South Sandwich Islands");
        countries.put("GT", "Guatemala");
        countries.put("GU", "Guam");
        countries.put("GW", "Guinea-Bissau");
        countries.put("GY", "Guyana");
        countries.put("HK", "Hong Kong");
        countries.put("HM", "Heard and McDonald Islands");
        countries.put("HN", "Honduras");
        countries.put("HR", "Croatia");
        countries.put("HT", "Haiti");
        countries.put("HU", "Hungary");
        countries.put("ID", "Indonesia");
        countries.put("IE", "Ireland");
        countries.put("IL", "Israel");
        countries.put("IM", "Isle Of Man");
        countries.put("IN", "India");
        countries.put("IO", "British Indian Ocean Territory");
        countries.put("IQ", "Iraq");
        countries.put("IR", "Iran, Islamic Republic of");
        countries.put("IS", "Iceland");
        countries.put("IT", "Italy");
        countries.put("JE", "Jersey");
        countries.put("JM", "Jamaica");
        countries.put("JO", "Jordan");
        countries.put("JP", "Japan");
        countries.put("KE", "Kenya");
        countries.put("KG", "Kyrgyzstan");
        countries.put("KH", "Cambodia");
        countries.put("KI", "Kiribati");
        countries.put("KM", "Comoros");
        countries.put("KN", "Saint Kitts and Nevis");
        countries.put("KP", "Korea, Democratic People's Republic of");
        countries.put("KR", "Korea, Republic of");
        countries.put("KW", "Kuwait");
        countries.put("KY", "Cayman Islands");
        countries.put("KZ", "Kazakhstan");
        countries.put("LA", "Lao People's Democratic Republic");
        countries.put("LB", "Lebanon");
        countries.put("LC", "Saint Lucia");
        countries.put("LI", "Liechtenstein");
        countries.put("LK", "Sri Lanka");
        countries.put("LR", "Liberia");
        countries.put("LS", "Lesotho");
        countries.put("LT", "Lithuania");
        countries.put("LU", "Luxembourg");
        countries.put("LV", "Latvia");
        countries.put("LY", "Libya");
        countries.put("MA", "Morocco");
        countries.put("MC", "Monaco");
        countries.put("MD", "Moldova, Republic of");
        countries.put("ME", "Montenegro");
        countries.put("MF", "Saint Martin (French part)");
        countries.put("MG", "Madagascar");
        countries.put("MH", "Marshall Islands");
        countries.put("MK", "Macedonia, The Former Yugoslav Republic of");
        countries.put("ML", "Mali");
        countries.put("MM", "Myanmar");
        countries.put("MN", "Mongolia");
        countries.put("MO", "Macau");
        countries.put("MP", "Northern Mariana Islands");
        countries.put("MQ", "Martinique");
        countries.put("MR", "Mauritania");
        countries.put("MS", "Montserrat");
        countries.put("MT", "Malta");
        countries.put("MU", "Mauritius");
        countries.put("MV", "Maldives");
        countries.put("MW", "Malawi");
        countries.put("MX", "Mexico");
        countries.put("MY", "Malaysia");
        countries.put("MZ", "Mozambique");
        countries.put("NA", "Namibia");
        countries.put("NC", "New Caledonia");
        countries.put("NE", "Niger");
        countries.put("NF", "Norfolk Island");
        countries.put("NG", "Nigeria");
        countries.put("NI", "Nicaragua");
        countries.put("NL", "Netherlands");
        countries.put("NO", "Norway");
        countries.put("NP", "Nepal");
        countries.put("NR", "Nauru");
        countries.put("NU", "Niue");
        countries.put("NZ", "New Zealand");
        countries.put("OM", "Oman");
        countries.put("PA", "Panama");
        countries.put("PE", "Peru");
        countries.put("PF", "French Polynesia");
        countries.put("PG", "Papua New Guinea");
        countries.put("PH", "Philippines");
        countries.put("PK", "Pakistan");
        countries.put("PL", "Poland");
        countries.put("PM", "Saint Pierre and Miquelon");
        countries.put("PN", "Pitcairn");
        countries.put("PR", "Puerto Rico");
        countries.put("PS", "Palestinian Territory, Occupied");
        countries.put("PT", "Portugal");
        countries.put("PW", "Palau");
        countries.put("PY", "Paraguay");
        countries.put("QA", "Qatar");
        countries.put("RE", "Réunion");
        countries.put("RO", "Romania");
        countries.put("RS", "Serbia");
        countries.put("RU", "Russian Federation");
        countries.put("RW", "Rwanda");
        countries.put("SA", "Saudi Arabia");
        countries.put("SB", "Solomon Islands");
        countries.put("SC", "Seychelles");
        countries.put("SD", "Sudan");
        countries.put("SE", "Sweden");
        countries.put("SG", "Singapore");
        countries.put("SH", "Saint Helena, Ascension and Tristan da Cunha");
        countries.put("SI", "Slovenia");
        countries.put("SJ", "Svalbard and Jan Mayen");
        countries.put("SK", "Slovakia");
        countries.put("SL", "Sierra Leone");
        countries.put("SM", "San Marino");
        countries.put("SN", "Senegal");
        countries.put("SO", "Somalia");
        countries.put("SR", "Suriname");
        countries.put("SS", "South Sudan");
        countries.put("ST", "Sao Tome and Principe");
        countries.put("SV", "El Salvador");
        countries.put("SX", "Sint Maarten (Dutch part)");
        countries.put("SY", "Syrian Arab Republic");
        countries.put("SZ", "Swaziland");
        countries.put("TC", "Turks and Caicos Islands");
        countries.put("TD", "Chad");
        countries.put("TF", "French Southern Territories");
        countries.put("TG", "Togo");
        countries.put("TH", "Thailand");
        countries.put("TJ", "Tajikistan");
        countries.put("TK", "Tokelau");
        countries.put("TL", "Timor-Leste");
        countries.put("TM", "Turkmenistan");
        countries.put("TN", "Tunisia");
        countries.put("TO", "Tonga");
        countries.put("TR", "Turkey");
        countries.put("TT", "Trinidad and Tobago");
        countries.put("TV", "Tuvalu");
        countries.put("TW", "Taiwan, Province of China");
        countries.put("TZ", "Tanzania, United Republic of");
        countries.put("UA", "Ukraine");
        countries.put("UG", "Uganda");
        countries.put("UM", "United States Minor Outlying Islands");
        countries.put("US", "United States");
        countries.put("UY", "Uruguay");
        countries.put("UZ", "Uzbekistan");
        countries.put("VA", "Holy See (Vatican City State)");
        countries.put("VC", "Saint Vincent and The Grenadines");
        countries.put("VE", "Venezuela, Bolivarian Republic of");
        countries.put("VG", "Virgin Islands, British");
        countries.put("VI", "Virgin Islands, U.S.");
        countries.put("VN", "Viet Nam");
        countries.put("VU", "Vanuatu");
        countries.put("WF", "Wallis and Futuna");
        countries.put("WS", "Samoa");
        countries.put("YE", "Yemen");
        countries.put("YT", "Mayotte");
        countries.put("ZA", "South Africa");
        countries.put("ZM", "Zambia");
        countries.put("ZW", "Zimbabwe");

        languages = new THashMap();
        // languages.put("NL", Arrays.asList(new Locale("NL","nld")));
        languages.put("FR", Arrays.asList(Locale.FRANCE));
        languages.put("EN", Arrays.asList(Locale.ENGLISH));
        languages.put("DE", Arrays.asList(Locale.GERMANY));

        localeMap = new HashMap<>();
        localeMap.put("FR", "<gmd:locale>\n"
                + "    <gmd:PT_Locale id=\"FR\">\n"
                + "      <gmd:languageCode>\n"
                + "        <gmd:LanguageCode codeList=\"http://www.loc.gov/standards/iso639-2/\" codeListValue=\"fre\" />\n"
                + "      </gmd:languageCode>\n"
                + "      <gmd:characterEncoding />\n"
                + "    </gmd:PT_Locale>\n"
                + "  </gmd:locale>");
        localeMap.put("NL", "<gmd:locale>\n"
                + "    <gmd:PT_Locale id=\"NL\">\n"
                + "      <gmd:languageCode>\n"
                + "        <gmd:LanguageCode codeList=\"http://www.loc.gov/standards/iso639-2/\" codeListValue=\"dut\" />\n"
                + "      </gmd:languageCode>\n"
                + "      <gmd:characterEncoding />\n"
                + "    </gmd:PT_Locale>\n"
                + "  </gmd:locale>");
        localeMap.put("DE", "<gmd:locale>\n"
                + "    <gmd:PT_Locale id=\"DE\">\n"
                + "      <gmd:languageCode>\n"
                + "        <gmd:LanguageCode codeList=\"http://www.loc.gov/standards/iso639-2/\" codeListValue=\"ger\" />\n"
                + "      </gmd:languageCode>\n"
                + "      <gmd:characterEncoding />\n"
                + "    </gmd:PT_Locale>\n"
                + "  </gmd:locale>");
        localeMap.put("EN", "<gmd:locale>\n"
                + "    <gmd:PT_Locale id=\"EN\">\n"
                + "      <gmd:languageCode>\n"
                + "        <gmd:LanguageCode codeList=\"http://www.loc.gov/standards/iso639-2/\" codeListValue=\"eng\" />\n"
                + "      </gmd:languageCode>\n"
                + "      <gmd:characterEncoding />\n"
                + "    </gmd:PT_Locale>\n"
                + "  </gmd:locale>");
        // ENGLISH = new HashSet<>();
        // ENGLISH.addAll(languages.get("EN"));

        /*languages.put("NL/FR", Arrays.asList(new Locale("nld"), Locale.FRENCH));
        languages.put("NL/FR/EN", Arrays.asList(new Locale("nld"), Locale.FRENCH, Locale.ENGLISH));
        languages.put("FR/EN", Arrays.asList(Locale.FRENCH, Locale.ENGLISH));*/
        roles = new THashMap<>();
        roles.put(IDataset.Role.RESOURCEPROVIDER, Role.RESOURCE_PROVIDER);
        roles.put(IDataset.Role.PUBLISHER, Role.PUBLISHER);
        roles.put(IDataset.Role.AUTHOR, Role.AUTHOR);
        roles.put(IDataset.Role.CUSTODIAN, Role.CUSTODIAN);
        roles.put(IDataset.Role.OWNER, Role.OWNER);
        roles.put(IDataset.Role.USER, Role.USER);
        roles.put(IDataset.Role.DISTRIBUTOR, Role.DISTRIBUTOR);
        roles.put(IDataset.Role.ORIGINATOR, Role.ORIGINATOR);
        roles.put(IDataset.Role.POINT_OF_CONTACT, Role.POINT_OF_CONTACT);
        roles.put(IDataset.Role.PRINCIPAL_INVESTIGATOR, Role.PRINCIPAL_INVESTIGATOR);
        roles.put(IDataset.Role.PROCESSOR, Role.PROCESSOR);

        onLineFunctions = new THashMap<>();
        onLineFunctions.put(OnlinePossibilityEnum.DOWNLOAD, OnLineFunction.DOWNLOAD);
        onLineFunctions.put(OnlinePossibilityEnum.INFORMATION, OnLineFunction.INFORMATION);
        onLineFunctions.put(OnlinePossibilityEnum.OFFLINE_ACCESS, OnLineFunction.OFFLINE_ACCESS);
        onLineFunctions.put(OnlinePossibilityEnum.ORDER, OnLineFunction.ORDER);
        onLineFunctions.put(OnlinePossibilityEnum.SEARCH, OnLineFunction.SEARCH);

        inspireThemes = new THashMap<>();

        String tmp = System.getProperty("java.io.tmpdir");
        INSPIRE_RDF_DIR = new File(tmp, "inspire-rdf");
        if (!Files.exists(INSPIRE_RDF_DIR.toPath())) {
            try {
                Files.createDirectories(INSPIRE_RDF_DIR.toPath());
            } catch (IOException e) {
                Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.SEVERE, "Couldn't create directory.", e);
            }
        }
        URL themeRdfUrl;

        try {
            for (String keywordLanguage : getKeywordLanguages()) {
                String fileName = "theme." + keywordLanguage + ".rdf";
                themeRdfUrl = new URL("http://inspire.ec.europa.eu/theme/" + fileName);
                FileUtils.copyURLToFile(themeRdfUrl, new File(INSPIRE_RDF_DIR, fileName));
            }
        } catch (IOException ex) {
            Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Set<String> getKeywordLanguages() {
        if (keywordLanguages == null || keywordLanguages.isEmpty()) {
            InputStream input = null;

            Properties prop = new Properties();
            try {

                input = ISO19115DatasetBuilder.class.getClassLoader().getResourceAsStream("config.properties");

                prop.load(input);

                keywordLanguages = new HashSet<String>(Arrays.asList(prop.getProperty("metadata.iso.inspire.keyword.languages").split(",")));
                return keywordLanguages;
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

    public ISO19115DatasetBuilder() {
        inspireThemes.put(IDataset.InspireTheme.ADDRESSES, buildKeyword(Arrays.asList(new String[]{"Addresses"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.ADMINISTRATIVE_UNITS, buildKeyword(Arrays.asList(new String[]{"Administrative units"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.AGRICULTURAL_AND_AQUACULTURE_FACILITIES, buildKeyword(Arrays.asList(new String[]{"Agricultural and aquaculture facilities"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.AREA_MANAGEMENT_RESTRICTION_REGULATION_ZONES_AND_REPORTING_UNITS, buildKeyword(Arrays.asList(new String[]{"Area management/restriction/regulation zones and reporting units"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.ATMOSPHERIC_CONDITIONS, buildKeyword(Arrays.asList(new String[]{"Atmospheric conditions"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.BIO_GEOGRAPHICAL_REGIONS, buildKeyword(Arrays.asList(new String[]{"Bio-geographical regions"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.BUILDINGS, buildKeyword(Arrays.asList(new String[]{"Buildings"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.CADASTRAL_PARCELS, buildKeyword(Arrays.asList(new String[]{"Cadastral parcels"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.COORDINATE_REFERENCE_SYSTEMS, buildKeyword(Arrays.asList(new String[]{"Coordinate reference systems"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.ELEVATION, buildKeyword(Arrays.asList(new String[]{"Elevation"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.ENERGY_RESOURCES, buildKeyword(Arrays.asList(new String[]{"Energy resources"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.ENVIRONMENTAL_MONITORING_FACILITIES, buildKeyword(Arrays.asList(new String[]{"Environmental monitoring facilities"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.GEOGRAPHICAL_GRID_SYSTEMS, buildKeyword(Arrays.asList(new String[]{"Geographical grid systems"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.GEOGRAPHICAL_NAMES, buildKeyword(Arrays.asList(new String[]{"Geographical names"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.GEOLOGY, buildKeyword(Arrays.asList(new String[]{"Geology"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.HABITATS_AND_BIOTOPES, buildKeyword(Arrays.asList(new String[]{"Habitats and biotopes"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.HUMAN_HEALTH_AND_SAFETY, buildKeyword(Arrays.asList(new String[]{"Human health and safety"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.HYDROGRAPHY, buildKeyword(Arrays.asList(new String[]{"Hydrography"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.LAND_COVER, buildKeyword(Arrays.asList(new String[]{"Land cover"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.LAND_USE, buildKeyword(Arrays.asList(new String[]{"Land use"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.METEOROLOGICAL_GEOGRAPHICAL_FEATURES, buildKeyword(Arrays.asList(new String[]{"Meteorological geographical features"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.MINERAL_RESOURCES, buildKeyword(Arrays.asList(new String[]{"Mineral resources"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.NATURAL_RISK_ZONES, buildKeyword(Arrays.asList(new String[]{"Natural risk zones"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.OCEANOGRAPHIC_GEOGRAPHICAL_FEATURES, buildKeyword(Arrays.asList(new String[]{"Oceanographic geographical features"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.ORTHOIMAGERY, buildKeyword(Arrays.asList(new String[]{"Orthoimagery"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.POPULATION_DISTRIBUTION_DEMOGRAPHY, buildKeyword(Arrays.asList(new String[]{"Population distribution — demography"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.PRODUCTION_AND_INDUSTRIAL_FACILITIES, buildKeyword(Arrays.asList(new String[]{"Production and industrial facilities"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.PROTECTED_SITES, buildKeyword(Arrays.asList(new String[]{"Protected sites"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.SEA_REGIONS, buildKeyword(Arrays.asList(new String[]{"Sea regions"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.SOIL, buildKeyword(Arrays.asList(new String[]{"Soil"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.SPECIES_DISTRIBUTION, buildKeyword(Arrays.asList(new String[]{"Species distribution"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.STATISTICAL_UNITS, buildKeyword(Arrays.asList(new String[]{"Statistical units"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.TRANSPORT_NETWORKS, buildKeyword(Arrays.asList(new String[]{"Transport networks"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        inspireThemes.put(IDataset.InspireTheme.UTILITY_AND_GOVERNMENTAL_SERVICES, buildKeyword(Arrays.asList(new String[]{"Utility and governmental services"}), null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));

    }

    /**
     * *
     * Create a metadata builder based for a given dataset.
     *
     * @param dataset
     */
    public ISO19115DatasetBuilder(IDataset dataset) {
        this();
        this.dataset = dataset;
    }

    private CitationDate buildRevisionDate(Date date) {
        DefaultCitationDate revisionDate = new DefaultCitationDate(date, DateType.REVISION);
        return revisionDate;
    }

    private CitationDate buildCreationDate(Date date) {
        CitationDate citationDate;
        if (date != null) {
            citationDate = new DefaultCitationDate(date, DateType.CREATION);
        } else {
            citationDate = NilReason.MISSING.createNilObject(CitationDate.class);
        }
        return citationDate;
    }

    private CitationDate buildPublicationDate(Date date) {
        CitationDate citationDate;
        if (date != null) {
            citationDate = new DefaultCitationDate(date, DateType.PUBLICATION);
        } else {
            citationDate = NilReason.MISSING.createNilObject(CitationDate.class);
        }
        return citationDate;
    }

    private String buildAnchorString(String identifier, String name) {
        if (name != null) {
            return "<gmx:Anchor xlink:href=\"" + identifier + "\">" + name + "</gmx:Anchor>";
        } else {
            return "<gmx:Anchor xlink:href=\"" + identifier + "\"/>";
        }
    }

    private Anchor buildAnchor(String identifier, String name) {
        return new Anchor(URI.create(identifier), name);
    }

    /**
     * **
     * Create an identifier based on an authority, an identifier (url or urn).
     *
     * @param identifier
     * @return
     */
    private Identifier buildIdentifier(String authorityName, String identifier, String name, boolean useAnchor) {
        if (useAnchor) {
            return new DefaultIdentifier(buildCitation(authorityName), buildAnchorString(identifier, name));
        } else {
            return new DefaultIdentifier(buildCitation(authorityName), identifier);
        }
    }

    /**
     * *
     * Create an simple citation, usable for an MD_Identifier.
     *
     * @param urn
     * @return
     */
    private DefaultCitation buildCitation(String authorityName) {
        DefaultCitation authorityCitation = new DefaultCitation();
        authorityCitation.setDates(singleton(buildCitationDate(null, DateType.CREATION, NilReason.INAPPLICABLE)));
        authorityCitation.setTitle(new SimpleInternationalString(authorityName));
        return authorityCitation;
    }

    private DefaultCitation buildCitation(IDataset dataset, boolean addResponsibleParties, boolean addOtherCitationDetails) {
        DefaultCitation citation = new DefaultCitation();
        citation.setTitle(new SimpleInternationalString(dataset.getTitle()));
        List<CitationDate> dates = new ArrayList<>();

        dates.add(buildCreationDate(dataset.getCreationDate()));
        dates.add(buildRevisionDate(dataset.getRevisionDate()));

        citation.setDates(dates);

        Set<DefaultResponsibleParty> responsibleParties = new HashSet<>();
        List<IDatasource> datasources = dataset.getDatasources();
        datasources.sort(new DatasourceComparator());

        StringBuilder otherCitationDetails = null;
        if (addOtherCitationDetails) {
            if (datasources.size() > 0) {

                otherCitationDetails = new StringBuilder("Dataset composed of the following sources: ");
                otherCitationDetails.append(LINE_SEPARATOR);
            }
        }
        for (IDatasource datasource : datasources) {
            if (addOtherCitationDetails) {
                otherCitationDetails.append(buildReference(datasource));
                otherCitationDetails.append(LINE_SEPARATOR);
            }
            if (addResponsibleParties && datasource.getParties() != null) {
                for (IInstituteRole party : datasource.getParties()) {
                    Role role = roles.get(party.getIsoRole());

                    DefaultResponsibleParty ofRole = null;

                    ofRole = buildResponsibleParty(role, party.getOrganisationName(), party.getPhone(), party.getFax(), party.getDeliveryPoint(), party.getCity(), party.getPostalCode(), party.getSdnCountryCode(), party.getEmailAddress(), party.getWebsite());

                    if (ofRole != null) {
                        responsibleParties.add(ofRole);
                    }

                }
            }
        }
        if (otherCitationDetails != null) {
            citation.setOtherCitationDetails(new SimpleInternationalString(otherCitationDetails.toString()));
        }
        citation.setCitedResponsibleParties(responsibleParties);
        citation.setIdentifiers(singleton(buildIdentifier("Belgian Marine Data Centre", dataset.getIdentifier(), null, false)));
        return citation;
    }

    private String buildReference(IDatasource datasource) {
        StringBuilder reference = new StringBuilder();
        SimpleDateFormat yearF = new SimpleDateFormat("yyyy");
        if (datasource.getAuthorList() != null) {
            reference.append(datasource.getAuthorList());
            if ((datasource.getPublicationDate() != null)) {
                reference.append(" (");
                reference.append(yearF.format(datasource.getPublicationDate()));
                reference.append("). ");
            } else {
                reference.append(" (n.d.). ");
            }
        } else {
            if ((datasource.getPublicationDate() != null)) {
                reference.append("");
                reference.append(yearF.format(datasource.getPublicationDate()));
                reference.append(". ");
            }
        }
        reference.append(datasource.getTitle());
        if (!datasource.getTitle().endsWith(".")) {
            reference.append(".");
        }
        reference.append((datasource.getSubtitle() != null) ? datasource.getSubtitle() : "");
        if (datasource.getSubtitle() != null && !datasource.getSubtitle().endsWith(".")) {
            reference.append(".");
        }
        return reference.toString();
    }

    private DefaultCitation buildCitation(IDatasource datasource) {
        DefaultCitation citation = new DefaultCitation();
        List<CitationDate> dates = new ArrayList<>();
        CitationDate publicationDate = buildPublicationDate(datasource.getPublicationDate());
        if (publicationDate != null) {
            dates.add(publicationDate);
        }

        citation.setDates(dates);

        Set<ResponsibleParty> responsibleParties = new HashSet<>();

        if (datasource.getParties() != null) {
            for (IInstituteRole party : datasource.getParties()) {
                Role role = roles.get(party.getIsoRole());
                ResponsibleParty ofRole = null;
                ofRole = buildResponsibleParty(role, party.getOrganisationName(), party.getPhone(), party.getFax(), party.getDeliveryPoint(), party.getCity(), party.getPostalCode(), party.getSdnCountryCode(), party.getEmailAddress(), party.getWebsite());

                if (ofRole != null) {
                    responsibleParties.add(ofRole);
                }
            }
        }

        citation.setTitle(new SimpleInternationalString(buildReference(datasource)));
        citation.setCitedResponsibleParties(responsibleParties);
        citation.setIdentifiers(singleton(buildIdentifier("Belgian Marine Data Centre", datasource.getIdentifier(), null, false)));
        return citation;
    }

    private SpatialRepresentationType buildSpatialRepresentationType() {
        return SPATIAL_TYPE_MAPPING.get(dataset.getSpatialType());
    }

    private DefaultDataIdentification buildIdentificationInfo(IDataset dataset) {
        DefaultDataIdentification identification = new DefaultDataIdentification();
        Collection<ResponsibleParty> contacts = new ArrayList<>();
        if (dataset.getPointsOfContact() != null) {
            for (IInstituteRole contact : dataset.getPointsOfContact()) {
                contacts.add(buildResponsibleParty(contact));
            }
        }

        identification.setPointOfContacts(contacts);
        identification.setCitation(buildCitation(dataset, true, true));
        if (dataset.getAbstract() != null) {
            identification.setAbstract(new SimpleInternationalString(dataset.getAbstract()));
        } else {
            InternationalString abstractString = new SimpleInternationalString("Missing");//NilReason.MISSING.createNilObject(InternationalString.class);

            identification.setAbstract(abstractString);
        }
        identification.setResourceConstraints(buildConstraints());

        Set<String> languageStrings = dataset.getLanguages();
        languageStrings.add("EN");
        if (languageStrings.size() > 0) {
            identification.setLanguages(buildLanguages(languageStrings));
        } else {
            identification.setLanguages(singleton(Locale.ENGLISH));
        }
        identification.setTopicCategories(singleton(TopicCategory.OCEANS));
        Collection<DefaultExtent> extent = buildExtent(dataset);

        if (extent != null) {
            identification.setExtents(extent);
        }
        List<IDatasource> datasources = dataset.getDatasources();
        datasources.sort(new DatasourceComparator());
        identification.setAggregationInfo(buildAggregateInformation(dataset.getMasterDatasets(), datasources));

        Set<Keywords> keywords = new THashSet();

        keywords.addAll(buildKeyword(dataset.getKeywords()));

        identification.setDescriptiveKeywords(keywords);

        identification.setSpatialRepresentationTypes(singleton(buildSpatialRepresentationType()));

        // identification.setSpatialResolutions(clctn);
        return identification;
    }

    private SimpleInternationalString buildCountry(String sdnCountryCode) {
        String countryName = countries.get(sdnCountryCode);
        return new SimpleInternationalString(countryName);
    }

    private Anchor buildCountryAnchor(String sdnCountryCode) {
        String countryName = countries.get(sdnCountryCode);
        return new Anchor(URI.create("SDN:C32:7:" + sdnCountryCode), countryName);
    }

    private List<Locale> buildLanguages2(Set<Locale> languageCodes) {
        List result = new ArrayList();
        for (Locale language : languageCodes) {
            result.addAll(languages.get(language));
        }
        return result;
    }

    private List<Locale> buildLanguages(Set<String> languageCodes) {
        List result = new ArrayList();
        for (String languageCode : languageCodes) {
            if (languages.get(languageCode) != null) {
                result.addAll(languages.get(languageCode));
            }
        }
        return result;
    }

    private DefaultResponsibleParty buildResponsibleParty(IInstituteRole contact) {
        return buildResponsibleParty(ROLE_MAPPING.get(contact.getIsoRole()), contact.getOrganisationName(), contact.getPhone(), contact.getFax(), contact.getDeliveryPoint(), contact.getCity(), contact.getPostalCode(), contact.getSdnCountryCode(), contact.getEmailAddress(), contact.getWebsite());
    }

    private DefaultResponsibleParty buildResponsibleParty(Role role, String organisationName, String phone, String fax, String deliveryPoint, String city, String postalCode, String sdnCountryCode, String email, String website) {
        if (role == null) {
            role = Role.ORIGINATOR;
        }
        DefaultResponsibleParty rp = new DefaultResponsibleParty(role);
        if (organisationName != null) {
            rp.setOrganisationName(new SimpleInternationalString(organisationName));
        }
        if (phone != null || fax != null || deliveryPoint != null || city != null || postalCode != null || sdnCountryCode != null || website != null) {
            DefaultContact contact = new DefaultContact();
            Set<DefaultTelephone> phones = new HashSet<>();
            if (phone != null) {
                DefaultTelephone t1 = new DefaultTelephone();
                t1.setNumber(phone);
                //t1.setNumberType(TelephoneType.VOICE);
                phones.add(t1);
            }
            if (fax != null) {
                DefaultTelephone t2 = new DefaultTelephone();
                t2.setNumber(fax);
                //t2.setNumberType(TelephoneType.FACSIMILE);
                phones.add(t2);
            }
            contact.setPhones(phones);
            if (deliveryPoint != null || city != null || postalCode != null || sdnCountryCode != null) {
                DefaultAddress add = new DefaultAddress();
                if (deliveryPoint != null) {
                    add.setDeliveryPoints(singleton(deliveryPoint));
                }
                if (city != null) {
                    add.setCity(new SimpleInternationalString(city));
                }
                if (postalCode != null) {
                    add.setPostalCode(postalCode);
                }
                if (sdnCountryCode != null) {
                    if (useAnchor) {
                        add.setCountry(buildCountryAnchor(sdnCountryCode));
                    } else {
                        add.setCountry(buildCountry(sdnCountryCode));
                    }
                }
                if (email != null) {
                    add.setElectronicMailAddresses(singleton(email));
                }
                contact.setAddresses(singleton(add));
            }
            if (website != null) {
                DefaultOnlineResource o = new DefaultOnlineResource(URI.create(website));
                o.setProtocol("http");
                contact.setOnlineResources(singleton(o));
            }

            rp.setContactInfo(contact);

        }
        return rp;
    }

    /**
     * *
     * It is not possible with apache sis 0.7 to properly create temporal bounds
     * (start and end date). The approach is to create the xml and manually
     * alter the output...
     *
     * @param dataset
     * @return
     */
    private Collection<DefaultExtent> buildExtent(IDataset dataset) {
        Set<DefaultExtent> result = new THashSet<>();
        DefaultExtent extent = new DefaultExtent();
        Set<IRegion> regions = new HashSet();
        for (IDatasource ds : dataset.getDatasources()) {
            if (ds.getRegionCollection() != null) {
                ds.getRegionCollection().stream().filter(o -> o != null).forEach(r -> regions.add(r));
            }
        }
        // geographic extent
        if (!regions.isEmpty() || (dataset.getWestBoundLon() != null && dataset.getEastBoundLon() != null && dataset.getSouthBoundLat() != null && dataset.getNorthBoundLat() != null)) {
            extent.setGeographicElements(buildGeographicExtent(regions, dataset.getWestBoundLon(), dataset.getEastBoundLon(), dataset.getSouthBoundLat(), dataset.getNorthBoundLat()));
        }

        //temporal extent
        if (dataset.getStartDate() != null && dataset.getEndDate() != null) {
            DefaultTemporalExtent tempExtent = new DefaultTemporalExtent();
            extent.setTemporalElements(singleton(tempExtent));
        }
        result.add(extent);
        return result;
    }

    private Set<GeographicExtent> buildGeographicExtent(Set<IRegion> regions, Double westBoundLon, Double eastBoundLon, Double southBoundLat, Double northBoundLat) {
        Set<GeographicExtent> result = new HashSet<>();

        if (eastBoundLon != null && eastBoundLon == 0) {
            eastBoundLon = westBoundLon;
        }
        if (northBoundLat != null && northBoundLat == 0) {
            northBoundLat = southBoundLat;
        }
        if (westBoundLon != null && eastBoundLon != null && southBoundLat != null && northBoundLat != null) {
            GeographicExtent geo = new DefaultGeographicBoundingBox(westBoundLon, eastBoundLon, southBoundLat, northBoundLat);
            result.add(geo);
        }
        if (regions != null) {
            for (IRegion region : regions) {
                GeographicExtent geoDesc = buildGeographicDescription("VLIZ", "http://marineregions.org/mrgid/" + region.getMRGID().toString(), region.getName());
                result.add(geoDesc);
            }
        }

        return result;
    }

    private GeographicExtent buildGeographicDescription(String authorityName, String identifier, String name) {
        if (useAnchor) {
            return new DefaultGeographicDescription(buildCitation(authorityName), buildAnchorString(identifier, name));
        } else {
            return new DefaultGeographicDescription(buildCitation(authorityName), name);
        }
    }

    private DefaultCitation buildThesaurusCitation(Thesaurus thesaurus) {
        return buildThesaurusCitation(thesaurus.thesaurusTitle, thesaurus.thesaurusAltTitle, thesaurus.thesaurusPublicationDate, thesaurus.thesaurusVersion);

    }

    private DefaultCitation buildThesaurusCitation(String thesaurusTitle, String thesaurusAltTitle, Date thesaurusPublicationDate, String thesaurusVersion) {
        //we create the citation describing the vocabulary used
        DefaultCitation citation = null;
        if (thesaurusTitle != null) {
            citation = new DefaultCitation();
            citation.setTitle(new SimpleInternationalString(thesaurusTitle));
            if (thesaurusAltTitle != null) {
                citation.setAlternateTitles(singleton(new SimpleInternationalString(thesaurusAltTitle)));
            }
            if (thesaurusPublicationDate != null) {
                DefaultCitationDate revisionDate = new DefaultCitationDate();
                revisionDate.setDateType(DateType.PUBLICATION);
                revisionDate.setDate(thesaurusPublicationDate);
                citation.setDates(singleton(revisionDate));
            }
            if (thesaurusVersion != null) {
                citation.setEdition(new SimpleInternationalString(thesaurusVersion));
            }
            //citation.setIdentifiers(singleton(new ImmutableIdentifier(null, null, "http://www.seadatanet.org/urnurl/")));
        }
        return citation;
    }

    private static class Thesaurus {

        String thesaurusTitle;
        String thesaurusAltTitle;
        Date thesaurusPublicationDate;
        String thesaurusVersion;
        public static final Thesaurus NO_THESAURUS = new Thesaurus(Thesaurus.NOTHING, null, null, null);
        public static final String NOTHING = "NO_THESAURUS";

        public Thesaurus(IKeyword keyword) {
            this(keyword.getThesaurusTitle() == null ? NOTHING : keyword.getThesaurusTitle(), null, keyword.getThesaurusPublicationDate(), keyword.getThesaurusVersion());
        }

        public Thesaurus(String thesaurusTitle, String thesaurusAltTitle, Date thesaurusPublicationDate, String thesaurusVersion) {
            if (thesaurusTitle == null) {
                throw new IllegalArgumentException("The thesaurus must have a title.");
            }
            this.thesaurusTitle = thesaurusTitle;
            this.thesaurusAltTitle = thesaurusAltTitle;
            this.thesaurusPublicationDate = thesaurusPublicationDate;
            this.thesaurusVersion = thesaurusVersion;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + Objects.hashCode(this.thesaurusTitle);
            hash = 23 * hash + Objects.hashCode(this.thesaurusPublicationDate);
            hash = 23 * hash + Objects.hashCode(this.thesaurusVersion);
            return hash;
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            } else if (other instanceof Thesaurus) {
                Thesaurus otherThesaurus = (Thesaurus) other;
                Date localThesaurusPublicationDate = this.thesaurusPublicationDate == null ? new Date() : this.thesaurusPublicationDate;
                String localThesaurusVersion = this.thesaurusVersion == null ? "" : this.thesaurusVersion;

                Date otherThesaurusPublicationDate = otherThesaurus.thesaurusPublicationDate == null ? new Date() : otherThesaurus.thesaurusPublicationDate;
                String otherThesaurusVersion = otherThesaurus.thesaurusVersion == null ? "" : otherThesaurus.thesaurusVersion;

                return otherThesaurus.thesaurusTitle.equals(this.thesaurusTitle) && localThesaurusPublicationDate.equals(otherThesaurusPublicationDate) && localThesaurusVersion.equals(otherThesaurusVersion);
            }
            return false;
        }

    }

    private Collection<Keywords> buildKeyword(Collection<IKeyword> keywords) {
        Collection<Keywords> allKeywords = new ArrayList<>();

        if (keywords != null && !keywords.isEmpty()) {
            Map< Thesaurus, List<IKeyword>> intKeywords = new THashMap<>();

            for (IKeyword keyword : keywords) {

                Thesaurus thesaurus = new Thesaurus(keyword);
                List<IKeyword> get = intKeywords.get(new Thesaurus(keyword));
                if (get != null) {
                    get.add(keyword);
                    intKeywords.put(thesaurus, get);
                } else {
                    List<IKeyword> subKeywords = new ArrayList<>();
                    subKeywords.add(keyword);
                    intKeywords.put(thesaurus, subKeywords);
                }

            }

            for (Map.Entry<Thesaurus, List<IKeyword>> entry : intKeywords.entrySet()) {
                DefaultKeywords individualKeyword = new DefaultKeywords();
                Thesaurus thesaurus = entry.getKey();
                List<IKeyword> keywordList = entry.getValue();

                //keyword.setType(type);
                Set<InternationalString> kws = new HashSet<>();
                for (IKeyword iKeyword : keywordList) {
                    String url = iKeyword.getUrl();
                    if (url != null && useAnchor) {
                        kws.add(new Anchor(URI.create(url), iKeyword.getPrefLabel()));
                    } else {
                        kws.add(new SimpleInternationalString(iKeyword.getPrefLabel()));
                    }
                    individualKeyword.setType(KEYWORD_TYPE_MAPPING.get(iKeyword.getType())); //actually technically multiple types could be present within the keywords of one thesaurus. 
                }

                individualKeyword.setKeywords(kws);
                if (!thesaurus.equals(Thesaurus.NO_THESAURUS)) {
                    individualKeyword.setThesaurusName(buildThesaurusCitation(thesaurus));
                }
                allKeywords.add(individualKeyword);
            }

        }
        return allKeywords;
    }

    private Keywords buildKeyword(Map<String, String> urlKeywords, KeywordType type, String thesaurusTitle, String thesaurusAltTitle, Date thesaurusPublicationDate, String thesaurusVersion) {
        if (useAnchor) {
            DefaultKeywords keyword = new DefaultKeywords();
            keyword.setType(type);
            Set<InternationalString> kws = new HashSet<>();
            for (Map.Entry<String, String> entry : urlKeywords.entrySet()) {
                kws.add(new Anchor(URI.create(entry.getKey()), entry.getValue()));
            }
            keyword.setKeywords(kws);
            if (thesaurusTitle != null) {
                keyword.setThesaurusName(buildThesaurusCitation(thesaurusTitle, thesaurusAltTitle, thesaurusPublicationDate, thesaurusVersion));
            }
            return keyword;
        } else {
            return buildKeyword(urlKeywords.values(), type, thesaurusTitle, thesaurusAltTitle, thesaurusPublicationDate, thesaurusVersion);
        }
    }

    private Keywords buildKeyword(Collection<String> keywords, KeywordType type, String thesaurusTitle, String thesaurusAltTitle, Date thesaurusPublicationDate, String thesaurusVersion) {

        DefaultKeywords keyword = new DefaultKeywords();
        keyword.setType(type);
        Set<InternationalString> kws = new HashSet<>();
        for (String kw : keywords) {
            kws.add(new SimpleInternationalString(kw));
        }
        keyword.setKeywords(kws);
        if (thesaurusTitle != null) {
            keyword.setThesaurusName(buildThesaurusCitation(thesaurusTitle, thesaurusAltTitle, thesaurusPublicationDate, thesaurusVersion));
        }

        return keyword;
    }

    private Set<Constraints> buildConstraints() {
        Set<Constraints> results = new THashSet();

        Set<Restriction> restrictions = new HashSet<>();
        restrictions.add(Restriction.OTHER_RESTRICTIONS);
        /* Set<Restriction> license = new HashSet<>();
        license.add(Restriction.LICENSE);*/

        DefaultLegalConstraints useLimitation = new DefaultLegalConstraints();
        useLimitation.setUseLimitations(singleton(new SimpleInternationalString("No conditions apply to access and use.")));
        // useLimitation.setAccessConstraints(restrictions);
        results.add(useLimitation);

        DefaultLegalConstraints accessConstraints = new DefaultLegalConstraints();
        accessConstraints.setAccessConstraints(restrictions);
        accessConstraints.setOtherConstraints(singleton(new SimpleInternationalString("No limitations apply to access.")));
        results.add(accessConstraints);

        DefaultLegalConstraints useConstraints = new DefaultLegalConstraints();
        useConstraints.setUseConstraints(restrictions);
        useConstraints.setOtherConstraints(singleton(new SimpleInternationalString("https://creativecommons.org/licenses/by/2.0/")));
        results.add(useConstraints);

        DefaultSecurityConstraints securityConstraint = new DefaultSecurityConstraints();
        securityConstraint.setClassification(Classification.UNCLASSIFIED);
        results.add(securityConstraint);
        return results;
    }

    private Format buildDumbFormat() {
        DefaultFormat format = new DefaultFormat();

        format.setName(new SimpleInternationalString("Formats are reported in multiple distributorFormats."));

        format.setVersion(new SimpleInternationalString("n/a"));
        format.setSpecification(new SimpleInternationalString("Formats are reported in multiple distributorFormats."));

        return format;
    }

    private Set<Format> buildFormat(List<IDistributionFormat> distributionFormats) {
        Set<Format> formats = new HashSet<>();
        for (IDistributionFormat distributionFormat : distributionFormats) {

            //if (distributionFormat.getDistributionFormatNiceName() != null) {
            DefaultFormat format = new DefaultFormat();

            if (useAnchor && distributionFormat.getDistributionFormatUrn() != null) {
                format.setName(new Anchor(URI.create(distributionFormat.getDistributionFormatUrn()), distributionFormat.getDistributionFormatMime().toString()));
            } else {
                format.setName(new SimpleInternationalString(distributionFormat.getDistributionFormatMime().toString()));
            }

            if (distributionFormat.getDistributionFormatVersion() != null) {
                format.setVersion(new SimpleInternationalString(distributionFormat.getDistributionFormatVersion()));
            } else {
                format.setVersion(/*NilReason.UNKNOWN.createNilObject(InternationalString.class)*/new SimpleInternationalString("Unknown"));
            }
            formats.add(format);
            //}
        }
        return formats;
    }

    private Distribution buildDistributionInfo(IDataset dataset) {
        if (dataset.getDistributionResources() != null && !dataset.getDistributionResources().isEmpty()) {
            DefaultDistribution distributionInfo = new DefaultDistribution();

            distributionInfo.setDistributionFormats(singleton(buildDumbFormat()));

            for (IDistributionResource distributionResource : dataset.getDistributionResources()) {
                URL url = distributionResource.getOnlineResourceUrl();
                if (url != null) {
                    DefaultDistributor distributor = new DefaultDistributor();
                    distributor.setDistributorContact(buildResponsibleParty(distributionResource.getDistributor()));
                    distributor.setDistributorFormats(buildFormat(distributionResource.getDistributionFormats()));

                    Double dataSizeInBytes = distributionResource.getDataSizeInMB();
                    DefaultDigitalTransferOptions digiTrans = new DefaultDigitalTransferOptions();
                    if (dataSizeInBytes != null && !dataSizeInBytes.isNaN() && !dataSizeInBytes.isInfinite() && dataSizeInBytes > 0) {

                        digiTrans.setTransferSize(distributionResource.getDataSizeInMB());

                    }
                    distributor.setDistributorTransferOptions(singleton(digiTrans));

                    Set<OnlineResource> resources = new HashSet<>();
                    DefaultOnlineResource resource = new DefaultOnlineResource();

                    try {
                        URI u = new URI(url.toString().replace(" ", "%20"));
                        resource.setLinkage(u);
                    } catch (URISyntaxException ex) {
                        resource = null;
                        Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.SEVERE, "The url of the distributionResource (" + url + ") is invalid.", ex);

                    }
                    if (resource != null) {
                        if (distributionResource.getOnlineResourceDescription() != null) {
                            resource.setDescription(new SimpleInternationalString(distributionResource.getOnlineResourceDescription()));
                        } else {
                            resource.setDescription(new SimpleInternationalString("Link to " + distributionResource.getOnlineResourceUrl().toString()));
                        }
                        if (distributionResource.getFunction() != null) {
                            resource.setFunction(onLineFunctions.get(distributionResource.getFunction()));
                        }
                        resource.setName(distributionResource.getOnlineResourceName());
                        resource.setProtocol(distributionResource.getOnlineResourceProtocol().toString());
                        resources.add(resource);
                    }

                    if (!resources.isEmpty()) {
                        digiTrans.setOnLines(resources);
                    }

                    distributionInfo.getDistributors().add(distributor);
                }

            }
            return distributionInfo;
        }
        return null;
    }

    private Collection<? extends DataQuality> buildDataQualityInfo(IDataset dataset) {
        Set<DataQuality> qualities = new THashSet();
        //scope
        DefaultDataQuality quality = new DefaultDataQuality(DATASET_SCOPE_QUALITY);
        //lineage
        if (dataset.getLineage() != null) {
            DefaultLineage lineage = new DefaultLineage();
            lineage.setStatement(new SimpleInternationalString(dataset.getLineage()));
            quality.setLineage(lineage);
        }
        //reports
        if (inspire) {
            DefaultDomainConsistency inspireCompliancy = new DefaultDomainConsistency();
            DefaultConformanceResult result = new DefaultConformanceResult();
            result.setPass(Boolean.TRUE);
            result.setSpecification(buildInspireCitation());
            result.setExplanation(new SimpleInternationalString("See the referenced specification"));

            inspireCompliancy.setResults(singleton(result));

            quality.setReports(singleton(inspireCompliancy));
            qualities.add(quality);
        }
        return qualities;

    }

    private DefaultCitation buildInspireCitation() {
        DefaultCitation citation = new DefaultCitation();
        citation.setTitle(new SimpleInternationalString("COMMISSION REGULATION (EU) No 1089/2010 of 23 November 2010 implementing Directive 2007/2/EC of the European Parliament and of the Council as regards interoperability of spatial data sets and services"));
        citation.setDates(singleton(buildCitationDate(INSPIRE_IMPLEMENTING_RULES_PUBLICATION_DATE, DateType.PUBLICATION, null)));
        return citation;
    }

    private Set<DefaultAggregateInformation> buildAggregateInformation(Collection<IDataset> masterDatasets, Collection<IDatasource> datasources) {

        Set<DefaultAggregateInformation> aggregateInfos = new HashSet<>();
        if (masterDatasets != null) {
            for (IDataset dataset : masterDatasets) {
                DefaultAggregateInformation aggregateInfo = new DefaultAggregateInformation();
                DefaultCitation citation = buildCitation(dataset, false, false); //don't print all the responsible parties and the otherCitationDetails of the parent dataset.

                aggregateInfo.setAggregateDataSetName(citation);
                aggregateInfo.setInitiativeType(InitiativeType.COLLECTION);
                aggregateInfo.setAssociationType(AssociationType.LARGER_WORD_CITATION);
                aggregateInfos.add(aggregateInfo);
            }
        }
        if (datasources != null) {
            for (IDatasource datasource : datasources) {
                DefaultAggregateInformation aggregateInfo = new DefaultAggregateInformation();
                DefaultCitation citation = buildCitation(datasource);

                aggregateInfo.setAggregateDataSetName(citation);
                aggregateInfo.setInitiativeType(InitiativeType.COLLECTION);
                aggregateInfo.setAssociationType(AssociationType.SOURCE);
                aggregateInfos.add(aggregateInfo);
            }
        }
        if (masterDatasets == null && datasources == null) {
            return null;
        }
        return aggregateInfos;
    }

    private CitationDate buildCitationDate(Date date, DateType type, NilReason nilReason) {
        CitationDate citationDate;

        if (date == null) {
            citationDate = nilReason.createNilObject(CitationDate.class);

        } else {
            citationDate = new CitationDate() {
                @Override
                public Date getDate() {
                    return date;
                }

                @Override
                public DateType getDateType() {
                    return type;
                }
            };
        }
        return citationDate;
    }

    private ReferenceSystemMetadata buildReferenceSystem(String epsgCode, boolean seaDataNet) {
        ImmutableIdentifier identifier;
        if (seaDataNet) {
            DefaultCitation RScitation = new DefaultCitation();
            RScitation.setTitle(new SimpleInternationalString("SeaDataNet geographic co-ordinate reference frames"));
            RScitation.setAlternateTitles(singleton(new SimpleInternationalString("L10")));
            RScitation.setDates(singleton(buildCitationDate(null, DateType.CREATION, NilReason.MISSING)));
            RScitation.setIdentifiers(singleton(new ImmutableIdentifier(null, null, "http://vocab.nerc.ac.uk/collection/L10/current")));

            if (useAnchor) {
                RScitation.setEdition(new Anchor(URI.create("SDN:C371:1:2"), "3"));
            } else {
                RScitation.setEdition(new SimpleInternationalString("3"));
            }

            identifier = new ImmutableIdentifier(RScitation, "L10", epsgCode);
        } else if (inspire) {
            identifier = new ImmutableIdentifier(null, "EPSG", "http://www.opengis.net/def/crs/EPSG/0/3035");
        } else {
            identifier = new ImmutableIdentifier(null, "EPSG", "http://www.opengis.net/def/crs/EPSG/0/" + epsgCode);
        }

        ReferenceSystemMetadata rs = new ReferenceSystemMetadata(identifier);
        return rs;
    }

    public String buildMetadataFileName() {
        return dataset.getFileName();
    }

    //  <gmd:spatialRepresentationInfo><gmd:MD_VectorSpatialRepresentation><gmd:topologyLevel><gmd:MD_TopologyLevelCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/ML_gmxCodelists.xml#MD_TopologyLevelCode" codeListValue="geometryOnly"/></gmd:topologyLevel></gmd:MD_VectorSpatialRepresentation></gmd:spatialRepresentationInfo>
    /**
     * *
     * Construct the ISO 19115 metadata content of the Dataset provided in the
     * constructor.
     *
     * @param inspire Whether the metadata content should take into account
     * INSPIRE specificities
     * @param useAnchor Whether the metadata content should construct
     * gmx:Anchors or stick to gco:Characterstrings
     * @return
     */
    public DefaultMetadata buildMetadata(boolean inspire, boolean useAnchor, IInstituteRole mdAuthor) {
        this.inspire = inspire;
        this.useAnchor = useAnchor;

        DefaultMetadata metadata = new DefaultMetadata();
        metadata.setMetadataStandards(Citations.ISO_19115);
        metadata.getLanguages().add(Locale.ENGLISH);

        if (mdAuthor != null) {
            metadata.setContacts(singleton(buildResponsibleParty(mdAuthor)));
        }
        Charset charset = Charset.forName("UTF-8");
        metadata.getCharacterSets().add(charset);

        final Identifier fileIdentifier = new DefaultIdentifier(dataset.getIdentifier());
        metadata.setMetadataIdentifier(fileIdentifier);

        final Date creation = new Date();
        metadata.setDateInfo(singleton(new DefaultCitationDate(creation, DateType.CREATION)));

        metadata.setMetadataScopes(singleton(DATASET_SCOPE));

        metadata.setReferenceSystemInfo(singleton(buildReferenceSystem(dataset.getEpsgCode(), false)));

        metadata.setIdentificationInfo(singleton(buildIdentificationInfo(dataset)));
        metadata.setDistributionInfo(buildDistributionInfo(dataset));
        metadata.setDataQualityInfo(buildDataQualityInfo(dataset));
        return metadata;
    }

    /* private static String needsKeywordTranslations(DefaultMetadata metadata) {

        for (Identification id : metadata.getIdentificationInfo()) {
            for (Keywords descriptiveKeyword : id.getDescriptiveKeywords()) {
                descriptiveKeyword.
            }
        }
    }*/
    /**
     * Translate the occurences of INSPIRE keywords in an ISO 19115 XML String.
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
     * @return
     * @throws IOException
     */
    private static String processKeywordTranslations(String XMLText) {

        List<List<String>> urlMatches = StringUtils.getRegexResultFromMultilineString(XMLText, Pattern.compile("<gmx:Anchor xlink:href=\"(.*?)\">"), null);
        List<String> urlMatches2 = StringUtils.flattenListOfLists(urlMatches);
        Set<String> urlMatches3 = new HashSet(urlMatches2);

        for (String urlMatch : urlMatches3) {
            Map<String, Boolean> synonymsFound = new HashMap<>();
            String pattern = "(<gmx:Anchor xlink:href=\"" + urlMatch + "\">.*?<\\/gmx:Anchor>)";
            // boolean synonymFound = false;
            StringBuilder freeTextBuilder = new StringBuilder("<gmd:PT_FreeText>");
            for (String keywordLanguage : keywordLanguages) {
                File gemetInspireFile = new File(INSPIRE_RDF_DIR, "theme." + keywordLanguage + ".rdf");

                Map<String, String> namespaces = new HashMap<>();
                namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
                namespaces.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
                namespaces.put("dct", "http://purl.org/dc/terms/");

                try {
                    List<String> synonym = XMLUtils.xpathQuery(gemetInspireFile, "/rdf:RDF/rdf:Description[@rdf:about='" + urlMatch + "']/dct:title/text()", namespaces);
                    if (synonym != null && synonym.size() == 1) {
                        synonymsFound.put(keywordLanguage, true);
                        //synonymFound = true;
                        freeTextBuilder.append("<gmd:textGroup><gmd:LocalisedCharacterString locale=\"#").append(keywordLanguage.toUpperCase()).append("\">").append(synonym.get(0)).append("</gmd:LocalisedCharacterString></gmd:textGroup>");
                    }
                } catch (IOException e) {
                    //cannot occur.
                }
            }
            if (!synonymsFound.isEmpty()) {
                freeTextBuilder.append("</gmd:PT_FreeText>");
                XMLText = XMLText.replaceAll(pattern, "$1" + freeTextBuilder.toString());
                for (Map.Entry<String, Boolean> entry : synonymsFound.entrySet()) {
                    String language = entry.getKey();
                    Boolean hasSynonym = entry.getValue();
                    if (hasSynonym) {
                        XMLText = XMLText.replaceAll("(</gmd:metadataStandardVersion>)", "$1" + localeMap.get(language.toUpperCase()));
                    }
                }

            }

        }
        return XMLText;
    }

    /**
     * *
     * Serialize to the ISO 19115 XML representation of the given ISO 19115
     * metadata content.
     *
     * @param metadata
     * @return
     */
    public String createXMLString(DefaultMetadata metadata, List<String> keywordLanguages) {

        Date start = dataset.getStartDate();
        Date end = dataset.getEndDate();

        try {
            String result = XML.marshal(metadata);
            if (start != null) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                String endString;
                if (end != null) {
                    endString = "<gml:endPosition>" + dateFormatter.format(end) + "</gml:endPosition>";
                } else {
                    endString = "<gml:endPosition indeterminatePosition=\"now\" ></gml:endPosition>";
                }

                UUID uuid = UUID.randomUUID();

                result = result.replaceAll("<gmd:EX_TemporalExtent/>", "<gmd:EX_TemporalExtent><gmd:extent>\n"
                        + "<gml:TimePeriod  gml:id=\"ID" + uuid.toString() + "\" xsi:type=\"gml:TimePeriodType\">"
                        + "<gml:beginPosition>" + dateFormatter.format(start)
                        + "</gml:beginPosition>"
                        + endString
                        + "</gml:TimePeriod>"
                        + "</gmd:extent></gmd:EX_TemporalExtent>");
            }

            /* String gmi = "xmlns:gmi=\"http://www.isotc211.org/2005/gmi\"";
            String gmx = "xmlns:gmx=\"http://www.isotc211.org/2005/gmx\"";
            String srv = "xmlns:srv=\"http://www.isotc211.org/2005/srv\"";
            String gts = "xmlns:gts=\"http://www.isotc211.org/2005/gts\"";*/
            String header = "<gmd:MD_Metadata xmlns:gmd=\"http://www.isotc211.org/2005/gmd\"\n"
                    + "                 xmlns:gco=\"http://www.isotc211.org/2005/gco\"\n"
                    + "                 xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"
                    + "                 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                    + "                 xmlns:gml=\"http://www.opengis.net/gml\"\n"
                    + "                 xmlns:geonet=\"http://www.fao.org/geonetwork\"\n"
                    + "                 xmlns:gmx=\"http://www.isotc211.org/2005/gmx\""
                    + "                 xsi:schemaLocation=\"http://www.isotc211.org/2005/gmd http://schemas.opengis.net/iso/19139/20070417/gmd/gmd.xsd http://www.isotc211.org/2005/gmx http://schemas.opengis.net/iso/19139/20070417/gmx/gmx.xsd\">";

            result = result.replaceAll("<gmd:MD_Metadata(.|\\n)*?>", header);

            result = result.replace("<gco:CharacterString>ISO 19115-1:2014(E)</gco:CharacterString>", "<gco:CharacterString>ISO 19115:2003/19139</gco:CharacterString>");
            result = result.replace("<gco:CharacterString>Geographic Information — Metadata Part 1: Fundamentals</gco:CharacterString>", "<gco:CharacterString>Geographic information -- Metadata</gco:CharacterString>");
            result = result.replace(" codeSpace=\"eng\"", "");
            result = result.replace("codeListValue=\"nld\">nld", "codeListValue=\"nld\">Dutch");
            result = result.replace("codeListValue=\"fra\"", "codeListValue=\"fre\"");
            result = result.replace("codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#LanguageCode\"", "codeList=\"http://www.loc.gov/standards/iso639-2/\"");
            //result = allKeywords.replace("<gco:CharacterString>&lt;gmx:Anchor", "<gmx:Anchor");
            //result = allKeywords.replace("&lt;/gmx:Anchor&gt;</gco:CharacterString>", "</gmx:Anchor>");
            //result = allKeywords.replace("&gt;", ">");

            //Pattern.compile(pattern1).matcher(allKeywords).replaceAll("<gmx:Anchor xlink:href=\"$1\"></gmx:Anchor>");
            //Pattern.compile(pattern2).matcher(allKeywords).replaceAll("<gmx:Anchor xlink:href=\"$1\">$2</gmx:Anchor>");
            String pattern1 = "<gco:CharacterString>&lt;gmx:Anchor xlink:href=\"(.*?)\"/&gt;</gco:CharacterString>";
            result = result.replaceAll(pattern1, "<gmx:Anchor xlink:href=\"$1\"></gmx:Anchor>");

            String pattern2 = "<gco:CharacterString>&lt;gmx:Anchor xlink:href=\"(.*?)\"&gt;(.*?)&lt;/gmx:Anchor&gt;</gco:CharacterString>";
            result = result.replaceAll(pattern2, "<gmx:Anchor xlink:href=\"$1\">$2</gmx:Anchor>");

            result = processKeywordTranslations(result);
            return result;
        } catch (JAXBException ex) {
            Logger.getLogger(ISO19115DatasetBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * *
     * Write the given metadata content to the given File. File should not be a
     * path.
     *
     * @param file
     * @param metadata
     * @throws FileNotFoundException
     */
    public void createFile(File file, DefaultMetadata metadata, List<String> keywordLanguages) throws FileNotFoundException {

        String xml = createXMLString(metadata, keywordLanguages);
        if (xml != null) {
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(xml);
            }
        }

    }

}
