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
public enum ProtocolEnum {
    GLG_KML_20_http_get_map {
        public String toString() {
            return "GLG:KML-2.0-http-get-map";
        }
    },
    OGC_KML {
        public String toString() {
            return "OGC:KML";
        }
    },
    INSPIRE_ATOM {
        public String toString() {
            return "INSPIRE-ATOM";
        }
    },
    OGC_WPS {
        public String toString() {
            return "OGC:WPS";
        }
    },
    OGC_WFS {
        public String toString() {
            return "OGC:WFS";
        }
    },
    OGC_WMS {
        public String toString() {
            return "OGC:WMS";
        }
    },
    OGC_WCS {
        public String toString() {
            return "OGC:WCS";
        }
    },
    DB_POSTGIS {
        public String toString() {
            return "DB:POSTGIS";
        }
    },
    WWW_LINK_10_http_opendap {
        public String toString() {
            return "WWW:LINK-1.0-http--opendap";
        }
    },
    WWW_LINK_10_http_link {
        public String toString() {
            return "WWW:LINK-1.0-http--link";
        }
    },
    WWW_DOWNLOAD_10_http_download {
        public String toString() {
            return "WWW:DOWNLOAD-1.0-http--download";
        }
    },
    WWW_DOWNLOAD_10_ftp_download {
        public String toString() {
            return "WWW:DOWNLOAD-1.0-ftp--download";
        }
    };

}
