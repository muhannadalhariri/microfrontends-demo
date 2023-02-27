package com.aiondigital.mfe.finances.repository;

import com.aiondigital.mfe.finances.domain.FinanceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the FinanceRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FinanceRequestRepository extends MongoRepository<FinanceRequest, String> {}
