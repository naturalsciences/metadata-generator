/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.impl;

import be.naturalsciences.bmdc.metadata.model.FormatEnum;
import be.naturalsciences.bmdc.metadata.model.IDistributionFormat;
import java.util.Objects;

public class DistributionFormat implements IDistributionFormat {
    
    static final long serialVersionUID = 1L;

    public static final DistributionFormat SHAPEFILE = new DistributionFormat("Zipped Shapefile archive", FormatEnum.SHAPE_ZIP, "shape-zip", null, null);
    public static final DistributionFormat GML = new DistributionFormat("GML", FormatEnum.GML, "gml3", null, null);
    public static final DistributionFormat JSON = new DistributionFormat("JSON", FormatEnum.JSON, "json", null, null);
    public static final DistributionFormat CSV = new DistributionFormat("CSV", FormatEnum.CSV, "csv", null, null);
    public static final IDistributionFormat EXCEL = new DistributionFormat("Excel", FormatEnum.EXCEL, null, null, null);
    public static final IDistributionFormat ATOM = new DistributionFormat("ATOM", FormatEnum.ATOM, null, null, null);
    public static final IDistributionFormat UNKNOWN = new DistributionFormat("Unknown", FormatEnum.UNKNOWN, null, null, null);
    public static final IDistributionFormat XML = new DistributionFormat("XML", FormatEnum.XML, null, null, null);
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

    @Override
    public String getDistributionFormatUrn() {
        return distributionFormatUrn;
    }

    @Override
    public void setDistributionFormatUrn(String distributionFormatUrn) {
        this.distributionFormatUrn = distributionFormatUrn;
    }

    @Override
    public FormatEnum getDistributionFormatMime() {
        return distributionFormatMime;
    }

    @Override
    public void setDistributionFormatMime(FormatEnum distributionFormatMime) {
        this.distributionFormatMime = distributionFormatMime;
    }

    @Override
    public String getDistributionFormatVersion() {
        return distributionFormatVersion;
    }

    @Override
    public void setDistributionFormatVersion(String distributionFormatVersion) {
        this.distributionFormatVersion = distributionFormatVersion;
    }

    @Override
    public String getGeoserverName() {
        return geoserverName;
    }

    @Override
    public void setGeoserverName(String geoserverName) {
        this.geoserverName = geoserverName;
    }

    @Override
    public String getDistributionFormatNiceName() {
        return niceName;
    }

    @Override
    public void setDistributionFormatNiceName(String niceName) {
        this.niceName = niceName;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (this == object) {
            return true;
        }
        if (!(object instanceof DistributionFormat)) {
            return false;
        }
        DistributionFormat other = (DistributionFormat) object;
        return this.getDistributionFormatMime() == other.getDistributionFormatMime();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.distributionFormatMime);
        return hash;
    }

}
