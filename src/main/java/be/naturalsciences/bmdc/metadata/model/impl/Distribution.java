/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.impl;

import be.naturalsciences.bmdc.metadata.model.IDistribution;
import be.naturalsciences.bmdc.metadata.model.IDistributionFormat;
import be.naturalsciences.bmdc.metadata.model.IDistributionResource;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import java.util.Collection;
import java.util.List;

public class Distribution implements IDistribution {

    private Collection<IDistributionFormat> distributionFormats;
    private Collection<IDistributionResource> distributionResources;
    private Double dataSizeInMb;
    private Collection<IInstituteRole> distributors;

    public Distribution(Collection<IDistributionFormat> distributionFormats, Collection<IDistributionResource> distributionResources, Double dataSizeInMb, List<IInstituteRole> distributors) {
        this.distributionFormats = distributionFormats;
        this.distributionResources = distributionResources;
        this.dataSizeInMb = dataSizeInMb;
        this.distributors = distributors;
    }

    public Collection<IDistributionFormat> getDistributionFormats() {
        return distributionFormats;
    }

    public void setDistributionFormats(Collection<IDistributionFormat> distributionFormats) {
        this.distributionFormats = distributionFormats;
    }

    public Collection<IDistributionResource> getDistributionResources() {
        return distributionResources;
    }

    public void setDistributionResources(Collection<IDistributionResource> distributionResources) {
        this.distributionResources = distributionResources;
    }

    public Double getDataSizeInMb() {
        return dataSizeInMb;
    }

    public void setDataSizeInMb(Double dataSizeInMb) {
        this.dataSizeInMb = dataSizeInMb;
    }

    public Collection<IInstituteRole> getDistributors() {
        return distributors;
    }

    public void setDistributors(Collection<IInstituteRole> distributors) {
        this.distributors = distributors;
    }

}
