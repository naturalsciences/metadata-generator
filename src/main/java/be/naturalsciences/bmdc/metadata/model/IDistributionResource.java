/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model;

import java.net.URL;
import java.util.List;

/**
 *
 * @author thomas
 */
public interface IDistributionResource {

    OnlinePossibilityEnum getFunction();

    void setFunction(OnlinePossibilityEnum function);

    URL getOnlineResourceUrl();

    void setOnlineResourceUrl(URL onlineResourceUrl);

    ProtocolEnum getOnlineResourceProtocol();

    void setOnlineResourceProtocol(ProtocolEnum onlineResourceProtocol);

    String getOnlineResourceName();

    void setOnlineResourceName(String onlineResourceName);

    String getOnlineResourceDescription();

    void setOnlineResourceDescription(String onlineResourceDescription);

    List<IDistributionFormat> getDistributionFormats();

    void setDistributionFormats(List<IDistributionFormat> formats);

    Double getDataSizeInMB();

    void setDataSizeInBytes(Double dataSizeInBytes);

    public IInstituteRole getDistributor();

    public void setDistributors(IInstituteRole distributor);

}
