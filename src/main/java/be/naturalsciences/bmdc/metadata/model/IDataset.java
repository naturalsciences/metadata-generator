/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model;

import be.naturalsciences.bmdc.utils.LocalizedString;
import be.naturalsciences.bmdc.metadata.model.IDatasource;
import be.naturalsciences.bmdc.metadata.model.IDistributionResource;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import be.naturalsciences.bmdc.metadata.model.IKeyword;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author thomas
 */
public interface IDataset extends Serializable {

    static final long serialVersionUID = 1L;

    public enum Role {
        DISTRIBUTOR, RESOURCEPROVIDER, COAUTHOR, EDITOR, CONTRIBUTOR, OWNER, USER, STAKEHOLDER, RIGHTS_HOLDER, FUNDER, PUBLISHER, AUTHOR, POINT_OF_CONTACT, PRINCIPAL_INVESTIGATOR, MEDIATOR, PROCESSOR, ORIGINATOR, CUSTODIAN, COLLABORATOR, SPONSOR
    }

    public enum InspireTheme {
        ADDRESSES, ADMINISTRATIVE_UNITS, AGRICULTURAL_AND_AQUACULTURE_FACILITIES, AREA_MANAGEMENT_RESTRICTION_REGULATION_ZONES_AND_REPORTING_UNITS, ATMOSPHERIC_CONDITIONS, BIO_GEOGRAPHICAL_REGIONS, BUILDINGS, CADASTRAL_PARCELS, COORDINATE_REFERENCE_SYSTEMS, ELEVATION, ENERGY_RESOURCES, ENVIRONMENTAL_MONITORING_FACILITIES, GEOGRAPHICAL_GRID_SYSTEMS, GEOGRAPHICAL_NAMES, GEOLOGY, HABITATS_AND_BIOTOPES, HUMAN_HEALTH_AND_SAFETY, HYDROGRAPHY, LAND_COVER, LAND_USE, METEOROLOGICAL_GEOGRAPHICAL_FEATURES, MINERAL_RESOURCES, NATURAL_RISK_ZONES, OCEANOGRAPHIC_GEOGRAPHICAL_FEATURES, ORTHOIMAGERY, POPULATION_DISTRIBUTION_DEMOGRAPHY, PRODUCTION_AND_INDUSTRIAL_FACILITIES, PROTECTED_SITES, SEA_REGIONS, SOIL, SPECIES_DISTRIBUTION, STATISTICAL_UNITS, TRANSPORT_NETWORKS, UTILITY_AND_GOVERNMENTAL_SERVICES
    }

    public enum SpatialType {
        VECTOR, GRID, STEREO_MODEL, TEXT_TABLE, TIN, VIDEO
    }

    public String getIdentifier();

    public void setIdentifier(String identifier);

    public String getTitle();

    public void setTitle(String title);

    public Set<LocalizedString> getMultilingualTitles();

    public Set<LocalizedString> getMultilingualAbstracts();

    public Set<LocalizedString> getMultilingualLineages();

    public void setMultilingualTitles(Set<LocalizedString> titles);

    public void setMultilingualAbstracts(Set<LocalizedString> abstracts);

    public void setMultilingualLineages(Set<LocalizedString> lineages);

    public Date getCreationDate();

    public void setCreationDate(Date creationDate);

    public Date getRevisionDate();

    public void setRevisionDate(Date revisionDate);

    public Set<String> getLanguages();

    public void setLanguages(Set<String> languages);

    public String getEpsgCode();

    public void setEpsgCode(String epsgCode);

    public List<IInstituteRole> getParties();

    public void setParties(List<IInstituteRole> parties);

    public List<IDataset> getMasterDatasets();

    public void setMasterDatasets(List<IDataset> masterDatasets);

    public List<IDatasource> getDatasources();

    public void setDatasources(List<IDatasource> datasources);

    public String getAbstract();

    public void setAbstract(String abstractText);

    public String getLineage();

    public void setLineage(String lineage);

    public Date getStartDate();

    public void setStartDate(Date startDate);

    public Date getEndDate();

    public void setEndDate(Date endDate);

    public Double getSouthBoundLat();

    public void setSouthBoundLat(Double southBoundLat);

    public Double getNorthBoundLat();

    public void setNorthBoundLat(Double northBoundLat);

    public Double getWestBoundLon();

    public void setWestBoundLon(Double westBoundLon);

    public Double getEastBoundLon();

    public void setEastBoundLon(Double eastBoundLon);

    public Collection<IDistributionResource> getDistributionResources();

    public void setDistributionResources(Collection<IDistributionResource> distributionResources);

    public boolean addDistributionResource(String baseUrl, String distributionResourceIdentifier, String distributionResourceDescriptiveName, String distributionResourceDescription, Map<String, String> urlArgumentValues, OnlinePossibilityEnum onlinePossibility, ProtocolEnum protocol, List<IDistributionFormat> formats) throws MalformedURLException, URISyntaxException;

    public void setPointsOfContact(Collection<IInstituteRole> pointsOfContact);

    public Collection<IInstituteRole> getPointsOfContact();

    public void setKeywords(Collection<IKeyword> keywords);

    public Collection<IKeyword> getKeywords();

    public void setIsoDialect(String isoDialect);

    public String getIsoDialect();

    public boolean isInspire();

    public SpatialType getSpatialType();

    public void setSpatialType(SpatialType spatialType);

    public String getFileName();

    public String getMetadataUrlXML();

    public String getMetadataUrlHuman();
}
