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

    GLG_KML_20_http_get_map("GLG:KML-2.0-http-get-map", "A KML getMap service"),
    OGC_KML("OGC:KML", "An OGC KML service"),
    INSPIRE_ATOM("INSPIRE-ATOM", "An INSPIRE ATOM service"),
    OGC_WPS("OGC:WPS", "A WPS service"),
    OGC_WFS("OGC:WFS", "A WFS service"),
    OGC_WMS("OGC:WMS", "A WMS service"),
    OGC_WCS("OGC:WCS", "A WCS service"),
    DB_POSTGIS("DB:POSTGIS", "A PostGIS connection"),
    WWW_LINK_10_http_opendap("WWW:LINK-1.0-http--opendap", "An OPeNDAP link"),
    WWW_LINK_10_http_link("WWW:LINK-1.0-http--link", "An HTTP link"),
    WWW_DOWNLOAD_10_http_download("WWW:DOWNLOAD-1.0-http--download", "An HTTP link"),
    WWW_DOWNLOAD_10_ftp_download("WWW:DOWNLOAD-1.0-ftp--download", "An FTP");

    private final String officialProtocol;
    private final String humanReadableProtocol;

    public String getOfficialProtocol() {
        return officialProtocol;
    }

    public String getHumanReadableProtocol() {
        return humanReadableProtocol;
    }

    ProtocolEnum(String officialProtocol, String humanReadableProtocol) {
        this.officialProtocol = officialProtocol;
        this.humanReadableProtocol = humanReadableProtocol;
    }
}
