package com.aiondigital.mfe.finances.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.aiondigital.mfe.finances.domain.FinanceRequest;
import com.aiondigital.mfe.finances.repository.FinanceRequestRepository;
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
 * Spring Data Elasticsearch repository for the {@link FinanceRequest} entity.
 */
public interface FinanceRequestSearchRepository
    extends ElasticsearchRepository<FinanceRequest, String>, FinanceRequestSearchRepositoryInternal {}

interface FinanceRequestSearchRepositoryInternal {
    Page<FinanceRequest> search(String query, Pageable pageable);

    Page<FinanceRequest> search(Query query);

    void index(FinanceRequest entity);
}

class FinanceRequestSearchRepositoryInternalImpl implements FinanceRequestSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final FinanceRequestRepository repository;

    FinanceRequestSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, FinanceRequestRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<FinanceRequest> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<FinanceRequest> search(Query query) {
        SearchHits<FinanceRequest> searchHits = elasticsearchTemplate.search(query, FinanceRequest.class);
        List<FinanceRequest> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(FinanceRequest entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
