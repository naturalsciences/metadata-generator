/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author thomas
 */
public interface IKeyword extends Serializable {
    
    static final long serialVersionUID = 1L;
    
    public enum KeywordType {
        DISCIPLINE,
        PLACE,
        STRATUM,
        TEMPORAL,
        THEME,
        DATACENTRE
    }

    public String getUrl();

    public void setUrl(String url);

    public String getPrefLabel();

    public void setPrefLabel(String url);

    public String getType();

    public String getThesaurusTitle();

    public Date getThesaurusPublicationDate();

    public String getThesaurusVersion();

    public String getThesaurusUrl();

    public void setType(String type);

    public void setThesaurusTitle(String thesaurusTitle);

    public void setThesaurusPublicationDate(Date thesaurusPublicationDate);

    public void setThesaurusVersion(String thesaurusVersion);

    public void setThesaurusUrl(String thesaurusUrl);
}
