/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.impl;

import be.naturalsciences.bmdc.metadata.model.IDistributionFormat;
import be.naturalsciences.bmdc.metadata.model.IDistributionResource;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import be.naturalsciences.bmdc.metadata.model.OnlinePossibilityEnum;
import be.naturalsciences.bmdc.metadata.model.ProtocolEnum;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class DistributionResource implements IDistributionResource {

    private OnlinePossibilityEnum function;
    private URL onlineResourceUrl;
    private ProtocolEnum onlineResourceProtocol;
    private String onlineResourceName;
    private String onlineResourceDescription;
    private Double dataSizeInBytes;
    private IInstituteRole distributor;
    private List<IDistributionFormat> distributionFormats;

    public DistributionResource(OnlinePossibilityEnum function, URL onlineResourceUrl, ProtocolEnum onlineResourceProtocol, String onlineResourceName, String onlineResourceDescription, List<IDistributionFormat> distributionFormats, Double dataSizeInBytes, IInstituteRole distributor) {
        this.function = function;
        this.onlineResourceUrl = onlineResourceUrl;
        this.onlineResourceProtocol = onlineResourceProtocol;
        this.onlineResourceName = onlineResourceName;
        this.onlineResourceDescription = onlineResourceDescription;
        this.distributionFormats = distributionFormats;
        this.dataSizeInBytes = dataSizeInBytes;
        this.distributor = distributor;
    }

    public OnlinePossibilityEnum getFunction() {
        return function;
    }

    public void setFunction(OnlinePossibilityEnum function) {
        this.function = function;
    }

    public URL getOnlineResourceUrl() {
        return onlineResourceUrl;
    }

    public void setOnlineResourceUrl(URL onlineResourceUrl) {
        this.onlineResourceUrl = onlineResourceUrl;
    }

    public ProtocolEnum getOnlineResourceProtocol() {
        return onlineResourceProtocol;
    }

    public void setOnlineResourceProtocol(ProtocolEnum onlineResourceProtocol) {
        this.onlineResourceProtocol = onlineResourceProtocol;
    }

    public String getOnlineResourceName() {
        return onlineResourceName;
    }

    public void setOnlineResourceName(String onlineResourceName) {
        this.onlineResourceName = onlineResourceName;
    }

    public String getOnlineResourceDescription() {
        return onlineResourceDescription;
    }

    public void setOnlineResourceDescription(String onlineResourceDescription) {
        this.onlineResourceDescription = onlineResourceDescription;
    }

    @Override
    public List<IDistributionFormat> getDistributionFormats() {
        return distributionFormats;
    }

    @Override
    public void setDistributionFormats(List<IDistributionFormat> distributionFormats) {
        this.distributionFormats = distributionFormats;
    }

    @Override
    public Double getDataSizeInMB() {
        return dataSizeInBytes;
    }

    @Override
    public void setDataSizeInBytes(Double dataSizeInBytes) {
        this.dataSizeInBytes = dataSizeInBytes;
    }

    @Override
    public IInstituteRole getDistributor() {
        return distributor;
    }

    @Override
    public void setDistributors(IInstituteRole distributor) {
        this.distributor = distributor;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (this == object) {
            return true;
        }
        if (!(object instanceof DistributionResource)) {
            return false;
        }
        DistributionResource other = (DistributionResource) object;
        return this.getOnlineResourceUrl().equals(other.getOnlineResourceUrl());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.onlineResourceUrl);
        return hash;
    }
}
