package com.aiondigital.mfe.transfers.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.aiondigital.mfe.transfers.domain.Transaction;
import com.aiondigital.mfe.transfers.repository.TransactionRepository;
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
 * Spring Data Elasticsearch repository for the {@link Transaction} entity.
 */
public interface TransactionSearchRepository extends ElasticsearchRepository<Transaction, String>, TransactionSearchRepositoryInternal {}

interface TransactionSearchRepositoryInternal {
    Page<Transaction> search(String query, Pageable pageable);

    Page<Transaction> search(Query query);

    void index(Transaction entity);
}

class TransactionSearchRepositoryInternalImpl implements TransactionSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TransactionRepository repository;

    TransactionSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TransactionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Transaction> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Transaction> search(Query query) {
        SearchHits<Transaction> searchHits = elasticsearchTemplate.search(query, Transaction.class);
        List<Transaction> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Transaction entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
