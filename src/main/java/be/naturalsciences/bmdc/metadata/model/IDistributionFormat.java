/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model;

/**
 *
 * @author thomas
 */
public interface IDistributionFormat {

    String getDistributionFormatUrn();

    void setDistributionFormatUrn(String distributionFormatUrn);

    FormatEnum getDistributionFormatMime();

    void setDistributionFormatMime(FormatEnum distributionFormatMime);

    String getDistributionFormatVersion();

    void setDistributionFormatVersion(String distributionFormatVersion);

    public String getGeoserverName();

    public void setGeoserverName(String geoserverName);

    public String getDistributionFormatNiceName();

    public void setDistributionFormatNiceName(String niceName);

}
