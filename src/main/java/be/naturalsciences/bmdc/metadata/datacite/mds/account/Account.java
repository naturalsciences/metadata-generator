// 

// 

package be.naturalsciences.bmdc.metadata.datacite.mds.account;

public class Account
{
    public static final String METADATA_SERVICE_ADDRESS = "https://mds.datacite.org/metadata/";
    public static final String DOI_SERVICE_ADDRESS = "https://mds.datacite.org/doi/";
    private String userName;
    private String password;
    private String prefix;
    
    public Account(final String userName, final String password, final String prefix) {
        this.userName = userName;
        this.password = password;
        this.prefix = prefix;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
}
