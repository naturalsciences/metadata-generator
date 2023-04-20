// 
// Decompiled by Procyon v0.5.36
// 

package be.naturalsciences.bmdc.metadata.datacite.mds.metadata;

import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPClient;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPRequest;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPResponse;
import java.io.IOException;
import be.naturalsciences.bmdc.metadata.datacite.metadata.Metadata;
import java.io.File;
import be.naturalsciences.bmdc.metadata.datacite.mds.account.Account;

public class PostMetadata implements IPostMetadata
{
    private Account account;
    private String xmlDCMetadata;
    private String doi;
    
    @Override
    public Account getAccount() {
        return this.account;
    }
    
    @Override
    public String getDoi() {
        return this.doi;
    }
    
    @Override
    public String getDCMetadata() {
        return this.xmlDCMetadata;
    }
    
    public PostMetadata(final Account account, final File file) {
        this.account = account;
        try {
            this.xmlDCMetadata = Metadata.getMetadataFromFile(file);
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("The provided file is invalid.", ex);
        }
    }
    
    public PostMetadata(final Account account, final String xmlDCMetadata) {
        this.account = account;
        this.xmlDCMetadata = xmlDCMetadata;
    }
    
    @Override
    public HTTPResponse execute() throws Exception {
        if (this.xmlDCMetadata == null) {
            throw new IllegalStateException("Could not execute posting the DataCite XML when it is not set or not valid.");
        }
        final HTTPRequest request = new HTTPRequest();
        request.setMethod(HTTPRequest.Method.POST);
        request.setURL("https://mds.datacite.org/metadata/");
        request.setUsername(this.account.getUserName());
        request.setPassword(this.account.getPassword());
        request.setContentType("application/xml;charset=UTF-8");
        request.setBody(this.xmlDCMetadata);
        final HTTPResponse response = HTTPClient.doHTTPRequest(request);
        return response;
    }
    
    @Override
    public boolean readyToExecute() {
        return this.xmlDCMetadata != null;
    }
    
    @Override
    public boolean doiAlreadyExists() throws Exception {
        if (this.doi == null) {
            throw new IllegalStateException("Could not verify the existance of this doi on DataCite as it is not set.");
        }
        final GetMetadata get = new GetMetadata(this.account, this.account.getPrefix() + "/" + this.doi);
        return get.execute().getResponseCode() == 200;
    }
}
