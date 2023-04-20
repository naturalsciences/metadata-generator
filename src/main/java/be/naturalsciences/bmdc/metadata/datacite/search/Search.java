package be.naturalsciences.bmdc.metadata.datacite.search;

import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPClient;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPRequest;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPResponse;

/**
 * Demo of DataCite Search API 
 * @see <a href="http://search.datacite.org">http://search.datacite.org</a>
 * @author mpaluch
 */
public class Search {

	public static final String SERVICE_ADDRESS = "http://search.datacite.org/api";	
	
	public void execute(){
		try{						
			StringBuilder queryParameters = new StringBuilder("q=ice+ocean");
			queryParameters.append("&wt=json");
			queryParameters.append("&fl=datacentre,doi,title");
			queryParameters.append("&rows=5&indent=true");
			
			HTTPRequest request = new HTTPRequest();
			request.setMethod(HTTPRequest.Method.GET);
			request.setURL(SERVICE_ADDRESS + "?" + queryParameters.toString());
			
			print(request.toString());
			
			HTTPResponse response = HTTPClient.doHTTPRequest(request);
			print(response.toString());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Search exec = new Search();
		exec.execute();
		exec = null;
	}

	private void print(String str){
		System.out.println(str);
	}
}
