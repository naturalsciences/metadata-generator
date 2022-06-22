/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.impl;

import be.naturalsciences.bmdc.metadata.model.IKeyword;
import be.naturalsciences.bmdc.metadata.model.Thesaurus;

public class Keyword implements IKeyword {

    private Long seqno;
    private String url;
    private String prefLabel;
    private String type;
    private Thesaurus thesaurus;

    public Keyword(Long seqno, String url, String prefLabel, String type, Thesaurus thesaurus/*Title, Date thesaurusPublicationDate, String thesaurusVersion, String thesaurusUrl*/) {
        this.seqno = seqno;
        this.url = url;
        this.prefLabel = prefLabel;
        this.type = type;
        /*     this.thesaurusTitle = thesaurusTitle;
        this.thesaurusPublicationDate = thesaurusPublicationDate;
        this.thesaurusVersion = thesaurusVersion;
        this.thesaurusUrl = thesaurusUrl;*/
    }

    public Long getSeqno() {
        return seqno;
    }

    public void setSeqno(Long seqno) {
        this.seqno = seqno;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Thesaurus getThesaurus() {
        return thesaurus;
    }

    public void setThesaurus(Thesaurus thesaurus) {
        this.thesaurus = thesaurus;
    }
    /*
    public String getThesaurusTitle() {
        return thesaurusTitle;
    }

    public void setThesaurusTitle(String thesaurusTitle) {
        this.thesaurusTitle = thesaurusTitle;
    }

    public Date getThesaurusPublicationDate() {
        return thesaurusPublicationDate;
    }

    public void setThesaurusPublicationDate(Date thesaurusPublicationDate) {
        this.thesaurusPublicationDate = thesaurusPublicationDate;
    }

    public String getThesaurusVersion() {
        return thesaurusVersion;
    }

    public void setThesaurusVersion(String thesaurusVersion) {
        this.thesaurusVersion = thesaurusVersion;
    }

    public String getThesaurusUrl() {
        return thesaurusUrl;
    }

    public void setThesaurusUrl(String thesaurusUrl) {
        this.thesaurusUrl = thesaurusUrl;
    }*/

}
