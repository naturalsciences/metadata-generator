/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author thomas
 */
public interface IDatasource {

    public String getTitle();

    public void setTitle(String title);

    public String getSubtitle();

    public void setSubtitle(String subtitle);

    public String getAbstract();

    public void setAbstract(String subtitle);

    public String getIdentifier();

    public void setIdentifier(String identifier);

    public Set<String> getLanguages();

    public void setLanguages(Set<String> languages);

    public Date getPublicationDate();

    public void setPublicationDate(Date publicationDate);

    public String getAuthorList();

    public void setAuthorList(String authorList);

    public List<IInstituteRole> getParties();

    public void setParties(List<IInstituteRole> parties);

    public List<IProject> getProjects();

    public void setProjects(List<IProject> projects);

    public List<IParameter> getParameters();

    public void setParameters(List<IParameter> paramaters);

    public List<IPlatform> getPlatforms();

    public void setPlatforms(List<IPlatform> platforms);

    public List<IRegion> getRegionCollection();
}
