// 
// Decompiled by Procyon v0.5.36
// 

package be.naturalsciences.bmdc.metadata.datacite.mds.metadata;

import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPResponse;
import be.naturalsciences.bmdc.metadata.datacite.mds.account.Account;

public interface IPostMetadata
{
    Account getAccount();
    
    String getDoi();
    
    String getDCMetadata();
    
    boolean doiAlreadyExists() throws Exception;
    
    boolean readyToExecute();
    
    HTTPResponse execute() throws Exception;
}
