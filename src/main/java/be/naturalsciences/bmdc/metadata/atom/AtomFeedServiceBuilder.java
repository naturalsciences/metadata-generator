/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.atom;

import be.naturalsciences.bmdc.metadata.model.IDataset;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.feed.synd.SyndLinkImpl;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author thomas
 * The creation of ATOM service feeds with this class has not been used in production.
 */
 @Deprecated
public class AtomFeedServiceBuilder {

    private final String serviceAtomUrl;
    private final Collection<IDataset> datasets;
    private final String topic;
    private final String serviceMetadataDocumentUrl;
    private final String topicExtended;

    // atom_servicefeed_msfd.xml, null,MSFD,
    public AtomFeedServiceBuilder(String serviceAtomUrl, Collection<IDataset> datasets, String topic, String topicExtended, String serviceMetadataDocumentUrl, String datasetMetadataPrefix) {
        this.serviceAtomUrl = serviceAtomUrl;
        this.datasets = datasets;
        this.topic = topic;
        this.serviceMetadataDocumentUrl = serviceMetadataDocumentUrl;
        this.topicExtended = topicExtended;
    }

    //http://geonetwork.bmdc.be/geonetwork/srv/eng/csw?request=GetRecords&service=CSW&version=2.0.2&resultType=results&namespace=csw:http://www.opengis.net/cat/csw&outputSchema=csw:IsoRecord&constraint=%3CFilter%20xmlns=%22http://www.opengis.net/ogc%22%20xmlns:gml=%22http://www.opengis.net/gml%22%3E%3CPropertyIsEqualTo%3E%3CPropertyName%3Ekeyword%3C/PropertyName%3E%3CLiteral%3EReporting%20INSPIRE%3C/Literal%3E%3C/PropertyIsEqualTo%3E%3C/Filter%3E&constraintLanguage=FILTER&constraint_language_version=1.1.0&typeNames=gmd:MD_Metadata&startPosition=1&maxRecords=200
    public String createXMLString(IInstituteRole author) throws FeedException {
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("RBINS INSPIRE Download Service for " + topic);
        feed.setDescription("INSPIRE Download Service of the " + author.getOrganisationName() + " to retrieve datasets of " + topicExtended);
        SyndLink selfMetadata = new SyndLinkImpl();
        selfMetadata.setRel("describedby");
        selfMetadata.setType("application/xml");
        selfMetadata.setHref(serviceMetadataDocumentUrl);

        SyndLink self = new SyndLinkImpl();
        self.setRel("self");
        self.setType("application/atom+xml");
        self.setTitle("This document");
        self.setHref(serviceAtomUrl);
        feed.setUri(serviceAtomUrl);

        feed.setLinks(Arrays.asList(selfMetadata, self));
        feed.setFeedType("atom_1.0");
        SyndPerson auth = new SyndPersonImpl();
        auth.setEmail(author.getEmailAddress());
        auth.setName(author.getOrganisationName());
        feed.setAuthors(Arrays.asList(auth));

        for (IDataset dataset : datasets) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setUpdatedDate(dataset.getRevisionDate());
            entry.setTitle(dataset.getTitle());
            SyndLink isoMetadataUrl = new SyndLinkImpl();
            isoMetadataUrl.setRel("describedby");
            isoMetadataUrl.setType("application/xml");
            isoMetadataUrl.setTitle("Full metadata for this dataset");
            isoMetadataUrl.setHref(dataset.getMetadataUrlXML());

            SyndLink downloadUrl = new SyndLinkImpl();
            downloadUrl.setRel("alternate");
            downloadUrl.setType("application/atom+xml");
            downloadUrl.setTitle("Full metadata for this dataset");
            downloadUrl.setHref(dataset.getMetadataUrlXML());

        }
        return new SyndFeedOutput().outputString(feed);
    }

    public String buildAtomFeedFileName() {
        return "service-atom-inspire.xml";
    }

    public void createFile(File file, IInstituteRole author) throws FileNotFoundException, FeedException {

        String xml = createXMLString(author);
        if (xml != null) {
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(xml);
            }
        }
    }
}
