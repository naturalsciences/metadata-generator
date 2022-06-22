/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.impl;

import be.naturalsciences.bmdc.metadata.model.IDatasource;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import be.naturalsciences.bmdc.metadata.model.IParameter;
import be.naturalsciences.bmdc.metadata.model.IPlatform;
import be.naturalsciences.bmdc.metadata.model.IProject;
import be.naturalsciences.bmdc.metadata.model.IRegion;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Datasource implements IDatasource {

    String title;
    String subtitle;
    String abstractText;
    String identifier;
    Date publicationDate;
    String authorList;
    List<IInstituteRole> parties;
    Set<String> languages;
    List<IProject> projects;
    List<IParameter> parameters;
    List<IPlatform> platforms;
    List<IRegion> regions;
    private String bibliographicReference;
    private String spatialRepresentation;
    private Double spatialResolutionDistance;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getAuthorList() {
        return authorList;
    }

    public void setAuthorList(String authorList) {
        this.authorList = authorList;
    }

    public List<IInstituteRole> getParties() {
        return parties;
    }

    public void setParties(List<IInstituteRole> parties) {
        this.parties = parties;
    }

    @Override
    public Set<String> getLanguages() {
        return languages;
    }

    @Override
    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public List<IProject> getProjects() {
        return projects;
    }

    public void setProjects(List<IProject> projects) {
        this.projects = projects;
    }

    public List<IParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<IParameter> parameters) {
        this.parameters = parameters;
    }

    public List<IPlatform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<IPlatform> platforms) {
        this.platforms = platforms;
    }

    public Datasource(String title, String subtitle, String identifier, Date publicationDate, String authorList, List<IInstituteRole> parties, Set<String> languages, List<IProject> projects, List<IParameter> paramaters, List<IPlatform> platforms, List<IRegion> regions, String bibliographicReference) {
        this.title = title;
        this.subtitle = subtitle;
        this.identifier = identifier;
        this.publicationDate = publicationDate;
        this.authorList = authorList;
        this.parties = parties;
        this.languages = languages;
        this.projects = projects;
        this.parameters = paramaters;
        this.platforms = platforms;
        this.regions = regions;
        this.bibliographicReference = bibliographicReference;
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
    public List<IRegion> getRegionCollection() {
        return regions;
    }

    @Override
    public String getBibliographicReference() {
        return this.bibliographicReference;
    }

    @Override
    public void setBibliographicReference(String reference) {
        this.bibliographicReference = bibliographicReference;

    }

    /*@Override
    public String getSpatialRepresentation() {
        return this.spatialRepresentation;
    }

    @Override
    public void setSpatialRepresentation(String spatialRepresentation) {
        this.spatialRepresentation = spatialRepresentation;
    }*/

    @Override
    public Double getSpatialResolutionDistance() {
        return this.spatialResolutionDistance;
    }

    @Override
    public void setSpatialResolutionDistance(Double spatialResolutionDistance) {
        this.spatialResolutionDistance = spatialResolutionDistance;
    }

}
