/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.datacite;

import be.naturalsciences.bmdc.metadata.iso.ISO19115DatasetPrinter;
import be.naturalsciences.bmdc.metadata.iso.IsoMetadataModifier;
import be.naturalsciences.bmdc.utils.JavaUtils;
import be.naturalsciences.bmdc.utils.StringUtils;
import be.naturalsciences.bmdc.utils.xml.XMLElement;
import be.naturalsciences.bmdc.metadata.datacite.mds.account.Account;
import be.naturalsciences.bmdc.metadata.datacite.metadata.Metadata;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPClient;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPRequest;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPResponse;
import java.io.File;
import java.io.IOException;
import be.naturalsciences.bmdc.utils.xml.XMLUtils;
import be.naturalsciences.bmdc.utils.xml.XMLUtils.IValidationResult;
import be.naturalsciences.bmdc.metadata.datacite.mds.doi.PostDOI;
import be.naturalsciences.bmdc.metadata.datacite.mds.metadata.GetMetadata;
import be.naturalsciences.bmdc.metadata.datacite.mds.metadata.IPostMetadata;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.metadata.InvalidMetadataException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A Class that creates a DataCite DOI and associated metadata in one operation
 * based on ISO 19115 metadata.
 *
 * @author thomas
 */
public final class ISO19115toDataCitePublisher implements IPostMetadata, IsoMetadataModifier {

    private static final String ISO_2_DC_XSLT_FILENAME = "iso2datacite3.xsl";
    private static URL DATACITE_4_XSD;

    private static String METADATA_RBINS_URL_PREFIX = "http://metadata.naturalsciences.be/";

