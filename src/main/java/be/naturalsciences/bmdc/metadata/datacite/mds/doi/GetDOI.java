// 
// Decompiled by Procyon v0.5.36
// 

package be.naturalsciences.bmdc.metadata.datacite.mds.doi;

import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPClient;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPRequest;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPResponse;
import be.naturalsciences.bmdc.metadata.datacite.mds.account.Account;

public class GetDOI
{
    private Account account;
    private String doi;
    
    public Account getAccount() {
        return this.account;
    }
    
    public void setAccount(final Account account) {
        this.account = account;
    }
    
    public String getDoi() {
        return this.doi;
    }
    
    public void setDoi(final String doi) {
        this.doi = doi;
    }
    
    public GetDOI(final Account account, final String doi) {
        this.account = account;
        this.doi = doi;
    }
    
    public HTTPResponse execute() throws Exception {
        final HTTPRequest request = new HTTPRequest();
        request.setMethod(HTTPRequest.Method.GET);
        request.setURL("https://mds.datacite.org/doi/" + this.doi);
        request.setUsername(this.account.getUserName());
        request.setPassword(this.account.getPassword());
        System.out.println(request.toString());
        return HTTPClient.doHTTPRequest(request);
    }
}
