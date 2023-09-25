/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.iso;

import static java.util.Collections.singleton;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
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
import org.apache.sis.metadata.iso.identification.DefaultAggregateInformation;
import org.apache.sis.metadata.iso.identification.DefaultBrowseGraphic;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.metadata.iso.identification.DefaultKeywords;
import org.apache.sis.metadata.iso.identification.DefaultRepresentativeFraction;
import org.apache.sis.metadata.iso.identification.DefaultResolution;
import org.apache.sis.metadata.iso.lineage.DefaultLineage;
import org.apache.sis.metadata.iso.quality.DefaultConformanceResult;
import org.apache.sis.metadata.iso.quality.DefaultDataQuality;
import org.apache.sis.metadata.iso.quality.DefaultDomainConsistency;
import org.apache.sis.metadata.iso.quality.DefaultScope;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.xml.NilReason;
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
import org.opengis.metadata.extent.GeographicDescription;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.identification.AssociationType;
import org.opengis.metadata.identification.BrowseGraphic;
import org.opengis.metadata.identification.InitiativeType;
import org.opengis.metadata.identification.KeywordType;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.identification.Resolution;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.quality.Scope;
import org.opengis.metadata.spatial.SpatialRepresentationType;
//import org.opengis.temporal.Period;
import org.opengis.util.InternationalString;

import be.naturalsciences.bmdc.metadata.model.IDataset;
import be.naturalsciences.bmdc.metadata.model.IDatasource;
import be.naturalsciences.bmdc.metadata.model.IDistributionFormat;
import be.naturalsciences.bmdc.metadata.model.IDistributionResource;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import be.naturalsciences.bmdc.metadata.model.IKeyword;
import be.naturalsciences.bmdc.metadata.model.IRegion;
import be.naturalsciences.bmdc.metadata.model.OnlinePossibilityEnum;
import be.naturalsciences.bmdc.metadata.model.Thesaurus;
import be.naturalsciences.bmdc.metadata.model.comparator.DatasourceComparator;
import be.naturalsciences.bmdc.metadata.model.comparator.GeographicDescriptionComparator;
import gnu.trove.set.hash.THashSet;

/**
 * A class responsible to create a complete ISO 19115:2003/19139 Metadata
 * representation.
 *
 * @author thomas
 */
public class ISO19115DatasetBuilder {

    public static final Logger LOG = Logger.getLogger(ISO19115DatasetBuilder.class.getSimpleName());

    public static final Map<String, String> COUNTRIES;
    public static final Map<String, List<Locale>> LANGUAGES;
    public static final Map<IDataset.Role, Role> ROLES;
    public static final Map<OnlinePossibilityEnum, OnLineFunction> ONLINE_FUNCTIONS;
    public static final Map<IDataset.InspireTheme, Keywords> INSPIRE_THEMES;

    public static final DefaultMetadataScope DATASET_SCOPE = new DefaultMetadataScope(ScopeCode.DATASET, null);
    public static final Scope DATASET_SCOPE_QUALITY = new DefaultScope(ScopeCode.DATASET);
    public static final Date INSPIRE_IMPLEMENTING_RULES_PUBLICATION_DATE = new GregorianCalendar(2010,
            Calendar.DECEMBER, 8).getTime();
    public static final Date GEMET_INSPIRE_THEMES_PUBLICATION_DATE = new GregorianCalendar(2008, Calendar.JUNE, 1)
            .getTime();
    public static final String GEMET_INSPIRE_THEMES_THESAURUS_URL = "http://www.eionet.europa.eu/gemet/inspire_themes";
    public static final Date MARINE_REGIONS_PUBLICATION_DATE = new GregorianCalendar(2018, Calendar.FEBRUARY, 21)
            .getTime();

    public static final String LINE_SEPARATOR = "\n" +
            "";

    // System.lineSeparator();

    public static final Role DEFAULT_ROLE = Role.AUTHOR;

    private IDataset dataset;
    private DefaultMetadata metadata; // the final result
    private boolean useAnchor;
    private boolean inspire;
    private IInstituteRole mdAuthor;
    private boolean useDatasetCitationResponsibleParty;
    private boolean useLinkedDatasetCitationResponsibleParty;

    /**
     * *
     * The authority responsible for issuing this dataset's identifier
     */
    private String identifierAuthority;

    public static String DEFAULT_USE_CONSTRAINTS = "No conditions apply to use.";
    public static String DEFAULT_ACCESS_CONSTRAINTS = "No limitations on public access.";
    public static String DEFAULT_LICENSE = "https://creativecommons.org/licenses/by/2.0/";

    public static Set<Restriction> OTHER_RESTRICTIONS_PLACEHOLDER = new LinkedHashSet<>(); // FIXED ORDER!

    static {
        OTHER_RESTRICTIONS_PLACEHOLDER.add(Restriction.OTHER_RESTRICTIONS);
    }

    /**
     * *
     * Returns the dataset that's used for this Builder.
     *
     * @return
     */
    public IDataset getDataset() {
        return dataset;
    }

    public boolean useAnchor() {
        return useAnchor;
    }

    /**
     * *
     * Sets whether Anchors should be used versus CharacterStrings, when the
     * actual URL is available.
     *
     * @param useAnchor
     */
    public void setUseAnchor(boolean useAnchor) {
        this.useAnchor = useAnchor;
    }

    public boolean isInspire() {
        return inspire;
    }

    /**
     * *
     * Sets whether the data output should be INSPIRE-compliant.
     *
     * @param inspire
     */
    public void setInspire(boolean inspire) {
        this.inspire = inspire;
    }

    public IInstituteRole getMdAuthor() {
        return mdAuthor;
    }

    /**
     * *
     * Sets the author of the metadata itself.
     *
     * @param mdAuthor
     */
    public void setMdAuthor(IInstituteRole mdAuthor) {
        this.mdAuthor = mdAuthor;
    }

    public boolean isUseDatasetCitationResponsibleParty() {
        return useDatasetCitationResponsibleParty;
    }

    /**
     * *
     * Sets whether the responsible party should be set (again) for the Citation
     * part.
     *
     * @param useDatasetCitationResponsibleParty
     */
    public void setUseDatasetCitationResponsibleParty(boolean useDatasetCitationResponsibleParty) {
        this.useDatasetCitationResponsibleParty = useDatasetCitationResponsibleParty;
    }

    public boolean isUseLinkedDatasetCitationResponsibleParty() {
        return useLinkedDatasetCitationResponsibleParty;
    }

    /**
     * *
     * Sets whether the responsible party should be set for all the
     * dependent/linked datasets.
     *
     * @param useLinkedDatasetCitationResponsibleParty
     */
    public void setUseLinkedDatasetCitationResponsibleParty(boolean useLinkedDatasetCitationResponsibleParty) {
        this.useLinkedDatasetCitationResponsibleParty = useLinkedDatasetCitationResponsibleParty;
    }

    public String getIdentifierAuthority() {
        return identifierAuthority;
    }

