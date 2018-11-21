/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.atom;

import be.naturalsciences.bmdc.metadata.model.IDataset;
import be.naturalsciences.bmdc.metadata.model.IDistributionResource;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
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
import static java.util.Collections.singleton;

/**
 *
 * @author thomas
 * 
 * "The creation of ATOM dataset feeds with this class has not been used in production.
 */
 @Deprecated
public class AtomFeedDatasetBuilder {

    private final IDataset dataset;
    private final String serviceAtomUrl;
    private final String datasetMetadataUrl;
    private final String datasetAtomUrl;

    public AtomFeedDatasetBuilder(IDataset dataset, String serviceAtomUrl, String datasetMetadataUrl, String datasetAtomUrl) {
        this.dataset = dataset;
        this.serviceAtomUrl = serviceAtomUrl;
        this.datasetMetadataUrl = datasetMetadataUrl;
        this.datasetAtomUrl = datasetAtomUrl;
    }

    public String createXMLString(IInstituteRole author) throws FeedException {
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("Download Service for " + dataset.getTitle());
        feed.setDescription("INSPIRE Download Service to retrieve the files attached to the dataset '" + dataset.getTitle() + "'");
        SyndLink selfMetadata = new SyndLinkImpl();
        selfMetadata.setHref(datasetMetadataUrl);
        selfMetadata.setRel("describedby");
        selfMetadata.setType("application/xml");

        SyndLink self = new SyndLinkImpl();
        self.setHref(datasetAtomUrl);
        self.setRel("self");
        self.setType("application/atom+xml");
        self.setTitle("This document");

        SyndLink up = new SyndLinkImpl();
        up.setHref(serviceAtomUrl);
        up.setRel("up");
        up.setType("application/atom+xml");
        up.setTitle("The service feed document");

        feed.setLinks(Arrays.asList(selfMetadata, self, up));
        feed.setFeedType("atom_1.0");
        SyndPerson auth = new SyndPersonImpl();
        auth.setEmail(author.getEmailAddress());
        auth.setName(author.getOrganisationName());
        feed.setAuthors(Arrays.asList(auth));
        for (IDistributionResource ds : dataset.getDistributionResources()) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setUpdatedDate(dataset.getRevisionDate());
            entry.setTitle(ds.getOnlineResourceUrl().toString());

            SyndLink linkAlt = new SyndLinkImpl();
            linkAlt.setHref(ds.getOnlineResourceUrl().toString());
            linkAlt.setType(ds.getDistributionFormats().get(0).getDistributionFormatMime().toString());
            linkAlt.setRel("alternate");

            SyndLink linkEnc = new SyndLinkImpl();
            linkEnc.setHref(ds.getOnlineResourceUrl().toString());
            linkEnc.setType(ds.getDistributionFormats().get(0).getDistributionFormatMime().toString());
            linkEnc.setRel("enclosure");

            entry.getLinks().add(linkAlt);
            entry.getLinks().add(linkEnc);
            feed.getEntries().add(entry);
        }
        return new SyndFeedOutput().outputString(feed);
    }

    public String buildAtomFeedFileName() {
        return this.dataset.getIdentifier().replaceAll("::", "_").replaceAll(":", "_") + "-ATOM.xml";
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
