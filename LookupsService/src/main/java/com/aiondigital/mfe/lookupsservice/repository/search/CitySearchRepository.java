package com.aiondigital.mfe.lookupsservice.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.aiondigital.mfe.lookupsservice.domain.City;
import com.aiondigital.mfe.lookupsservice.repository.CityRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link City} entity.
 */
public interface CitySearchRepository extends ElasticsearchRepository<City, String>, CitySearchRepositoryInternal {}

interface CitySearchRepositoryInternal {
    Page<City> search(String query, Pageable pageable);

    Page<City> search(Query query);

    void index(City entity);
}

class CitySearchRepositoryInternalImpl implements CitySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CityRepository repository;

    CitySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CityRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<City> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<City> search(Query query) {
        SearchHits<City> searchHits = elasticsearchTemplate.search(query, City.class);
        List<City> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(City entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
