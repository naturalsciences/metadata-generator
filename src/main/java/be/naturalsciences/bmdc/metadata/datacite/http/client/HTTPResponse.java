package be.naturalsciences.bmdc.metadata.datacite.http.client;

/**
 * Encapsulates a HTTP response
 */
public final class HTTPResponse
{
	/*********************************************************************************************
	 * VARIABLE DECLARATIONS                                                                     *
	 *********************************************************************************************/

	// The HTTP response code
	private int responseCode;

	// The content type of the response body
	private String contentType;

	// The response body
	private byte[] body = new byte[0];	
	

	/*********************************************************************************************
	 * CONSTRUCTORS AND PUBLIC INITIALISATION                                                    *
	 *********************************************************************************************/

	public HTTPResponse(){}

	public HTTPResponse( int responseCode ){
		this.responseCode = responseCode;
	}

	/*********************************************************************************************
	 * PUBLIC METHODS                                                                            *
	 *********************************************************************************************/

	public int getResponseCode(){
		return responseCode;
	}

	public void setResponseCode(int responseCode){
		this.responseCode = responseCode;
	}

	public String getContentType(){
		return contentType;
	}

	public void setContentType(String contentType){
		this.contentType = contentType;
	}

	public byte[] getBody(){
		return body;
	}

	public void setBody(byte[] body){
		this.body = body;
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();

		builder.append("HTTPResponse [\n ");
		builder.append("responseCode = \"").append(responseCode).append("\"");
		builder.append(",\n ");
		builder.append("contentType = \"").append(contentType).append("\"");
		builder.append(",\n ");
		builder.append("body = \"");
		if (contentType!=null && contentType.contains("application/pdf")){
			builder.append("byteStream size: "+(body.length > 0 ? body.length/1024 : "0")+"KB");
		}
		else{ 
			builder.append((body.length>0 ? new String(body) : "null"));
		}
		builder.append("\"");
		builder.append(" ]\n" );

		return builder.toString();
	}

	/*********************************************************************************************
	 * PRIVATE METHODS                                                                           *
	 *********************************************************************************************/

}
