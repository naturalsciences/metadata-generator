/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.impl;

import be.naturalsciences.bmdc.metadata.model.FormatEnum;
import be.naturalsciences.bmdc.metadata.model.IDistributionFormat;

public class DistributionFormat implements IDistributionFormat {

    public static final DistributionFormat SHAPEFILE = new DistributionFormat("Zipped Shapefile archive", FormatEnum.SHAPE_ZIP, "shape-zip", null, null);
    public static final DistributionFormat GML = new DistributionFormat("GML", FormatEnum.GML, "gml3", null, null);
    public static final DistributionFormat JSON = new DistributionFormat("JSON", FormatEnum.JSON, "json", null, null);
    public static final DistributionFormat CSV = new DistributionFormat("CSV", FormatEnum.CSV, "csv", null, null);
    public static final IDistributionFormat EXCEL = new DistributionFormat("Excel", FormatEnum.EXCEL, null, null, null);
    public static final IDistributionFormat ATOM = new DistributionFormat("ATOM", FormatEnum.ATOM, null, null, null);
    public static final IDistributionFormat NETCDF = new DistributionFormat("NetCDF", FormatEnum.NETCDF, null, null, null);

    private String distributionFormatUrn;
    private FormatEnum distributionFormatMime;
    private String distributionFormatVersion;
    private String geoserverName;
    private String niceName;

    public DistributionFormat(String niceName, FormatEnum distributionFormatMime, String geoserverName, String distributionFormatVersion, String distributionFormatUrn) {
        this.distributionFormatUrn = distributionFormatUrn;
        this.distributionFormatMime = distributionFormatMime;
        this.distributionFormatVersion = distributionFormatVersion;
        this.geoserverName = geoserverName;
        this.niceName = niceName;
    }

    public String getDistributionFormatUrn() {
        return distributionFormatUrn;
    }

    public void setDistributionFormatUrn(String distributionFormatUrn) {
        this.distributionFormatUrn = distributionFormatUrn;
    }

    public FormatEnum getDistributionFormatMime() {
        return distributionFormatMime;
    }

    public void setDistributionFormatMime(FormatEnum distributionFormatMime) {
        this.distributionFormatMime = distributionFormatMime;
    }

    public String getDistributionFormatVersion() {
        return distributionFormatVersion;
    }

    public void setDistributionFormatVersion(String distributionFormatVersion) {
        this.distributionFormatVersion = distributionFormatVersion;
    }

    public String getGeoserverName() {
        return geoserverName;
    }

    public void setGeoserverName(String geoserverName) {
        this.geoserverName = geoserverName;
    }

    public String getDistributionFormatNiceName() {
        return niceName;
    }

    public void setDistributionFormatNiceName(String niceName) {
        this.niceName = niceName;
    }

}
