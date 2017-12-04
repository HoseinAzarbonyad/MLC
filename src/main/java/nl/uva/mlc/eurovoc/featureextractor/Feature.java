package nl.uva.mlc.eurovoc.featureextractor;

import java.util.Objects;

/**
 *
 * @author mosi
 */
public class Feature {
    private String fName;
    private Double fValue;
    private String qId;
    private String dId;
    private Integer dRankq;
    private String label;

    public Feature(String fName, Double fValue, String qId, String dId, Integer dRankq) {
        this.fName = fName;
        this.fValue = fValue;
        this.qId = qId;
        this.dId = dId;
        this.dRankq = dRankq;
    }
    
    public Feature(String fName, Double fValue, String qId, String dId, String label) {
        this.fName = fName;
        this.fValue = fValue;
        this.qId = qId;
        this.dId = dId;
        this.label = label;
    }
    
     public Feature(String fName, Double fValue, String dId) {
        this.fName = fName;
        this.fValue = fValue;
        this.dId = dId;
    }

    public String getLabel() {
        return label;
    }

    public String getfName() {
        return fName;
    }

    public Double getfValue() {
        return fValue;
    }

    public String getqId() {
        return qId;
    }

    public String getdId() {
        return dId;
    }
    
    public Integer getdRankq() {
        return dRankq;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setfValue(Double fValue) {
        this.fValue = fValue;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }
    public void setdRankq(Integer dRankq) {
        this.dRankq = dRankq;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.qId);
        hash = 29 * hash + Objects.hashCode(this.dId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Feature other = (Feature) obj;
        if (!Objects.equals(this.qId, other.qId)) {
            return false;
        }
        if (!Objects.equals(this.dId, other.dId)) {
            return false;
        }
        return true;
    }
    
}

