package com.aiondigital.mfe.lookupsservice.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.aiondigital.mfe.lookupsservice.domain.Country;
import com.aiondigital.mfe.lookupsservice.repository.CountryRepository;
import com.aiondigital.mfe.lookupsservice.repository.search.CountrySearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Country}.
 */
@Service
public class CountryService {

    private final Logger log = LoggerFactory.getLogger(CountryService.class);

    private final CountryRepository countryRepository;

    private final CountrySearchRepository countrySearchRepository;

    public CountryService(CountryRepository countryRepository, CountrySearchRepository countrySearchRepository) {
        this.countryRepository = countryRepository;
        this.countrySearchRepository = countrySearchRepository;
    }

    /**
     * Save a country.
     *
     * @param country the entity to save.
     * @return the persisted entity.
     */
    public Country save(Country country) {
        log.debug("Request to save Country : {}", country);
        Country result = countryRepository.save(country);
        countrySearchRepository.index(result);
        return result;
    }

    /**
     * Update a country.
     *
     * @param country the entity to save.
     * @return the persisted entity.
     */
    public Country update(Country country) {
        log.debug("Request to update Country : {}", country);
        Country result = countryRepository.save(country);
        countrySearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a country.
     *
     * @param country the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Country> partialUpdate(Country country) {
        log.debug("Request to partially update Country : {}", country);

        return countryRepository
            .findById(country.getId())
            .map(existingCountry -> {
                if (country.getNameAr() != null) {
                    existingCountry.setNameAr(country.getNameAr());
                }
                if (country.getNameEn() != null) {
                    existingCountry.setNameEn(country.getNameEn());
                }
                if (country.getCode() != null) {
                    existingCountry.setCode(country.getCode());
                }
                if (country.getCurrencyCode() != null) {
                    existingCountry.setCurrencyCode(country.getCurrencyCode());
                }

                return existingCountry;
            })
            .map(countryRepository::save)
            .map(savedCountry -> {
                countrySearchRepository.save(savedCountry);

                return savedCountry;
            });
    }

    /**
     * Get all the countries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Country> findAll(Pageable pageable) {
        log.debug("Request to get all Countries");
        return countryRepository.findAll(pageable);
    }

    /**
     * Get one country by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Country> findOne(String id) {
        log.debug("Request to get Country : {}", id);
        return countryRepository.findById(id);
    }

    /**
     * Delete the country by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Country : {}", id);
        countryRepository.deleteById(id);
        countrySearchRepository.deleteById(id);
    }

    /**
     * Search for the country corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Country> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Countries for query {}", query);
        return countrySearchRepository.search(query, pageable);
    }
}
