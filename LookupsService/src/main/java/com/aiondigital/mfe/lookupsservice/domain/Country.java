package com.aiondigital.mfe.lookupsservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Country.
 */
@Document(collection = "country")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "country")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("name_ar")
    private String nameAr;

    @NotNull
    @Field("name_en")
    private String nameEn;

    @NotNull
    @Size(min = 2, max = 2)
    @Field("code")
    private String code;

    @NotNull
    @Size(min = 3, max = 3)
    @Field("currency_code")
    private String currencyCode;

    @DBRef
    @Field("city")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "country" }, allowSetters = true)
    private Set<City> cities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Country id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameAr() {
        return this.nameAr;
    }

    public Country nameAr(String nameAr) {
        this.setNameAr(nameAr);
        return this;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getNameEn() {
        return this.nameEn;
    }

    public Country nameEn(String nameEn) {
        this.setNameEn(nameEn);
        return this;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getCode() {
        return this.code;
    }

    public Country code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public Country currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Set<City> getCities() {
        return this.cities;
    }

    public void setCities(Set<City> cities) {
        if (this.cities != null) {
            this.cities.forEach(i -> i.setCountry(null));
        }
        if (cities != null) {
            cities.forEach(i -> i.setCountry(this));
        }
        this.cities = cities;
    }

    public Country cities(Set<City> cities) {
        this.setCities(cities);
        return this;
    }

    public Country addCity(City city) {
        this.cities.add(city);
        city.setCountry(this);
        return this;
    }

    public Country removeCity(City city) {
        this.cities.remove(city);
        city.setCountry(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        return id != null && id.equals(((Country) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Country{" +
            "id=" + getId() +
            ", nameAr='" + getNameAr() + "'" +
            ", nameEn='" + getNameEn() + "'" +
            ", code='" + getCode() + "'" +
            ", currencyCode='" + getCurrencyCode() + "'" +
            "}";
    }
}
