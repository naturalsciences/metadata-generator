// 

// 

package be.naturalsciences.bmdc.metadata.datacite.mds.doi;

import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPClient;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPRequest;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPResponse;
import be.naturalsciences.bmdc.metadata.datacite.mds.account.Account;

public class PostDOI
{
    private Account account;
    private String url;
    private String doi;
    
    public Account getAccount() {
        return this.account;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public String getDoi() {
        return this.doi;
    }
    
    public PostDOI(final Account account, final String url, final String doi) {
        this.account = account;
        this.url = url;
        this.doi = doi;
    }
    
    public HTTPResponse execute() throws Exception {
        final String contentType = "text/plain;charset=UTF-8";
        String requestBody = "";
        requestBody = requestBody + "doi=" + this.doi;
        requestBody += "\n";
        requestBody = requestBody + "url=" + this.url;
        final HTTPRequest request = new HTTPRequest();
        request.setMethod(HTTPRequest.Method.POST);
        request.setURL("https://mds.datacite.org/doi/");
        request.setUsername(this.account.getUserName());
        request.setPassword(this.account.getPassword());
        request.setContentType(contentType);
        request.setBody(requestBody);
        return HTTPClient.doHTTPRequest(request);
    }
}
