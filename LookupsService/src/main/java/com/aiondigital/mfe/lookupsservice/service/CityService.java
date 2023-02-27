package com.aiondigital.mfe.lookupsservice.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.aiondigital.mfe.lookupsservice.domain.City;
import com.aiondigital.mfe.lookupsservice.repository.CityRepository;
import com.aiondigital.mfe.lookupsservice.repository.search.CitySearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link City}.
 */
@Service
public class CityService {

    private final Logger log = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;

    private final CitySearchRepository citySearchRepository;

    public CityService(CityRepository cityRepository, CitySearchRepository citySearchRepository) {
        this.cityRepository = cityRepository;
        this.citySearchRepository = citySearchRepository;
    }

    /**
     * Save a city.
     *
     * @param city the entity to save.
     * @return the persisted entity.
     */
    public City save(City city) {
        log.debug("Request to save City : {}", city);
        City result = cityRepository.save(city);
        citySearchRepository.index(result);
        return result;
    }

    /**
     * Update a city.
     *
     * @param city the entity to save.
     * @return the persisted entity.
     */
    public City update(City city) {
        log.debug("Request to update City : {}", city);
        City result = cityRepository.save(city);
        citySearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a city.
     *
     * @param city the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<City> partialUpdate(City city) {
        log.debug("Request to partially update City : {}", city);

        return cityRepository
            .findById(city.getId())
            .map(existingCity -> {
                if (city.getNameAr() != null) {
                    existingCity.setNameAr(city.getNameAr());
                }
                if (city.getNameEn() != null) {
                    existingCity.setNameEn(city.getNameEn());
                }

                return existingCity;
            })
            .map(cityRepository::save)
            .map(savedCity -> {
                citySearchRepository.save(savedCity);

                return savedCity;
            });
    }

    /**
     * Get all the cities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<City> findAll(Pageable pageable) {
        log.debug("Request to get all Cities");
        return cityRepository.findAll(pageable);
    }

    /**
     * Get one city by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<City> findOne(String id) {
        log.debug("Request to get City : {}", id);
        return cityRepository.findById(id);
    }

    /**
     * Delete the city by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete City : {}", id);
        cityRepository.deleteById(id);
        citySearchRepository.deleteById(id);
    }

    /**
     * Search for the city corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<City> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Cities for query {}", query);
        return citySearchRepository.search(query, pageable);
    }
}
