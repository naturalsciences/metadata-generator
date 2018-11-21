/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.impl;

import be.naturalsciences.bmdc.metadata.model.IDataset;
import be.naturalsciences.bmdc.metadata.model.IDatasource;
import be.naturalsciences.bmdc.metadata.model.IDistributionResource;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import be.naturalsciences.bmdc.metadata.model.IKeyword;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Dataset implements IDataset {

    String identifier;
    String title;
    Date creationDate;
    Date revisionDate;
    Set<String> languages;
    String epsgCode;
    List<IInstituteRole> parties;
    List<IDataset> masterDatasets;
    List<IDatasource> datasources;
    String abstractText;
    Date startDate;
    Date endDate;
    Double southBoundLat;
    Double northBoundLat;
    Double westBoundLon;
    Double eastBoundLon;
    Collection<IDistributionResource> distributionResources;
    String lineage;
    Set<InspireTheme> themes;
    private Collection<IInstituteRole> pointsOfContact;
    private Collection<IKeyword> keywords;
    private String isoDialect;
    private SpatialType spatialType;
    //private String fileIdentifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public String getEpsgCode() {
        return epsgCode;
    }

    public void setEpsgCode(String epsgCode) {
        this.epsgCode = epsgCode;
    }

    public List<IInstituteRole> getParties() {
        return parties;
    }

    public void setParties(List<IInstituteRole> parties) {
        this.parties = parties;
    }

    public List<IDataset> getMasterDatasets() {
        return masterDatasets;
    }

    public void setMasterDatasets(List<IDataset> masterDatasets) {
        this.masterDatasets = this.masterDatasets;
    }

    public List<IDatasource> getDatasources() {
        return datasources;
    }

    public void setDatasources(List<IDatasource> datasources) {
        this.datasources = datasources;
    }

    @Override
    public String getAbstract() {
        return abstractText;
    }

    @Override
    public void setAbstract(String abstractText) {
        this.abstractText = abstractText;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Double getSouthBoundLat() {
        return southBoundLat;
    }

    @Override
    public void setSouthBoundLat(Double southBoundLat) {
        this.southBoundLat = southBoundLat;
    }

    @Override
    public Double getNorthBoundLat() {
        return northBoundLat;
    }

    @Override
    public void setNorthBoundLat(Double northBoundLat) {
        this.northBoundLat = northBoundLat;
    }

    @Override
    public Double getWestBoundLon() {
        return westBoundLon;
    }

    @Override
    public void setWestBoundLon(Double westBoundLon) {
        this.westBoundLon = westBoundLon;
    }

    @Override
    public Double getEastBoundLon() {
        return eastBoundLon;
    }

    @Override
    public void setEastBoundLon(Double eastBoundLon) {
        this.eastBoundLon = eastBoundLon;
    }

    @Override
    public String getLineage() {
        return lineage;
    }

    @Override
    public void setLineage(String lineage) {
        this.lineage = lineage;
    }

    @Override
    public void setPointsOfContact(Collection<IInstituteRole> pointsOfContact) {
        this.pointsOfContact = pointsOfContact;
    }

    @Override
    public Collection<IInstituteRole> getPointsOfContact() {
        return this.pointsOfContact;
    }

    public Dataset(String identifier, String title, Date creationDate, Date revisionDate, Set<String> languages, String epsgCode, List<IInstituteRole> parties, List<IDataset> innerDatasets, List<IDatasource> datasources, String abstractText, Date startDate, Date endDate, Double southBoundLat, Double northBoundLat, Double westBoundLon, Double eastBoundLon, Collection<IDistributionResource> distributionResources, String lineage, Set<InspireTheme> themes, Collection<IInstituteRole> pointsOfContact, SpatialType spatialType, Collection<IKeyword> keywords) {
        this.identifier = identifier;
        this.title = title;
        this.creationDate = creationDate;
        this.revisionDate = revisionDate;
        this.languages = languages;
        this.epsgCode = epsgCode;
        this.parties = parties;
        this.masterDatasets = innerDatasets;
        this.datasources = datasources;
        this.abstractText = abstractText;
        this.startDate = startDate;
        this.endDate = endDate;
        this.southBoundLat = southBoundLat;
        this.northBoundLat = northBoundLat;
        this.westBoundLon = westBoundLon;
        this.eastBoundLon = eastBoundLon;
        this.distributionResources = distributionResources;
        this.lineage = lineage;
        this.themes = themes;
        this.pointsOfContact = pointsOfContact;
        this.spatialType = spatialType;
        this.keywords = keywords;
    }

    @Override
    public void setKeywords(Collection<IKeyword> keywords) {
        this.keywords = keywords;
    }

    @Override
    public Collection<IKeyword> getKeywords() {
        return this.keywords;
    }

    @Override
    public void setIsoDialect(String isoDialect) {
        this.isoDialect = isoDialect;
    }

    @Override
    public String getIsoDialect() {
        return isoDialect;
    }

    @Override
    public boolean isInspire() {
        return isoDialect.equals("ISO 19115_INSPIRE");
    }

    @Override
    public Collection<IDistributionResource> getDistributionResources() {
        return distributionResources;
    }

    @Override
    public void setDistributionResources(Collection<IDistributionResource> distributionResources) {
        this.distributionResources = distributionResources;
    }

    @Override
    public SpatialType getSpatialType() {
        return this.spatialType;
    }

    @Override
    public void setSpatialType(SpatialType spatialType) {
        this.spatialType = spatialType;
    }

    @Override
    public String getFileName() {
        return this.identifier + ".xml";
    }

    @Override
    public String getMetadataUrlXML() {
        return "http://metadata.naturalsciences.be/" + identifier + ".xml";
    }

    @Override
    public String getMetadataUrlHuman() {
        return "http://metadata.naturalsciences.be/" + identifier;
    }

}
