// 

// 

package be.naturalsciences.bmdc.metadata.datacite.mds.metadata;

import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPClient;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPRequest;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPResponse;
import be.naturalsciences.bmdc.metadata.datacite.mds.account.Account;

public class GetMetadata
{
    private Account account;
    private String doi;
    
    public GetMetadata(final Account account, final String doi) {
        this.account = account;
        this.doi = doi;
    }
    
    public HTTPResponse execute() throws Exception {
        final HTTPRequest request = new HTTPRequest();
        request.setMethod(HTTPRequest.Method.GET);
        request.setURL("https://mds.datacite.org/metadata/" + this.doi);
        request.setUsername(this.account.getUserName());
        request.setPassword(this.account.getPassword());
        request.setAccept("application/xml");
        return HTTPClient.doHTTPRequest(request);
    }
}