    public void setIdentifierAuthority(String identifierAuthority) {
        this.identifierAuthority = identifierAuthority;
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

        ROLE_MAPPING = new HashMap<>();
        ROLE_MAPPING.put(IDataset.Role.DISTRIBUTOR, org.opengis.metadata.citation.Role.DISTRIBUTOR);
        ROLE_MAPPING.put(IDataset.Role.RESOURCEPROVIDER, org.opengis.metadata.citation.Role.RESOURCE_PROVIDER);
        ROLE_MAPPING.put(IDataset.Role.OWNER, org.opengis.metadata.citation.Role.OWNER);
        ROLE_MAPPING.put(IDataset.Role.USER, org.opengis.metadata.citation.Role.USER);
        ROLE_MAPPING.put(IDataset.Role.PUBLISHER, org.opengis.metadata.citation.Role.PUBLISHER);
        ROLE_MAPPING.put(IDataset.Role.AUTHOR, org.opengis.metadata.citation.Role.AUTHOR);
        ROLE_MAPPING.put(IDataset.Role.POINT_OF_CONTACT, org.opengis.metadata.citation.Role.POINT_OF_CONTACT);
        ROLE_MAPPING.put(IDataset.Role.PRINCIPAL_INVESTIGATOR,
                org.opengis.metadata.citation.Role.PRINCIPAL_INVESTIGATOR);
        ROLE_MAPPING.put(IDataset.Role.PROCESSOR, org.opengis.metadata.citation.Role.PROCESSOR);
        ROLE_MAPPING.put(IDataset.Role.ORIGINATOR, org.opengis.metadata.citation.Role.ORIGINATOR);
        ROLE_MAPPING.put(IDataset.Role.CUSTODIAN, org.opengis.metadata.citation.Role.CUSTODIAN);

        SPATIAL_TYPE_MAPPING = new HashMap<>();
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.GRID, SpatialRepresentationType.GRID);
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.STEREO_MODEL, SpatialRepresentationType.STEREO_MODEL);
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.TEXT_TABLE, SpatialRepresentationType.TEXT_TABLE);
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.TIN, SpatialRepresentationType.TIN);
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.VECTOR, SpatialRepresentationType.VECTOR);
        SPATIAL_TYPE_MAPPING.put(IDataset.SpatialType.VIDEO, SpatialRepresentationType.VIDEO);

        KEYWORD_TYPE_MAPPING = new HashMap<>();
        KEYWORD_TYPE_MAPPING.put("discipline", KeywordType.DISCIPLINE);
        KEYWORD_TYPE_MAPPING.put("place", KeywordType.PLACE);
        KEYWORD_TYPE_MAPPING.put("stratum", KeywordType.STRATUM);
        KEYWORD_TYPE_MAPPING.put("temporal", KeywordType.TEMPORAL);
        KEYWORD_TYPE_MAPPING.put("theme", KeywordType.THEME);

        COUNTRIES = new HashMap<>();
        COUNTRIES.put("99", "Unknown");
        COUNTRIES.put("9X", "inapplicable");
        COUNTRIES.put("AD", "Andorra");
        COUNTRIES.put("AE", "United Arab Emirates");
        COUNTRIES.put("AF", "Afghanistan");
        COUNTRIES.put("AG", "Antigua and Barbuda");
        COUNTRIES.put("AI", "Anguilla");
        COUNTRIES.put("AL", "Albania");
        COUNTRIES.put("AM", "Armenia");
        COUNTRIES.put("AO", "Angola");
        COUNTRIES.put("AQ", "Antarctica");
        COUNTRIES.put("AR", "Argentina");
        COUNTRIES.put("AS", "American Samoa");
        COUNTRIES.put("AT", "Austria");
        COUNTRIES.put("AU", "Australia");
        COUNTRIES.put("AW", "Aruba");
        COUNTRIES.put("AX", "Åland Islands");
        COUNTRIES.put("AZ", "Azerbaijan");
        COUNTRIES.put("BA", "Bosnia and Herzegovina");
        COUNTRIES.put("BB", "Barbados");
        COUNTRIES.put("BD", "Bangladesh");
        COUNTRIES.put("BE", "Belgium");
        COUNTRIES.put("BF", "Burkina Faso");
        COUNTRIES.put("BG", "Bulgaria");
        COUNTRIES.put("BH", "Bahrain");
        COUNTRIES.put("BI", "Burundi");
        COUNTRIES.put("BJ", "Benin");
        COUNTRIES.put("BL", "Saint Barthélemy");
        COUNTRIES.put("BM", "Bermuda");
        COUNTRIES.put("BN", "Brunei Darussalam");
        COUNTRIES.put("BO", "Bolivia, Plurinational State of");
        COUNTRIES.put("BQ", "Bonaire, Sint Eustatius and Saba");
        COUNTRIES.put("BR", "Brazil");
        COUNTRIES.put("BS", "Bahamas");
        COUNTRIES.put("BT", "Bhutan");
        COUNTRIES.put("BV", "Bouvet Island");
        COUNTRIES.put("BW", "Botswana");
        COUNTRIES.put("BY", "Belarus");
        COUNTRIES.put("BZ", "Belize");
        COUNTRIES.put("CA", "Canada");
        COUNTRIES.put("CC", "Cocos (Keeling) Islands");
        COUNTRIES.put("CD", "Congo, The Democratic Republic of the");
        COUNTRIES.put("CF", "Central African Republic");
        COUNTRIES.put("CG", "Congo");
        COUNTRIES.put("CH", "Switzerland");
        COUNTRIES.put("CI", "Côte d'Ivoire");
        COUNTRIES.put("CK", "Cook Islands");
        COUNTRIES.put("CL", "Chile");
        COUNTRIES.put("CM", "Cameroon");
        COUNTRIES.put("CN", "China");
        COUNTRIES.put("CO", "Colombia");
        COUNTRIES.put("CR", "Costa Rica");
        COUNTRIES.put("CU", "Cuba");
        COUNTRIES.put("CV", "Cape Verde");
        COUNTRIES.put("CW", "Curaçao");
        COUNTRIES.put("CX", "Christmas Island");
        COUNTRIES.put("CY", "Cyprus");
        COUNTRIES.put("CZ", "Czech Republic");
        COUNTRIES.put("DE", "Germany");
        COUNTRIES.put("DJ", "Djibouti");
        COUNTRIES.put("DK", "Denmark");
        COUNTRIES.put("DM", "Dominica");
        COUNTRIES.put("DO", "Dominican Republic");
        COUNTRIES.put("DZ", "Algeria");
        COUNTRIES.put("EC", "Ecuador");
        COUNTRIES.put("EE", "Estonia");
        COUNTRIES.put("EG", "Egypt");
        COUNTRIES.put("EH", "Western Sahara");
        COUNTRIES.put("ER", "Eritrea");
        COUNTRIES.put("ES", "Spain");
        COUNTRIES.put("ET", "Ethiopia");
        COUNTRIES.put("FI", "Finland");
        COUNTRIES.put("FJ", "Fiji");
        COUNTRIES.put("FK", "Falkland Islands (Malvinas)");
        COUNTRIES.put("FM", "Micronesia, Federated States of");
        COUNTRIES.put("FO", "Faroe Islands");
        COUNTRIES.put("FR", "France");
        COUNTRIES.put("GA", "Gabon");
        COUNTRIES.put("GB", "United Kingdom");
        COUNTRIES.put("GD", "Grenada");
        COUNTRIES.put("GE", "Georgia");
        COUNTRIES.put("GF", "French Guiana");
        COUNTRIES.put("GG", "Guernsey");
        COUNTRIES.put("GH", "Ghana");
        COUNTRIES.put("GI", "Gibraltar");
        COUNTRIES.put("GL", "Greenland");
        COUNTRIES.put("GM", "Gambia");
        COUNTRIES.put("GN", "Guinea");
        COUNTRIES.put("GP", "Guadeloupe");
        COUNTRIES.put("GQ", "Equatorial Guinea");
        COUNTRIES.put("GR", "Greece");
        COUNTRIES.put("GS", "South Georgia and the South Sandwich Islands");
        COUNTRIES.put("GT", "Guatemala");
        COUNTRIES.put("GU", "Guam");
        COUNTRIES.put("GW", "Guinea-Bissau");
        COUNTRIES.put("GY", "Guyana");
        COUNTRIES.put("HK", "Hong Kong");
        COUNTRIES.put("HM", "Heard and McDonald Islands");
        COUNTRIES.put("HN", "Honduras");
        COUNTRIES.put("HR", "Croatia");
        COUNTRIES.put("HT", "Haiti");
        COUNTRIES.put("HU", "Hungary");
        COUNTRIES.put("ID", "Indonesia");
        COUNTRIES.put("IE", "Ireland");
        COUNTRIES.put("IL", "Israel");
        COUNTRIES.put("IM", "Isle Of Man");
        COUNTRIES.put("IN", "India");
        COUNTRIES.put("IO", "British Indian Ocean Territory");
        COUNTRIES.put("IQ", "Iraq");
        COUNTRIES.put("IR", "Iran, Islamic Republic of");
        COUNTRIES.put("IS", "Iceland");
        COUNTRIES.put("IT", "Italy");
        COUNTRIES.put("JE", "Jersey");
        COUNTRIES.put("JM", "Jamaica");
        COUNTRIES.put("JO", "Jordan");
        COUNTRIES.put("JP", "Japan");
        COUNTRIES.put("KE", "Kenya");
        COUNTRIES.put("KG", "Kyrgyzstan");
        COUNTRIES.put("KH", "Cambodia");
        COUNTRIES.put("KI", "Kiribati");
        COUNTRIES.put("KM", "Comoros");
        COUNTRIES.put("KN", "Saint Kitts and Nevis");
        COUNTRIES.put("KP", "Korea, Democratic People's Republic of");
        COUNTRIES.put("KR", "Korea, Republic of");
        COUNTRIES.put("KW", "Kuwait");
        COUNTRIES.put("KY", "Cayman Islands");
        COUNTRIES.put("KZ", "Kazakhstan");
        COUNTRIES.put("LA", "Lao People's Democratic Republic");
        COUNTRIES.put("LB", "Lebanon");
        COUNTRIES.put("LC", "Saint Lucia");
        COUNTRIES.put("LI", "Liechtenstein");
        COUNTRIES.put("LK", "Sri Lanka");
        COUNTRIES.put("LR", "Liberia");
        COUNTRIES.put("LS", "Lesotho");
        COUNTRIES.put("LT", "Lithuania");
        COUNTRIES.put("LU", "Luxembourg");
        COUNTRIES.put("LV", "Latvia");
        COUNTRIES.put("LY", "Libya");
        COUNTRIES.put("MA", "Morocco");
        COUNTRIES.put("MC", "Monaco");
        COUNTRIES.put("MD", "Moldova, Republic of");
        COUNTRIES.put("ME", "Montenegro");
        COUNTRIES.put("MF", "Saint Martin (French part)");
        COUNTRIES.put("MG", "Madagascar");
        COUNTRIES.put("MH", "Marshall Islands");
        COUNTRIES.put("MK", "Macedonia, The Former Yugoslav Republic of");
        COUNTRIES.put("ML", "Mali");
        COUNTRIES.put("MM", "Myanmar");
        COUNTRIES.put("MN", "Mongolia");
        COUNTRIES.put("MO", "Macau");
        COUNTRIES.put("MP", "Northern Mariana Islands");
        COUNTRIES.put("MQ", "Martinique");
        COUNTRIES.put("MR", "Mauritania");
        COUNTRIES.put("MS", "Montserrat");
        COUNTRIES.put("MT", "Malta");
        COUNTRIES.put("MU", "Mauritius");
        COUNTRIES.put("MV", "Maldives");
        COUNTRIES.put("MW", "Malawi");
        COUNTRIES.put("MX", "Mexico");
        COUNTRIES.put("MY", "Malaysia");
        COUNTRIES.put("MZ", "Mozambique");
        COUNTRIES.put("NA", "Namibia");
        COUNTRIES.put("NC", "New Caledonia");
        COUNTRIES.put("NE", "Niger");
        COUNTRIES.put("NF", "Norfolk Island");
        COUNTRIES.put("NG", "Nigeria");
        COUNTRIES.put("NI", "Nicaragua");
        COUNTRIES.put("NL", "Netherlands");
        COUNTRIES.put("NO", "Norway");
        COUNTRIES.put("NP", "Nepal");
        COUNTRIES.put("NR", "Nauru");
        COUNTRIES.put("NU", "Niue");
        COUNTRIES.put("NZ", "New Zealand");
        COUNTRIES.put("OM", "Oman");
        COUNTRIES.put("PA", "Panama");
        COUNTRIES.put("PE", "Peru");
        COUNTRIES.put("PF", "French Polynesia");
        COUNTRIES.put("PG", "Papua New Guinea");
        COUNTRIES.put("PH", "Philippines");
        COUNTRIES.put("PK", "Pakistan");
        COUNTRIES.put("PL", "Poland");
        COUNTRIES.put("PM", "Saint Pierre and Miquelon");
        COUNTRIES.put("PN", "Pitcairn");
        COUNTRIES.put("PR", "Puerto Rico");
        COUNTRIES.put("PS", "Palestinian Territory, Occupied");
        COUNTRIES.put("PT", "Portugal");
        COUNTRIES.put("PW", "Palau");
        COUNTRIES.put("PY", "Paraguay");
        COUNTRIES.put("QA", "Qatar");
        COUNTRIES.put("RE", "Réunion");
        COUNTRIES.put("RO", "Romania");
        COUNTRIES.put("RS", "Serbia");
        COUNTRIES.put("RU", "Russian Federation");
        COUNTRIES.put("RW", "Rwanda");
        COUNTRIES.put("SA", "Saudi Arabia");
        COUNTRIES.put("SB", "Solomon Islands");
        COUNTRIES.put("SC", "Seychelles");
        COUNTRIES.put("SD", "Sudan");
        COUNTRIES.put("SE", "Sweden");
        COUNTRIES.put("SG", "Singapore");
        COUNTRIES.put("SH", "Saint Helena, Ascension and Tristan da Cunha");
        COUNTRIES.put("SI", "Slovenia");
        COUNTRIES.put("SJ", "Svalbard and Jan Mayen");
        COUNTRIES.put("SK", "Slovakia");
        COUNTRIES.put("SL", "Sierra Leone");
        COUNTRIES.put("SM", "San Marino");
        COUNTRIES.put("SN", "Senegal");
        COUNTRIES.put("SO", "Somalia");
        COUNTRIES.put("SR", "Suriname");
        COUNTRIES.put("SS", "South Sudan");
        COUNTRIES.put("ST", "Sao Tome and Principe");
        COUNTRIES.put("SV", "El Salvador");
        COUNTRIES.put("SX", "Sint Maarten (Dutch part)");
        COUNTRIES.put("SY", "Syrian Arab Republic");
        COUNTRIES.put("SZ", "Swaziland");
        COUNTRIES.put("TC", "Turks and Caicos Islands");
        COUNTRIES.put("TD", "Chad");
        COUNTRIES.put("TF", "French Southern Territories");
        COUNTRIES.put("TG", "Togo");
        COUNTRIES.put("TH", "Thailand");
        COUNTRIES.put("TJ", "Tajikistan");
        COUNTRIES.put("TK", "Tokelau");
        COUNTRIES.put("TL", "Timor-Leste");
        COUNTRIES.put("TM", "Turkmenistan");
        COUNTRIES.put("TN", "Tunisia");
        COUNTRIES.put("TO", "Tonga");
        COUNTRIES.put("TR", "Turkey");
        COUNTRIES.put("TT", "Trinidad and Tobago");
        COUNTRIES.put("TV", "Tuvalu");
        COUNTRIES.put("TW", "Taiwan, Province of China");
        COUNTRIES.put("TZ", "Tanzania, United Republic of");
        COUNTRIES.put("UA", "Ukraine");
        COUNTRIES.put("UG", "Uganda");
        COUNTRIES.put("UM", "United States Minor Outlying Islands");
        COUNTRIES.put("US", "United States");
        COUNTRIES.put("UY", "Uruguay");
        COUNTRIES.put("UZ", "Uzbekistan");
        COUNTRIES.put("VA", "Holy See (Vatican City State)");
        COUNTRIES.put("VC", "Saint Vincent and The Grenadines");
        COUNTRIES.put("VE", "Venezuela, Bolivarian Republic of");
        COUNTRIES.put("VG", "Virgin Islands, British");
        COUNTRIES.put("VI", "Virgin Islands, U.S.");
        COUNTRIES.put("VN", "Viet Nam");
        COUNTRIES.put("VU", "Vanuatu");
        COUNTRIES.put("WF", "Wallis and Futuna");
        COUNTRIES.put("WS", "Samoa");
        COUNTRIES.put("YE", "Yemen");
        COUNTRIES.put("YT", "Mayotte");
        COUNTRIES.put("ZA", "South Africa");
        COUNTRIES.put("ZM", "Zambia");
        COUNTRIES.put("ZW", "Zimbabwe");

        LANGUAGES = new HashMap<>();
        LANGUAGES.put("NL", Arrays.asList(new Locale.Builder().setLanguage("Dutch").setLanguageTag("nl-BE").build())); /// *."NL","nld")*/
        LANGUAGES.put("FR", Arrays.asList(Locale.FRENCH)); // FRANCE
        LANGUAGES.put("EN", Arrays.asList(Locale.ENGLISH));
        LANGUAGES.put("DE", Arrays.asList(Locale.GERMAN)); // GERMANY

        /*
         * languages.put("NL/FR", Arrays.asList(new Locale("nld"), Locale.FRENCH));
         * languages.put("NL/FR/EN", Arrays.asList(new Locale("nld"), Locale.FRENCH,
         * Locale.ENGLISH));
         * languages.put("FR/EN", Arrays.asList(Locale.FRENCH, Locale.ENGLISH));
         */
        ROLES = new HashMap<>();
        ROLES.put(IDataset.Role.RESOURCEPROVIDER, Role.RESOURCE_PROVIDER);
        ROLES.put(IDataset.Role.PUBLISHER, Role.PUBLISHER);
        ROLES.put(IDataset.Role.AUTHOR, Role.AUTHOR);
        ROLES.put(IDataset.Role.CUSTODIAN, Role.CUSTODIAN);
        ROLES.put(IDataset.Role.OWNER, Role.OWNER);
        ROLES.put(IDataset.Role.USER, Role.USER);
        ROLES.put(IDataset.Role.DISTRIBUTOR, Role.DISTRIBUTOR);
        ROLES.put(IDataset.Role.ORIGINATOR, Role.ORIGINATOR);
        ROLES.put(IDataset.Role.POINT_OF_CONTACT, Role.POINT_OF_CONTACT);
        ROLES.put(IDataset.Role.PRINCIPAL_INVESTIGATOR, Role.PRINCIPAL_INVESTIGATOR);
        ROLES.put(IDataset.Role.PROCESSOR, Role.PROCESSOR);

        ONLINE_FUNCTIONS = new HashMap<>();
        ONLINE_FUNCTIONS.put(OnlinePossibilityEnum.DOWNLOAD, OnLineFunction.DOWNLOAD);
        ONLINE_FUNCTIONS.put(OnlinePossibilityEnum.INFORMATION, OnLineFunction.INFORMATION);
        ONLINE_FUNCTIONS.put(OnlinePossibilityEnum.OFFLINE_ACCESS, OnLineFunction.OFFLINE_ACCESS);
        ONLINE_FUNCTIONS.put(OnlinePossibilityEnum.ORDER, OnLineFunction.ORDER);
        ONLINE_FUNCTIONS.put(OnlinePossibilityEnum.SEARCH, OnLineFunction.SEARCH);

        INSPIRE_THEMES = new HashMap<>();
    }

    private ISO19115DatasetBuilder() {
        INSPIRE_THEMES.put(IDataset.InspireTheme.ADDRESSES,
                buildKeyword(Arrays.asList(new String[] { "Addresses" }), null, "GEMET - INSPIRE themes, version 1.0",
                        null, GEMET_INSPIRE_THEMES_THESAURUS_URL, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.ADMINISTRATIVE_UNITS,
                buildKeyword(Arrays.asList(new String[] { "Administrative units" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.AGRICULTURAL_AND_AQUACULTURE_FACILITIES,
                buildKeyword(Arrays.asList(new String[] { "Agricultural and aquaculture facilities" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.AREA_MANAGEMENT_RESTRICTION_REGULATION_ZONES_AND_REPORTING_UNITS,
                buildKeyword(
                        Arrays.asList(
                                new String[] { "Area management/restriction/regulation zones and reporting units" }),
                        null, "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.ATMOSPHERIC_CONDITIONS,
                buildKeyword(Arrays.asList(new String[] { "Atmospheric conditions" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.BIO_GEOGRAPHICAL_REGIONS,
                buildKeyword(Arrays.asList(new String[] { "Bio-geographical regions" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.BUILDINGS,
                buildKeyword(Arrays.asList(new String[] { "Buildings" }), null, "GEMET - INSPIRE themes, version 1.0",
                        null, GEMET_INSPIRE_THEMES_THESAURUS_URL, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.CADASTRAL_PARCELS,
                buildKeyword(Arrays.asList(new String[] { "Cadastral parcels" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.COORDINATE_REFERENCE_SYSTEMS,
                buildKeyword(Arrays.asList(new String[] { "Coordinate reference systems" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.ELEVATION,
                buildKeyword(Arrays.asList(new String[] { "Elevation" }), null, "GEMET - INSPIRE themes, version 1.0",
                        null, GEMET_INSPIRE_THEMES_THESAURUS_URL, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.ENERGY_RESOURCES,
                buildKeyword(Arrays.asList(new String[] { "Energy resources" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.ENVIRONMENTAL_MONITORING_FACILITIES,
                buildKeyword(Arrays.asList(new String[] { "Environmental monitoring facilities" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.GEOGRAPHICAL_GRID_SYSTEMS,
                buildKeyword(Arrays.asList(new String[] { "Geographical grid systems" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.GEOGRAPHICAL_NAMES,
                buildKeyword(Arrays.asList(new String[] { "Geographical names" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.GEOLOGY,
                buildKeyword(Arrays.asList(new String[] { "Geology" }), null, "GEMET - INSPIRE themes, version 1.0",
                        null, GEMET_INSPIRE_THEMES_THESAURUS_URL, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.HABITATS_AND_BIOTOPES,
                buildKeyword(Arrays.asList(new String[] { "Habitats and biotopes" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.HUMAN_HEALTH_AND_SAFETY,
                buildKeyword(Arrays.asList(new String[] { "Human health and safety" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.HYDROGRAPHY,
                buildKeyword(Arrays.asList(new String[] { "Hydrography" }), null, "GEMET - INSPIRE themes, version 1.0",
                        null, GEMET_INSPIRE_THEMES_THESAURUS_URL, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.LAND_COVER,
                buildKeyword(Arrays.asList(new String[] { "Land cover" }), null, "GEMET - INSPIRE themes, version 1.0",
                        null, GEMET_INSPIRE_THEMES_THESAURUS_URL, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.LAND_USE,
                buildKeyword(Arrays.asList(new String[] { "Land use" }), null, "GEMET - INSPIRE themes, version 1.0",
                        null, GEMET_INSPIRE_THEMES_THESAURUS_URL, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.METEOROLOGICAL_GEOGRAPHICAL_FEATURES,
                buildKeyword(Arrays.asList(new String[] { "Meteorological geographical features" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.MINERAL_RESOURCES,
                buildKeyword(Arrays.asList(new String[] { "Mineral resources" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.NATURAL_RISK_ZONES,
                buildKeyword(Arrays.asList(new String[] { "Natural risk zones" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.OCEANOGRAPHIC_GEOGRAPHICAL_FEATURES,
                buildKeyword(Arrays.asList(new String[] { "Oceanographic geographical features" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.ORTHOIMAGERY,
                buildKeyword(Arrays.asList(new String[] { "Orthoimagery" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.POPULATION_DISTRIBUTION_DEMOGRAPHY,
                buildKeyword(Arrays.asList(new String[] { "Population distribution — demography" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.PRODUCTION_AND_INDUSTRIAL_FACILITIES,
                buildKeyword(Arrays.asList(new String[] { "Production and industrial facilities" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.PROTECTED_SITES,
                buildKeyword(Arrays.asList(new String[] { "Protected sites" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.SEA_REGIONS,
                buildKeyword(Arrays.asList(new String[] { "Sea regions" }), null, "GEMET - INSPIRE themes, version 1.0",
                        null, GEMET_INSPIRE_THEMES_THESAURUS_URL, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.SOIL,
                buildKeyword(Arrays.asList(new String[] { "Soil" }), null, "GEMET - INSPIRE themes, version 1.0", null,
                        GEMET_INSPIRE_THEMES_THESAURUS_URL, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.SPECIES_DISTRIBUTION,
                buildKeyword(Arrays.asList(new String[] { "Species distribution" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.STATISTICAL_UNITS,
                buildKeyword(Arrays.asList(new String[] { "Statistical units" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.TRANSPORT_NETWORKS,
                buildKeyword(Arrays.asList(new String[] { "Transport networks" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));
        INSPIRE_THEMES.put(IDataset.InspireTheme.UTILITY_AND_GOVERNMENTAL_SERVICES,
                buildKeyword(Arrays.asList(new String[] { "Utility and governmental services" }), null,
                        "GEMET - INSPIRE themes, version 1.0", null, GEMET_INSPIRE_THEMES_THESAURUS_URL,
                        GEMET_INSPIRE_THEMES_PUBLICATION_DATE, "1.0"));

    }

    /**
     * *
     * Create a metadata builder based for a given dataset.
     *
     * @param dataset
     * @param inspire
     * @param useAnchor
     * @param useLinkedDatasetCitationResponsibleParty
     * @param useDatasetCitationResponsibleParty
     * @param mdAuthor
     * @param identifierAuthority
     */
    public ISO19115DatasetBuilder(IDataset dataset, boolean inspire, boolean useAnchor,
            boolean useDatasetCitationResponsibleParty, boolean useLinkedDatasetCitationResponsibleParty,
            IInstituteRole mdAuthor, String identifierAuthority) {
        this();
        if (dataset == null) {
            throw new IllegalArgumentException("Provided dataset argument is null.");
        }
        if (identifierAuthority == null) {
            throw new IllegalArgumentException(
                    "You must provide the name of the authority that issued this dataset's identifier.");
        }
        this.dataset = dataset;
        this.inspire = inspire;
        this.useAnchor = useAnchor;
        this.useDatasetCitationResponsibleParty = useDatasetCitationResponsibleParty;
        this.useLinkedDatasetCitationResponsibleParty = useLinkedDatasetCitationResponsibleParty;
        this.mdAuthor = mdAuthor;
        this.identifierAuthority = identifierAuthority;
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

    private String buildAnchorString(String url, String name) {
        if (name != null) {
            return "<gmx:Anchor xlink:href=\"" + url + "\">" + name + "</gmx:Anchor>";
        } else {
            return "<gmx:Anchor xlink:href=\"" + url + "\"/>";
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
    private Identifier buildGeneralIdentifier(String authorityName, String identifier, String name, boolean useAnchor) {
        if (authorityName == null || authorityName.equals("")) {
            if (useAnchor) {
                return new DefaultIdentifier(buildAnchorString(identifier, name));
            } else {
                return new DefaultIdentifier(identifier);
            }
        } else {
            if (useAnchor) {
                return new DefaultIdentifier(buildCitation(authorityName), buildAnchorString(identifier, name));
            } else {
                return new DefaultIdentifier(buildCitation(authorityName), identifier);
            }
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

    /**
     * *
     *
     * @param dataset
     * @param addResponsibleParties   Whether or not a gmd:CI_Citation needs a
     *                                gmd:citedResponsibleParty
     * @param addOtherCitationDetails Whether or not gmd:CI_Citation needs a a
     *                                concatanetion of the underlying data sources
     * @return
     */
    private DefaultCitation buildDatasetCitation(IDataset dataset, boolean addResponsibleParties,
            boolean addOtherCitationDetails) {
        DefaultCitation citation = new DefaultCitation();

        citation.setTitle(new SimpleInternationalString(dataset.getTitle()));
        List<CitationDate> dates = new ArrayList<>();

        dates.add(buildCreationDate(dataset.getCreationDate()));
        dates.add(buildRevisionDate(dataset.getRevisionDate()));
        dates.add(buildPublicationDate(Date.from(Instant.now())));

        citation.setDates(dates);

        Set<ResponsibleParty> responsibleParties = new HashSet<>();
        TreeSet<IDatasource> datasources = new TreeSet<>(new DatasourceComparator());
        datasources.addAll(dataset.getDatasources());

        // StringBuilder otherCitationDetailsSb = null;
        int i = 1;
        Set<String> otherCitationDetailsSet = new LinkedHashSet<>(); // no duplicates and preserve insert order.

        int nbCitations = datasources.size();
        if (addOtherCitationDetails) {
            if (datasources.size() > 0) {
                // otherCitationDetailsSb = new StringBuilder("Please cite as follows: ");
                otherCitationDetailsSet.add("Please cite as follows: ");
                otherCitationDetailsSet.add(LINE_SEPARATOR);
            }
        }
        for (IDatasource datasource : datasources) {
            if (addOtherCitationDetails) {
                if (nbCitations > 1) {
                    otherCitationDetailsSet.add(String.format("%s) ", i));
                }
                otherCitationDetailsSet.add(buildReference(datasource));
                if (i != nbCitations) {
                    otherCitationDetailsSet.add(" ");
                }
                otherCitationDetailsSet.add(LINE_SEPARATOR);
                i++;
            }
            if (addResponsibleParties && datasource.getParties() != null) { // authors, custodians, ...
                for (IInstituteRole party : datasource.getParties()) {
                    Role role = ROLES.get(party.getIsoRole());

                    DefaultResponsibleParty responsibleParty = buildResponsibleParty(role, party.getOrganisationName(),
                            party.getPhone(), party.getFax(), party.getDeliveryPoint(), party.getCity(),
                            party.getPostalCode(), party.getSdnCountryCode(), party.getEmailAddress(),
                            party.getWebsite());

                    if (responsibleParty != null) {
                        responsibleParties.add(responsibleParty);
                    }

                }
                citation.setCitedResponsibleParties(responsibleParties);
            }
        }
        if (otherCitationDetailsSet != null) {
            citation.setOtherCitationDetails(
                    singleton(new SimpleInternationalString(String.join("", otherCitationDetailsSet))));
        }

        Set<Identifier> identifiers = new HashSet<>();
        if (dataset.getExternaMetadataUrl() != null) {
            for (Map.Entry<String, String> entry : dataset.getExternaMetadataUrl().entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                identifiers.add(buildGeneralIdentifier(key, val, val, true));
            }
        }

        identifiers.add(buildGeneralIdentifier(identifierAuthority, dataset.getIdentifier(), null, false));
        citation.setIdentifiers(identifiers);

        return citation;
    }

    /***
     * Build an APA-style reference for the provided datasource.
     * 
     * @param datasource
     * @return
     */
    public String buildReference(IDatasource datasource) {
        if (datasource.getBibliographicReference() == null) {
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
                reference.append(". ");
            }
            reference.append((datasource.getSubtitle() != null) ? datasource.getSubtitle() : "");
            if (datasource.getSubtitle() != null && !datasource.getSubtitle().endsWith(".")) {
                reference.append(".");
            }
            return reference.toString();
        } else {
            return datasource.getBibliographicReference();
        }
    }

    private DefaultCitation buildDatasourceCitation(IDatasource datasource, boolean addResponsibleParties) {
        DefaultCitation citation = new DefaultCitation();
        List<CitationDate> dates = new ArrayList<>();
        CitationDate publicationDate = buildPublicationDate(datasource.getPublicationDate());
        if (publicationDate != null) {
            dates.add(publicationDate);
        }

        citation.setDates(dates);

        Set<ResponsibleParty> responsibleParties = new HashSet<>();

        if (addResponsibleParties && datasource.getParties() != null) {
            for (IInstituteRole party : datasource.getParties()) {
                Role role = ROLES.get(party.getIsoRole());
                ResponsibleParty responsibleParty = buildResponsibleParty(role, party.getOrganisationName(),
                        party.getPhone(), party.getFax(), party.getDeliveryPoint(), party.getCity(),
                        party.getPostalCode(), party.getSdnCountryCode(), party.getEmailAddress(), party.getWebsite());

                if (responsibleParty != null) {
                    responsibleParties.add(responsibleParty);
                }
            }
            citation.setCitedResponsibleParties(responsibleParties);
        }

        citation.setTitle(new SimpleInternationalString(buildReference(datasource)));

        citation.setIdentifiers(
                singleton(buildGeneralIdentifier(identifierAuthority, datasource.getIdentifier(), null, false)));
        return citation;
    }

    private SpatialRepresentationType buildSpatialRepresentationType() {
        return SPATIAL_TYPE_MAPPING.get(dataset.getSpatialType());
    }

    private DefaultDataIdentification buildIdentificationInfo(IDataset dataset) {
        DefaultDataIdentification identification = new DefaultDataIdentification();
        Set<ResponsibleParty> contacts = new HashSet<>();
        if (dataset.getPointsOfContact() != null) {
            for (IInstituteRole contact : dataset.getPointsOfContact()) {
                contacts.add(buildResponsibleParty(contact));
            }
        }
        if (!useDatasetCitationResponsibleParty) { // don't add the resp parties that have already been added in
                                                   // CI_Citation again
            for (IDatasource datasource : dataset.getDatasources()) {
                if (datasource.getParties() != null) {
                    for (IInstituteRole party : datasource.getParties()) {
                        Role role = ROLES.get(party.getIsoRole());
                        DefaultResponsibleParty responsibleParty = buildResponsibleParty(role,
                                party.getOrganisationName(), party.getPhone(), party.getFax(), party.getDeliveryPoint(),
                                party.getCity(), party.getPostalCode(), party.getSdnCountryCode(),
                                party.getEmailAddress(), party.getWebsite());
                        if (responsibleParty != null) {
                            contacts.add(responsibleParty);
                        }
                    }
                }
            }
        }
        identification.setPointOfContacts(contacts);
        if (dataset.getBrowseGraphicUrl() != null) {
            Set<BrowseGraphic> graphics = new HashSet<>();
            URI imageUri = URI.create(dataset.getBrowseGraphicUrl());
            DefaultBrowseGraphic browseGraphic = new DefaultBrowseGraphic();
            browseGraphic.setFileDescription(new DefaultInternationalString("large_thumbnail")); // A GeoNetwork shebang
                                                                                                 // to show the image as
                                                                                                 // a thumbnail.
            browseGraphic.setFileType(FilenameUtils.getExtension(dataset.getBrowseGraphicUrl()));
            browseGraphic.setFileName(imageUri);
            graphics.add(browseGraphic);
            identification.setGraphicOverviews(graphics);
        }

        identification.setCitation(buildDatasetCitation(dataset, useDatasetCitationResponsibleParty, true)); // don't
                                                                                                             // add the
                                                                                                             // resp.
                                                                                                             // parties
                                                                                                             // in the
                                                                                                             // citation.
        if (dataset.getAbstract() != null) {
            identification.setAbstract(new SimpleInternationalString(dataset.getAbstract()));
        } else {
            InternationalString abstractString = new SimpleInternationalString(
                    "No abstract has been provided for this dataset.");// NilReason.MISSING.createNilObject(InternationalString.class);

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
        List<TopicCategory> topics = new ArrayList<>();
        for (IDataset.TopicCategory topic : dataset.getTopicCategory()) {
            TopicCategory tc = topic == null ? null : TopicCategory.valueOf(topic.name());
            if (tc != null) {
                topics.add(tc);
            }
        }
        if (topics.isEmpty()) {
            topics.add(TopicCategory.OCEANS);
        }
        identification.setTopicCategories(topics);

        Collection<DefaultExtent> extent = buildExtent(dataset);

        if (extent != null) {
            identification.setExtents(extent);
        }
        TreeSet<IDatasource> datasources = new TreeSet<>(new DatasourceComparator());
        datasources.addAll(dataset.getDatasources());

        /*
         * List<IDatasource> datasources = dataset.getDatasources();
         * datasources.sort(new DatasourceComparator());
         */
        identification.setAggregationInfo(buildAggregateInformation(dataset.getMasterDatasets(), datasources));

        Set<Keywords> keywords = new LinkedHashSet<>(); // sort according to keyword characteristics

        keywords.addAll(buildKeywords(dataset.getKeywords()));

        identification.setDescriptiveKeywords(keywords);

        identification.setSpatialRepresentationTypes(singleton(buildSpatialRepresentationType()));

        OptionalDouble maxDistance = datasources.stream().map(ds -> ds.getSpatialResolutionDistance())
                .filter(ds -> Objects.nonNull(ds))
                .collect(Collectors.toList())
                .stream().mapToDouble(Double::doubleValue)
                .max();
        if (maxDistance.isPresent()) {
            identification.setSpatialResolutions(singleton(buildSpatialResolutionDistance(maxDistance.getAsDouble())));
        }

        OptionalLong maxScale = datasources.stream().map(ds -> ds.getSpatialResolutionScale())
                .filter(ds -> Objects.nonNull(ds))
                .collect(Collectors.toList())
                .stream().mapToLong(Long::longValue)
                .max();
        if (maxScale.isPresent()) {
            identification.setSpatialResolutions(singleton(buildSpatialResolutionScale(maxScale.getAsLong())));
        }
        return identification;
    }

    private Resolution buildSpatialResolutionDistance(double distance) {
        DefaultResolution r = new DefaultResolution();
        r.setDistance(distance);
        return r;
    }

    private Resolution buildSpatialResolutionScale(long scale) {
        DefaultResolution r = new DefaultResolution();
        DefaultRepresentativeFraction representativeFraction = new DefaultRepresentativeFraction();
        representativeFraction.setDenominator(scale);
        r.setEquivalentScale(representativeFraction);
        return r;
    }

    private SimpleInternationalString buildCountry(String sdnCountryCode) {
        String countryName = COUNTRIES.get(sdnCountryCode);
        return new SimpleInternationalString(countryName);
    }

    private Anchor buildCountryAnchor(String sdnCountryCode) {
        String countryName = COUNTRIES.get(sdnCountryCode);
        return new Anchor(URI.create("http://vocab.nerc.ac.uk/collection/C32/current/" + sdnCountryCode), countryName);
    }

    private List<Locale> buildLanguages(Set<String> languageCodes) {
        List<Locale> result = new ArrayList<>();
        for (String languageCode : languageCodes) {
            if (LANGUAGES.get(languageCode) != null) {
                result.addAll(LANGUAGES.get(languageCode));
            }
        }
        return result;
    }

    private DefaultResponsibleParty buildResponsibleParty(IInstituteRole contact) {
        return buildResponsibleParty(ROLE_MAPPING.get(contact.getIsoRole()), contact.getOrganisationName(),
                contact.getPhone(), contact.getFax(), contact.getDeliveryPoint(), contact.getCity(),
                contact.getPostalCode(), contact.getSdnCountryCode(), contact.getEmailAddress(), contact.getWebsite());
    }

    private DefaultResponsibleParty buildResponsibleParty(Role role, String organisationName, String phone, String fax,
            String deliveryPoint, String city, String postalCode, String sdnCountryCode, String email, String website) {
        DefaultResponsibleParty rp = null;
        if (role != null) {
            rp = new DefaultResponsibleParty(role);
            if (organisationName != null) {
                rp.setOrganisationName(new SimpleInternationalString(organisationName));
            }
            if (phone != null || fax != null || deliveryPoint != null || city != null || postalCode != null
                    || sdnCountryCode != null || email != null || website != null) {
                DefaultContact contact = new DefaultContact();
                Set<DefaultTelephone> phones = new HashSet<>();
                if (phone != null) {
                    DefaultTelephone t1 = new DefaultTelephone();
                    t1.setNumber(phone);
                    // t1.setNumberType(TelephoneType.VOICE);
                    phones.add(t1);
                }
                if (fax != null) {
                    DefaultTelephone t2 = new DefaultTelephone();
                    t2.setNumber(fax);
                    // t2.setNumberType(TelephoneType.FACSIMILE);
                    phones.add(t2);
                }
                contact.setPhones(phones);
                if (deliveryPoint != null || city != null || postalCode != null || sdnCountryCode != null
                        || email != null) {
                    DefaultAddress add = new DefaultAddress();
                    if (deliveryPoint != null) {
                        add.setDeliveryPoints(singleton(new SimpleInternationalString(deliveryPoint)));
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
        Set<IRegion> regions = new HashSet<>();
        for (IDatasource ds : dataset.getDatasources()) {
            if (ds.getRegionCollection() != null) {
                ds.getRegionCollection().stream().filter(o -> o != null).forEach(r -> regions.add(r));
            }
        }
        // geographic extent
        if (!regions.isEmpty() || (dataset.getWestBoundLon() != null && dataset.getEastBoundLon() != null
                && dataset.getSouthBoundLat() != null && dataset.getNorthBoundLat() != null)) {
            extent.setGeographicElements(buildGeographicExtent(regions, dataset.getWestBoundLon(),
                    dataset.getEastBoundLon(), dataset.getSouthBoundLat(), dataset.getNorthBoundLat()));
        }

        // temporal extent
        /*
         * if (dataset.getStartDate() != null && dataset.getEndDate() != null) {
         * DefaultTemporalExtent tempExtent = new DefaultTemporalExtent();
         * tempExtent.setBounds(new Date(2000,1,1), new Date(2000,1,1));
         * extent.setTemporalElements(singleton(tempExtent));
         * }
         */ // Done in the printer
        result.add(extent);
        return result;
    }

    private Set<GeographicExtent> buildGeographicExtent(Set<IRegion> regions, Double westBoundLon, Double eastBoundLon,
            Double southBoundLat, Double northBoundLat) {
        Set<GeographicExtent> extents = new LinkedHashSet<>(); // keep insertion order

        if (eastBoundLon != null && eastBoundLon == 0) {
            eastBoundLon = westBoundLon;
        }
        if (northBoundLat != null && northBoundLat == 0) {
            northBoundLat = southBoundLat;
        }
        if (westBoundLon != null && eastBoundLon != null && southBoundLat != null && northBoundLat != null) {
            GeographicExtent geo = new DefaultGeographicBoundingBox(westBoundLon, eastBoundLon, southBoundLat,
                    northBoundLat);
            extents.add(geo);
        }
        Set<GeographicDescription> descriptions = new TreeSet<>(new GeographicDescriptionComparator()); // have
                                                                                                        // permamnet
                                                                                                        // sort order

        if (regions != null) {
            for (IRegion region : regions) {
                GeographicDescription geoDesc = buildGeographicDescription("Vlaams Instituut voor de Zee (VLIZ)",
                        "http://marineregions.org/mrgid/" + region.getMRGID().toString(), region.getName());
                descriptions.add(geoDesc);
            }
        } else {
            GeographicDescription geoDesc = buildGeographicDescription("Bounding box");
            descriptions.add(geoDesc);
        }
        extents.addAll(descriptions);

        return extents;
    }

    private GeographicDescription buildGeographicDescription(String authorityName, String identifier, String name) {
        if (useAnchor) {
            return new DefaultGeographicDescription(buildCitation(authorityName), buildAnchorString(identifier, name));
        } else {
            return new DefaultGeographicDescription(buildCitation(authorityName), name);
        }
    }

    private GeographicDescription buildGeographicDescription(String name) {
        return new DefaultGeographicDescription(name);
    }

    private DefaultCitation buildThesaurusCitation(Thesaurus thesaurus) {
        return buildThesaurusCitation(thesaurus.getThesaurusTitle(), thesaurus.getThesaurusAltTitle(),
                thesaurus.getThesaurusUrl(), thesaurus.getThesaurusDate(), thesaurus.getThesaurusVersion());

    }

    private DefaultCitation buildThesaurusCitation(String thesaurusTitle, String thesaurusAltTitle, String thesaurusUrl,
            Date thesaurusPublicationDate, String thesaurusVersion) {
        // we create the citation describing the vocabulary used
        DefaultCitation citation = null;
        if (thesaurusTitle != null) {
            citation = new DefaultCitation();
            citation.setTitle(new SimpleInternationalString(thesaurusTitle));

            if (thesaurusUrl != null && useAnchor) {
                citation.setTitle(new Anchor(URI.create(thesaurusUrl), thesaurusTitle));
            } else {
                citation.setTitle(new SimpleInternationalString(thesaurusTitle));
            }

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
            // citation.setIdentifiers(singleton(new ImmutableIdentifier(null, null,
            // "http://www.seadatanet.org/urnurl/")));
        }
        return citation;
    }

    private Collection<Keywords> buildKeywords(Collection<IKeyword> keywords) {
        Collection<Keywords> allKeywords = new LinkedHashSet<>(); // TreeSet<>(new KeywordsComparator()); //maintain
                                                                  // insertion order
        if (keywords != null && !keywords.isEmpty()) {
            Map<Thesaurus, List<IKeyword>> intKeywords = new HashMap<>(); // order by thesaurus
            for (IKeyword keyword : keywords) {
                // Thesaurus thesaurus = new Thesaurus(keyword);
                // List<IKeyword> get = intKeywords.get(new Thesaurus(keyword));
                Thesaurus thesaurus = keyword.getThesaurus();
                List<IKeyword> get = intKeywords.get(thesaurus);
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

                // keyword.setType(type);
                Set<InternationalString> kws = new LinkedHashSet<>(); // maintain insertion order
                for (IKeyword iKeyword : keywordList) {
                    String url = iKeyword.getUrl();
                    if (url != null && useAnchor) {
                        kws.add(new Anchor(URI.create(url), iKeyword.getPrefLabel()));
                    } else {
                        kws.add(new SimpleInternationalString(iKeyword.getPrefLabel()));
                    }
                    individualKeyword.setType(KEYWORD_TYPE_MAPPING.get(iKeyword.getType())); // actually technically
                                                                                             // multiple types could be
                                                                                             // present within the
                                                                                             // keywords of one
                                                                                             // thesaurus.
                }

                individualKeyword.setKeywords(kws);
                if (thesaurus != null && !thesaurus.equals(Thesaurus.NO_THESAURUS)) {
                    individualKeyword.setThesaurusName(buildThesaurusCitation(thesaurus));
                }

                allKeywords.add(individualKeyword);
            }

        }
        return allKeywords;
    }

    private Keywords buildKeyword(Collection<String> keywords, KeywordType type, String thesaurusTitle,
            String thesaurusAltTitle, String thesaurusUrl, Date thesaurusPublicationDate, String thesaurusVersion) {
        DefaultKeywords keyword = new DefaultKeywords();
        keyword.setType(type);
        Set<InternationalString> kws = new HashSet<>();
        for (String kw : keywords) {
            kws.add(new SimpleInternationalString(kw));
        }
        keyword.setKeywords(kws);
        if (thesaurusTitle != null) {
            keyword.setThesaurusName(buildThesaurusCitation(thesaurusTitle, thesaurusAltTitle, thesaurusUrl,
                    thesaurusPublicationDate, thesaurusVersion));
        }

        return keyword;
    }

    private Set<Constraints> useConstraints = new LinkedHashSet<>();

    private void buildGeneralUseConstraints(String useConstraintString) {
        DefaultLegalConstraints useConstraint = new DefaultLegalConstraints();
        if (useConstraintString != null) {
            useConstraint.setUseConstraints(OTHER_RESTRICTIONS_PLACEHOLDER);
            useConstraint.setOtherConstraints(singleton(new SimpleInternationalString(useConstraintString)));
            useConstraints.add(useConstraint);
        }

    }

    private void buildUseConstraintsForLicense(String licenseURl) {
        DefaultLegalConstraints useConstraint = new DefaultLegalConstraints();
        useConstraint.setUseConstraints(OTHER_RESTRICTIONS_PLACEHOLDER);
        if (licenseURl == null) {
            if (useAnchor) {
                Anchor license = new Anchor(URI.create(DEFAULT_LICENSE), DEFAULT_LICENSE);
                if (inspire) {
                    Collection<Anchor> useConstraints = new HashSet<Anchor>();
                    Anchor inspireBazaar = new Anchor(URI.create(
                            "http://inspire.ec.europa.eu/metadata-codelist/ConditionsApplyingToAccessAndUse/noConditionsApply"),
                            "No conditions apply to access and use");
                    useConstraints.add(inspireBazaar);
                    useConstraints.add(license);
                    useConstraint.setOtherConstraints(useConstraints);
                } else {
                    useConstraint.setOtherConstraints(singleton(license));
                }
            } else {
                useConstraint.setOtherConstraints(singleton(new SimpleInternationalString(DEFAULT_LICENSE)));
            }
        } else {
            if (useAnchor) {
                Anchor license = new Anchor(URI.create(licenseURl), licenseURl);
                if (inspire) {
                    Collection<Anchor> useConstraints = new HashSet<Anchor>();
                    Anchor inspireBazaar = new Anchor(URI.create(
                            "http://inspire.ec.europa.eu/metadata-codelist/ConditionsApplyingToAccessAndUse/noConditionsApply"),
                            "No conditions apply to access and use");
                    useConstraints.add(inspireBazaar);
                    useConstraints.add(license);
                    useConstraint.setOtherConstraints(useConstraints);
                } else {
                    useConstraint.setOtherConstraints(singleton(license));
                }
            } else {
                useConstraint.setOtherConstraints(singleton(new SimpleInternationalString(licenseURl)));
            }
        }
        useConstraints.add(useConstraint); // to ensure that the default use constraint will not be added multiple times
    }

    private Set<Constraints> buildConstraints() {
        Set<Constraints> allConstraints = new LinkedHashSet<>(); // fixed order of addition

        buildUseConstraintsForLicense(dataset.getLicenseUrl());
        buildGeneralUseConstraints(dataset.getTermsUrl());
        buildGeneralUseConstraints(dataset.getLiability());
        allConstraints.addAll(useConstraints);

        DefaultLegalConstraints accessConstraints = new DefaultLegalConstraints();
        accessConstraints.setAccessConstraints(OTHER_RESTRICTIONS_PLACEHOLDER);
        if (dataset.getAccessLimitations() == null) {
            accessConstraints.setOtherConstraints(singleton(new SimpleInternationalString(DEFAULT_ACCESS_CONSTRAINTS)));
        } else {
            accessConstraints
                    .setOtherConstraints(singleton(new SimpleInternationalString(dataset.getAccessLimitations())));
        }
        allConstraints.add(accessConstraints);

        DefaultLegalConstraints useLimitation = new DefaultLegalConstraints();
        if (dataset.getUseConditions() == null) {
            useLimitation.setUseLimitations(singleton(new SimpleInternationalString(DEFAULT_USE_CONSTRAINTS))); // "No
                                                                                                                // conditions
                                                                                                                // apply
                                                                                                                // to
                                                                                                                // access
                                                                                                                // and
                                                                                                                // use."
        } else {
            useLimitation.setUseLimitations(singleton(new SimpleInternationalString(dataset.getUseConditions())));
        }
        allConstraints.add(useLimitation);

        DefaultSecurityConstraints securityConstraint = new DefaultSecurityConstraints();
        securityConstraint.setClassification(Classification.UNCLASSIFIED);
        allConstraints.add(securityConstraint);
        return allConstraints;
    }

    private Set<Format> buildDumbFormat(Collection<IDistributionResource> distributionResources) {
        Set<IDistributionFormat> formats = distributionResources.stream().map(dr -> dr.getDistributionFormats())
                .flatMap(l -> l.stream()).collect(Collectors.toCollection(LinkedHashSet::new)); // maintain insertion
                                                                                                                                                                                                        // order
        return buildFormat(formats);
    }

    private Set<Format> buildFormat(Set<IDistributionFormat> distributionFormats) {
        Set<Format> formats = new LinkedHashSet<>(); // maintain insertion order
        for (IDistributionFormat distributionFormat : distributionFormats) {
            DefaultFormat format = new DefaultFormat();

            if (useAnchor && distributionFormat.getDistributionFormatUrn() != null) {
                format.setName(new Anchor(URI.create(distributionFormat.getDistributionFormatUrn()),
                        distributionFormat.getDistributionFormatMime().toString()));
            } else {
                format.setName(
                        new SimpleInternationalString(distributionFormat.getDistributionFormatMime().toString()));
            }

            if (distributionFormat.getDistributionFormatVersion() != null) {
                format.setVersion(new SimpleInternationalString(distributionFormat.getDistributionFormatVersion()));
            } else {
                format.setVersion(
                        /* NilReason.UNKNOWN.createNilObject(InternationalString.class) */new SimpleInternationalString(
                                "Unknown"));
            }
            formats.add(format);
            // }
        }
        return formats;
    }

    private Distribution buildDistributionInfo(IDataset dataset) {
        if (dataset.getDistributionResources() != null && !dataset.getDistributionResources().isEmpty()) {
            DefaultDistribution distributionInfo = new DefaultDistribution();

            distributionInfo.setDistributionFormats(buildDumbFormat(dataset.getDistributionResources()));

            for (IDistributionResource distributionResource : dataset.getDistributionResources()) {
                URL url = distributionResource.getOnlineResourceUrl();
                if (url != null) {
                    DefaultDistributor distributor = new DefaultDistributor();
                    distributor.setDistributorContact(buildResponsibleParty(distributionResource.getDistributor()));
                    distributor.setDistributorFormats(
                            buildFormat(new HashSet<>(distributionResource.getDistributionFormats())));

                    Double dataSizeInMegaBytes = distributionResource.getDataSizeInMegaBytes();
                    DefaultDigitalTransferOptions digiTrans = new DefaultDigitalTransferOptions();
                    if (dataSizeInMegaBytes != null && !dataSizeInMegaBytes.isNaN() && !dataSizeInMegaBytes.isInfinite()
                            && dataSizeInMegaBytes > 0) {
                        digiTrans.setTransferSize(distributionResource.getDataSizeInMegaBytes());
                    }
                    distributor.setDistributorTransferOptions(singleton(digiTrans));

                    Set<OnlineResource> resources = new HashSet<>();
                    DefaultOnlineResource resource = new DefaultOnlineResource();

                    try {
                        URI u = new URI(url.toString().replace(" ", "%20"));
                        resource.setLinkage(u);
                    } catch (URISyntaxException ex) {
                        resource = null;
                        LOG.log(Level.SEVERE,
                                "The url of the distributionResource (" + url + ") is invalid.", ex);

                    }
                    if (resource != null) {
                        if (distributionResource.getOnlineResourceDescription() != null) {
                            resource.setDescription(
                                    new SimpleInternationalString(distributionResource.getOnlineResourceDescription()));
                        } else {
                            resource.setDescription(new SimpleInternationalString(
                                    "Link to " + distributionResource.getOnlineResourceUrl().toString()));
                        }
                        if (distributionResource.getFunction() != null) {
                            resource.setFunction(ONLINE_FUNCTIONS.get(distributionResource.getFunction()));
                        }
                        if (distributionResource.getOnlineResourceName() != null) {
                            resource.setName(
                                    new SimpleInternationalString(distributionResource.getOnlineResourceName())); // either
                                                                                                                                   // a
                                                                                                                                   // human
                                                                                                                                   // readable
                                                                                                                                   // name
                        }
                        // or an identifier on a wfs
                        resource.setProtocol(distributionResource.getOnlineResourceProtocol().getOfficialProtocol());
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
        Set<DataQuality> qualities = new THashSet<>();
        // scope
        DefaultDataQuality quality = new DefaultDataQuality(DATASET_SCOPE_QUALITY);
        // lineage
        if (dataset.getLineage() != null) {
            DefaultLineage lineage = new DefaultLineage();
            lineage.setStatement(new SimpleInternationalString(dataset.getLineage()));
            quality.setLineage(lineage);
        }
        // reports
        if (inspire) {
            DefaultDomainConsistency inspireCompliancy = new DefaultDomainConsistency();
            DefaultConformanceResult result = new DefaultConformanceResult();
            result.setPass(Boolean.TRUE);
            result.setSpecification(buildInspireCitation());
            result.setExplanation(new SimpleInternationalString("See the referenced specification"));

            inspireCompliancy.setResults(singleton(result));

            quality.setReports(singleton(inspireCompliancy));
        }
        qualities.add(quality);
        return qualities;

    }

    private DefaultCitation buildInspireCitation() {
        DefaultCitation citation = new DefaultCitation();
        citation.setTitle(buildAnchor("https://eur-lex.europa.eu/eli/reg/2010/1089",
                "COMMISSION REGULATION (EU) No 1089/2010 of 23 November 2010 implementing Directive 2007/2/EC of the European Parliament and of the Council as regards interoperability of spatial data sets and services"));
        citation.setDates(
                singleton(buildCitationDate(INSPIRE_IMPLEMENTING_RULES_PUBLICATION_DATE, DateType.PUBLICATION, null)));
        return citation;
    }

    private Set<DefaultAggregateInformation> buildAggregateInformation(Collection<IDataset> masterDatasets,
            Collection<IDatasource> datasources) {

        Set<DefaultAggregateInformation> aggregateInfos = new HashSet<>();
        if (masterDatasets != null) {
            for (IDataset dataset : masterDatasets) {
                DefaultAggregateInformation aggregateInfo = new DefaultAggregateInformation();
                DefaultCitation citation = buildDatasetCitation(dataset, useLinkedDatasetCitationResponsibleParty,
                        false); // don't print all the responsible parties and the otherCitationDetails of the
                                                                                                                                   // parent dataset.

                aggregateInfo.setAggregateDataSetName(citation);
                aggregateInfo.setInitiativeType(InitiativeType.COLLECTION);
                aggregateInfo.setAssociationType(AssociationType.LARGER_WORD_CITATION);
                aggregateInfos.add(aggregateInfo);
            }
        }
        /*
         * if (datasources != null) { //ignore this for now as it is not really
         * informative
         * for (IDatasource datasource : datasources) {
         * DefaultAggregateInformation aggregateInfo = new
         * DefaultAggregateInformation();
         * DefaultCitation citation = buildDatasourceCitation(datasource,
         * useLinkedDatasetCitationResponsibleParty);
         * 
         * aggregateInfo.setAggregateDataSetName(citation);
         * aggregateInfo.setInitiativeType(InitiativeType.COLLECTION);
         * aggregateInfo.setAssociationType(AssociationType.SOURCE);
         * aggregateInfos.add(aggregateInfo);
         * }
         * }
         */
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
            RScitation.setIdentifiers(
                    singleton(new ImmutableIdentifier(null, null, "http://vocab.nerc.ac.uk/collection/L10/current")));

            if (useAnchor) {
                RScitation.setEdition(new Anchor(URI.create("SDN:C371:1:2"), "3"));
            } else {
                RScitation.setEdition(new SimpleInternationalString("3"));
            }

            identifier = new ImmutableIdentifier(RScitation, "L10", epsgCode);
        } /*
           * else if (inspire) {
           * identifier = new ImmutableIdentifier(null, "EPSG",
           * "http://www.opengis.net/def/crs/EPSG/0/3035");
           * }
           */ else {
            identifier = new ImmutableIdentifier(null, "EPSG", "http://www.opengis.net/def/crs/EPSG/0/" + epsgCode);
        }

        ReferenceSystemMetadata rs = new ReferenceSystemMetadata(identifier);
        return rs;
    }

    public String getFileName() {
        return dataset.getMetadataFileName();
    }

    /**
     * *
     * Construct the ISO 19115 metadata content of the Dataset provided in the
     * constructor, or return the already constructed metadata if done so.
     *
     * @param inspire   Whether the metadata content should take into account
     *                  INSPIRE specificities.
     * @param useAnchor Whether the metadata content should construct
     *                  gmx:Anchors or stick to gco:Characterstrings.
     * @param mdAuthor  the author of the metadata.
     * @return
     */
    public DefaultMetadata getMetadataDocument() {
        if (dataset == null) {
            throw new NullPointerException("The dataset is null. Cannot create DefaultMetadata.");
        }
        if (metadata == null) {// only create the metadata if it's requested for the first time.
            metadata = new DefaultMetadata();
            metadata.setMetadataStandards(Citations.ISO_19115);
            metadata.getLanguages().add(Locale.ENGLISH);

            if (mdAuthor != null) {
                metadata.setContacts(singleton(buildResponsibleParty(mdAuthor)));
            }
            Charset charset = Charset.forName("UTF-8");
            metadata.getCharacterSets().add(charset);

            final Identifier fileIdentifier = new DefaultIdentifier(dataset.getIdentifier());
            metadata.setMetadataIdentifier(fileIdentifier);

            // final Date creation = new Date();
            Date creation = Date.from(Instant.MAX);
            metadata.setDateInfo(singleton(new DefaultCitationDate(creation, DateType.CREATION)));

            metadata.setMetadataScopes(singleton(DATASET_SCOPE));

            metadata.setReferenceSystemInfo(singleton(buildReferenceSystem(dataset.getEpsgCode(), false)));

            metadata.setIdentificationInfo(singleton(buildIdentificationInfo(dataset)));
            Distribution distributionInfo = buildDistributionInfo(dataset);
            if (distributionInfo != null) {
                metadata.setDistributionInfo(singleton(distributionInfo));
            }
            metadata.setDataQualityInfo(buildDataQualityInfo(dataset));
        }
        return metadata;
    }

    public String toString() {
        return this.getMetadataDocument().toString();
    }

}
