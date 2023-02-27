package com.aiondigital.mfe.lookupsservice.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.aiondigital.mfe.lookupsservice.domain.Card;
import com.aiondigital.mfe.lookupsservice.repository.CardRepository;
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
 * Spring Data Elasticsearch repository for the {@link Card} entity.
 */
public interface CardSearchRepository extends ElasticsearchRepository<Card, String>, CardSearchRepositoryInternal {}

interface CardSearchRepositoryInternal {
    Page<Card> search(String query, Pageable pageable);

    Page<Card> search(Query query);

    void index(Card entity);
}

class CardSearchRepositoryInternalImpl implements CardSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CardRepository repository;

    CardSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CardRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Card> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Card> search(Query query) {
        SearchHits<Card> searchHits = elasticsearchTemplate.search(query, Card.class);
        List<Card> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Card entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
