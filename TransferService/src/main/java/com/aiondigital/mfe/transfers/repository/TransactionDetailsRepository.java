package com.aiondigital.mfe.transfers.repository;

import com.aiondigital.mfe.transfers.domain.TransactionDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the TransactionDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionDetailsRepository extends MongoRepository<TransactionDetails, String> {}
