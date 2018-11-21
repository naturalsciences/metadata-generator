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
public enum FormatEnum {
    UNKNOWN {
        public String toString() {
            return "";
        }
    },
    ATOM {
        public String toString() {
            return "application/atom+xml";
        }
    },
    GML {
        public String toString() {
            return "application/gml+xml";
        }
    },
    XML {
        public String toString() {
            return "application/xml";
        }
    },
    JSON {
        public String toString() {
            return "application/json";
        }
    },
    KML {
        public String toString() {
            return "application/vnd.google-earth.kml xml";
        }
    },
    GEOPACKAGE {
        public String toString() {
            return "application/x-gpkg";
        }
    },
    CSV {
        public String toString() {
            return "text/csv";
        }
    },
    SHAPE_ZIP {
        public String toString() {
            return "application/x-shapefile";
        }
    },
    NETCDF {
        public String toString() {
            return "application/x-netcdf";
        }
    },
    OGR_CSV {
        public String toString() {
            return "OGR-CSV";
        }
    },
    OGR_FileGDB {
        public String toString() {
            return "OGR-FileGDB";
        }
    },
    OGR_GPKG {
        public String toString() {
            return "OGR-GPKG";
        }
    },
    OGR_KML {
        public String toString() {
            return "OGR-KML";
        }
    },
    OGR_MIF {
        public String toString() {
            return "OGR-MIF";
        }
    },
    OGR_TAB {
        public String toString() {
            return "OGR-TAB";
        }
    }, EXCEL {
        public String toString() {
            return "application/vnd.ms-excel";
        }
    };

}