    static {
        try {
            DATACITE_4_XSD = new URL("http://schema.datacite.org/meta/kernel-4.1/metadata.xsd");
        } catch (MalformedURLException ex) {//cannot happen
            Logger.getLogger(ISO19115toDataCitePublisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private final Account account;
    private String xmlDCMetadata;
    private String datasetIdentifier;
    private Document isoMetadata;
    private boolean dCMetadataValid = true;

    /**
     * *
     * Post a valid DataCite metadata xml file to DataCite given a specific
     * account
     *
     * @param account
     * @param file
     */
    public ISO19115toDataCitePublisher(Account account) {
        this.account = account;
    }

    private static InputStream getXsltDocument() {
        return JavaUtils.getResource(ISO_2_DC_XSLT_FILENAME, ISO19115toDataCitePublisher.class);
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public String getDCMetadata() {
        return xmlDCMetadata;
    }

    @Override
    public String getDoi() {
        return account.getPrefix() + "/" + datasetIdentifier;
    }

    @Override
    public void setIsoMetadata(Document isoMetadata) throws InvalidMetadataException {
        this.isoMetadata = isoMetadata;
        updateDatasetIdentifier();
        updateDCMetadata();
    }

    public void setIsoMetadata(File isoMetadata) throws InvalidMetadataException {
        ISO19115toDataCitePublisher.this.updateISOMetadata(isoMetadata);
        updateDatasetIdentifier();
        updateDCMetadata();
    }

    public void setIsoMetadata(String isoMetadata) throws InvalidMetadataException {
        ISO19115toDataCitePublisher.this.updateISOMetadata(isoMetadata);
        updateDatasetIdentifier();
        updateDCMetadata();
    }

    @Override
    public Document getIsoMetadata() {
        return isoMetadata;
    }

    public String getISOMetadata() {
        return XMLUtils.toXML(isoMetadata);
    }

    private void updateDCMetadata() throws InvalidMetadataException {
        try {
            String dcMd = XMLUtils.xsltTransform(getXsltDocument(), getISOMetadata(), 2, false); //the GeoNetwork xslt requires version 2!; the simple one works with version 1
            //System.out.println(account);
            System.out.println(account.getPrefix());
            dcMd = dcMd.replaceAll("doiPrefixPlaceholder", account.getPrefix());
            this.xmlDCMetadata = dcMd;
            IValidationResult result = XMLUtils.validateXMLAgainstXSD(DATACITE_4_XSD, dcMd);
            if (!result.isValid()) {
                dCMetadataValid = false;
                throw new InvalidMetadataException("No DataCite metadata generated for dataset " + datasetIdentifier + ". The ISO metadata is invalid because its (xslt) transformation result failed against the DataCite 4.1 metadata specification (XSD). Message: " + result.getMessage() + "|| Metadata: " + StringUtils.flattenString(dcMd));
            }

        } catch (IOException | URISyntaxException | TransformerException ex) {
            throw new IllegalArgumentException("No DataCite metadata generated for dataset " + datasetIdentifier + ". The provided ISO metadata is invalid because it could not be converted to DataCite (xslt conversion).", ex);
        }
    }

    private void updateISOMetadata(File file) {
        String isoMetadata = "";
        try {
            isoMetadata = Metadata.getMetadataFromFile(file);
        } catch (IOException ex) {
            throw new IllegalArgumentException("The provided file is invalid (doesn't exist, nonreadable).", ex);
        }
        ISO19115toDataCitePublisher.this.updateISOMetadata(isoMetadata);
    }

    private void updateISOMetadata(String xmlISOMetadata) {
        try {
            isoMetadata = XMLUtils.toDocument(xmlISOMetadata);
        } catch (JAXBException ex) {
            Logger.getLogger(ISO19115toDataCitePublisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ISO19115toDataCitePublisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ISO19115toDataCitePublisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ISO19115toDataCitePublisher.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateDatasetIdentifier();
    }

    @Override
    public boolean doiAlreadyExists() throws Exception {
        if (datasetIdentifier == null) {
            throw new IllegalStateException("Could not verify the existance of this doi on DataCite as it is not set.");
        }
        GetMetadata get = new GetMetadata(account, account.getPrefix() + "/" + datasetIdentifier);
        HTTPResponse result;
        try {
            result = get.execute();
            return result.getResponseCode() == 200;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void updateDatasetIdentifier() {
        List<String> fileIdentifiers = XMLUtils.xpathQueryString(isoMetadata, "//gmd:fileIdentifier/gco:CharacterString/text()", ISO19115DatasetPrinter.MD_NAMESPACES);
        String fileIdentifier = fileIdentifiers.get(0);
        datasetIdentifier = fileIdentifier;
    }

    @Override
    public void updateOriginalMetadata() {
        if (isoMetadata != null) {
            XMLElement element = new XMLElement("gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date[last()]", null, null, null);
            List<Node> nodes;
            try {
                nodes = XMLUtils.xpathQueryNodes(isoMetadata, element.toXPath(), ISO19115DatasetPrinter.MD_NAMESPACES); //is the element even present in the document??
                XMLUtils.pasteAfter(isoMetadata, nodes, getIdentifierSnippet(), ISO19115DatasetPrinter.MD_NAMESPACES);
                Logger.getLogger(ISO19115toDataCitePublisher.class.getName()).log(Level.INFO, "Added doi MD_Identifier for dataset " + datasetIdentifier);
            } catch (Exception ex) {
                Logger.getLogger(ISO19115toDataCitePublisher.class.getName()).log(Level.SEVERE, "There was a problem appending the XML", ex);
            }
        }
    }

    @Override
    public HTTPResponse execute() throws Exception {
        if (!dCMetadataValid) {
            throw new IllegalStateException("Could not execute posting the DataCite XML when it is not valid.");
        }

        HTTPRequest request = new HTTPRequest();
        request.setMethod(HTTPRequest.Method.POST);
        request.setURL(Account.METADATA_SERVICE_ADDRESS);

        request.setUsername(account.getUserName());
        request.setPassword(account.getPassword());

        request.setContentType("application/xml;charset=UTF-8");
        request.setBody(xmlDCMetadata);

        HTTPResponse response = HTTPClient.doHTTPRequest(request);

        PostDOI doiPoster = new PostDOI(account, METADATA_RBINS_URL_PREFIX + datasetIdentifier, getDoi());
        doiPoster.execute();
        return response;
    }

    @Override
    public boolean readyToExecute() {
        return datasetIdentifier != null && dCMetadataValid;
    }

    @Override
    public String getIdentifierSnippet() {
        String replacement = "<gmd:identifier xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:gco=\"http://www.isotc211.org/2005/gco\"  xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:gmx=\"http://www.isotc211.org/2005/gmx\">\n"
                + "  <gmd:MD_Identifier>\n"
                + "    <gmd:authority>\n"
                + "      <gmd:CI_Citation>\n"
                + "        <gmd:title>\n"
                + "          <gco:CharacterString>DataCite Digital Object Identifier</gco:CharacterString>\n"
                + "        </gmd:title>\n"
                + "        <gmd:date>\n"
                + "          <gmd:CI_Date>\n"
                + "            <gmd:date>\n"
                + "              <gco:Date>2016-05-19</gco:Date>\n"
                + "            </gmd:date>\n"
                + "            <gmd:dateType>\n"
                + "              <gmd:CI_DateTypeCode codeList=\"http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/ML_gmxCodelists.xml#CI_DateTypeCode\" codeListValue=\"creation\" />\n"
                + "            </gmd:dateType>\n"
                + "          </gmd:CI_Date>\n"
                + "        </gmd:date>\n"
                + "      </gmd:CI_Citation>\n"
                + "    </gmd:authority>\n"
                + "    <gmd:code>\n"
                + "      <gmx:Anchor xlink:href=\"https://doi.org/" + getDoi() + "\">" + getDoi() + "</gmx:Anchor>\n"
                + "    </gmd:code>\n"
                + "  </gmd:MD_Identifier>\n"
                + "</gmd:identifier>";

        return replacement;
    }

}
