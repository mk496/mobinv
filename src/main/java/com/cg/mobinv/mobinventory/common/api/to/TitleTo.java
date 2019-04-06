package com.cg.mobinv.mobinventory.common.api.to;

import org.apache.olingo.odata2.api.annotation.edm.*;

/**
 * @author manjinsi
 */
@EdmEntityType(name = "Title")
@EdmEntitySet(name = "Titles")
public class TitleTo {

    @EdmKey
    @EdmProperty
    private Long id;

    @EdmProperty
    private Integer keyFigure;

    @EdmProperty
    private String longIdentifier;

    @EdmProperty
    private String reasonDisputable;

    @EdmProperty
    private String shortIdentifier;

//    @EdmProperty
//    private String explanation;
//
//    @EdmProperty
//    private Integer authorizationType;
//
//    @EdmProperty
//    private Integer titleClassification;
//
//    @EdmProperty
//    private Integer titleType;
//
//    @EdmProperty
//    private Integer budgetControl;
//
//    @EdmProperty
//    private Integer titleState;
//
//    @EdmProperty
//    private Boolean isActive;
//
//    @EdmProperty
//    private Integer roundState;


    /**
     * @return id
     */
    public Long getId() {

        return this.id;
    }

    /**
     * @param id new value of {@link #getid}.
     */
    public void setId(Long id) {

        this.id = id;
    }

    /**
     * @return keyFigure
     */
    public Integer getKeyFigure() {

        return this.keyFigure;
    }

    /**
     * @param keyFigure new value of {@link #getkeyFigure}.
     */
    public void setKeyFigure(Integer keyFigure) {

        this.keyFigure = keyFigure;
    }

    /**
     * @return longIdentifier
     */
    public String getLongIdentifier() {

        return this.longIdentifier;
    }

    /**
     * @param longIdentifier new value of {@link #getlongIdentifier}.
     */
    public void setLongIdentifier(String longIdentifier) {

        this.longIdentifier = longIdentifier;
    }

    /**
     * @return reasonDisputable
     */
    public String getReasonDisputable() {

        return this.reasonDisputable;
    }

    /**
     * @param reasonDisputable new value of {@link #getreasonDisputable}.
     */
    public void setReasonDisputable(String reasonDisputable) {

        this.reasonDisputable = reasonDisputable;
    }

    /**
     * @return shortIdentifier
     */
    public String getShortIdentifier() {

        return this.shortIdentifier;
    }

    /**
     * @param shortIdentifier new value of {@link #getshortIdentifier}.
     */
    public void setShortIdentifier(String shortIdentifier) {

        this.shortIdentifier = shortIdentifier;
    }
}
