package com.aiondigital.mfe.lookupsservice.repository;

import com.aiondigital.mfe.lookupsservice.domain.Card;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Card entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CardRepository extends MongoRepository<Card, String> {}
