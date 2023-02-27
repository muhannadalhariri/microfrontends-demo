package com.aiondigital.mfe.finances.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A FinanceRequest.
 */
@Document(collection = "finance_request")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "financerequest")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FinanceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("user_id")
    private String userId;

    @NotNull
    @Field("total_amount")
    private Double totalAmount;

    @NotNull
    @Field("installment_amount")
    private Double installmentAmount;

    @NotNull
    @Field("installment_period")
    private Integer installmentPeriod;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public FinanceRequest id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public FinanceRequest userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getTotalAmount() {
        return this.totalAmount;
    }

    public FinanceRequest totalAmount(Double totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getInstallmentAmount() {
        return this.installmentAmount;
    }

    public FinanceRequest installmentAmount(Double installmentAmount) {
        this.setInstallmentAmount(installmentAmount);
        return this;
    }

    public void setInstallmentAmount(Double installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public Integer getInstallmentPeriod() {
        return this.installmentPeriod;
    }

    public FinanceRequest installmentPeriod(Integer installmentPeriod) {
        this.setInstallmentPeriod(installmentPeriod);
        return this;
    }

    public void setInstallmentPeriod(Integer installmentPeriod) {
        this.installmentPeriod = installmentPeriod;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FinanceRequest)) {
            return false;
        }
        return id != null && id.equals(((FinanceRequest) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FinanceRequest{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", installmentAmount=" + getInstallmentAmount() +
            ", installmentPeriod=" + getInstallmentPeriod() +
            "}";
    }
}
