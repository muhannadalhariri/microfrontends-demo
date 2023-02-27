package com.aiondigital.mfe.transfers.domain;

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
 * A Transaction.
 */
@Document(collection = "transaction")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transaction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("reference_id")
    private String referenceId;

    @NotNull
    @Field("user_id")
    private String userId;

    @DBRef
    @Field("transactionDetails")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "transaction" }, allowSetters = true)
    private Set<TransactionDetails> transactionDetails = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Transaction id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferenceId() {
        return this.referenceId;
    }

    public Transaction referenceId(String referenceId) {
        this.setReferenceId(referenceId);
        return this;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getUserId() {
        return this.userId;
    }

    public Transaction userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<TransactionDetails> getTransactionDetails() {
        return this.transactionDetails;
    }

    public void setTransactionDetails(Set<TransactionDetails> transactionDetails) {
        if (this.transactionDetails != null) {
            this.transactionDetails.forEach(i -> i.setTransaction(null));
        }
        if (transactionDetails != null) {
            transactionDetails.forEach(i -> i.setTransaction(this));
        }
        this.transactionDetails = transactionDetails;
    }

    public Transaction transactionDetails(Set<TransactionDetails> transactionDetails) {
        this.setTransactionDetails(transactionDetails);
        return this;
    }

    public Transaction addTransactionDetails(TransactionDetails transactionDetails) {
        this.transactionDetails.add(transactionDetails);
        transactionDetails.setTransaction(this);
        return this;
    }

    public Transaction removeTransactionDetails(TransactionDetails transactionDetails) {
        this.transactionDetails.remove(transactionDetails);
        transactionDetails.setTransaction(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return id != null && id.equals(((Transaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", referenceId='" + getReferenceId() + "'" +
            ", userId='" + getUserId() + "'" +
            "}";
    }
}
