package com.gv.jhipsterapp001.repository;

import com.gv.jhipsterapp001.domain.Palha;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Palha entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PalhaRepository extends ReactiveCrudRepository<Palha, Long>, PalhaRepositoryInternal {
    Flux<Palha> findAllBy(Pageable pageable);

    @Override
    <S extends Palha> Mono<S> save(S entity);

    @Override
    Flux<Palha> findAll();

    @Override
    Mono<Palha> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PalhaRepositoryInternal {
    <S extends Palha> Mono<S> save(S entity);

    Flux<Palha> findAllBy(Pageable pageable);

    Flux<Palha> findAll();

    Mono<Palha> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Palha> findAllBy(Pageable pageable, Criteria criteria);

}
