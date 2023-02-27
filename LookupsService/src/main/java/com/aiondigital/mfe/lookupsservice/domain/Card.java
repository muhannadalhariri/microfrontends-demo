package com.aiondigital.mfe.lookupsservice.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Card.
 */
@Document(collection = "card")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "card")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name_ar")
    private String nameAr;

    @Field("name_en")
    private String nameEn;

    @Field("card_type_id")
    private Integer cardTypeId;

    @Field("card_reference")
    private String cardReference;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Card id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameAr() {
        return this.nameAr;
    }

    public Card nameAr(String nameAr) {
        this.setNameAr(nameAr);
        return this;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getNameEn() {
        return this.nameEn;
    }

    public Card nameEn(String nameEn) {
        this.setNameEn(nameEn);
        return this;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public Integer getCardTypeId() {
        return this.cardTypeId;
    }

    public Card cardTypeId(Integer cardTypeId) {
        this.setCardTypeId(cardTypeId);
        return this;
    }

    public void setCardTypeId(Integer cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public String getCardReference() {
        return this.cardReference;
    }

    public Card cardReference(String cardReference) {
        this.setCardReference(cardReference);
        return this;
    }

    public void setCardReference(String cardReference) {
        this.cardReference = cardReference;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        return id != null && id.equals(((Card) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Card{" +
            "id=" + getId() +
            ", nameAr='" + getNameAr() + "'" +
            ", nameEn='" + getNameEn() + "'" +
            ", cardTypeId=" + getCardTypeId() +
            ", cardReference='" + getCardReference() + "'" +
            "}";
    }
}
