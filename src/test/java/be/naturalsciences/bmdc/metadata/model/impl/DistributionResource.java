/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.impl;

import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import be.naturalsciences.bmdc.metadata.model.IDistributionFormat;
import be.naturalsciences.bmdc.metadata.model.IDistributionResource;
import be.naturalsciences.bmdc.metadata.model.OnlinePossibilityEnum;
import be.naturalsciences.bmdc.metadata.model.ProtocolEnum;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class DistributionResource implements IDistributionResource, Serializable {

    static final long serialVersionUID = 1L;

    private OnlinePossibilityEnum function;
    private URL onlineResourceUrl;
    private ProtocolEnum onlineResourceProtocol;
    //private String onlineResourceName;

    private List<IDistributionFormat> distributionFormats;
    private Double dataSizeInMegaBytes;
    private IInstituteRole distributor;
    private String onlineResourceIdentifier;
    private String onlineResourceDescriptiveName;
    private String onlineResourceDescription;
    private String crs;

    public DistributionResource(OnlinePossibilityEnum function, URL onlineResourceUrl, ProtocolEnum onlineResourceProtocol, String onlineResourceIdentifier, String onlineResourceDescriptiveName, String onlineResourceDescription, List<IDistributionFormat> distributionFormats, Double dataSizeInBytes, IInstituteRole distributor) {
        this.function = function;
        this.onlineResourceUrl = onlineResourceUrl;
        this.onlineResourceProtocol = onlineResourceProtocol;
        this.distributionFormats = distributionFormats;
        this.dataSizeInMegaBytes = dataSizeInBytes;
        this.distributor = distributor;
        this.onlineResourceIdentifier = onlineResourceIdentifier;
        this.onlineResourceDescriptiveName = onlineResourceDescriptiveName;
        this.onlineResourceDescription = onlineResourceDescription;
    }

    @Override
    public OnlinePossibilityEnum getFunction() {
        return function;
    }

    @Override
    public void setFunction(OnlinePossibilityEnum function) {
        this.function = function;
    }

    @Override
    public URL getOnlineResourceUrl() {
        return onlineResourceUrl;
    }

    @Override
    public void setOnlineResourceUrl(URL onlineResourceUrl) {
        this.onlineResourceUrl = onlineResourceUrl;
    }

    @Override
    public ProtocolEnum getOnlineResourceProtocol() {
        return onlineResourceProtocol;
    }

    @Override
    public void setOnlineResourceProtocol(ProtocolEnum onlineResourceProtocol) {
        this.onlineResourceProtocol = onlineResourceProtocol;
    }

    public String getOnlineResourceDescription() {
        return onlineResourceDescription;
    }

    @Override
    public void setOnlineResourceDescription(String onlineResourceDescription) {
        this.onlineResourceDescription = onlineResourceDescription;
    }

    @Override
    public String getOnlineResourceDescriptiveName() {
        return onlineResourceDescriptiveName;
    }

    @Override
    public void setOnlineResourceDescriptiveName(String onlineResourceDescriptiveName) {
        this.onlineResourceDescriptiveName = onlineResourceDescriptiveName;
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
    public Double getDataSizeInMegaBytes() {
        return dataSizeInMegaBytes;
    }

    @Override
    public void setDataSizeInMegaBytes(Double dataSizeInBytes) {
        this.dataSizeInMegaBytes = dataSizeInBytes;
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
        return this.getOnlineResourceUrl().equals(other.getOnlineResourceUrl()) && this.getOnlineResourceProtocol().equals(other.getOnlineResourceProtocol());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.onlineResourceUrl);
        hash = 37 * hash + Objects.hashCode(this.onlineResourceProtocol);
        return hash;
    }

    @Override
    public String getOnlineResourceIdentifier() {
        return onlineResourceIdentifier;
    }

    @Override
    public void setOnlineResourceIdentifier(String onlineResourceIdentifier) {
        this.onlineResourceIdentifier = onlineResourceIdentifier;
    }

    @Override
    public boolean isInspire() {
        return false;
    }

    @Override
    public String getCrs() {
        return this.crs;
    }

    @Override
    public void setCrs(String crs) {
        this.crs = crs;
    }

}
