package com.gv.jhipsterapp001.repository;

import com.gv.jhipsterapp001.domain.WishList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the WishList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WishListRepository extends ReactiveCrudRepository<WishList, Long>, WishListRepositoryInternal {
    @Query("SELECT * FROM wish_list entity WHERE entity.customer_id = :id")
    Flux<WishList> findByCustomer(Long id);

    @Query("SELECT * FROM wish_list entity WHERE entity.customer_id IS NULL")
    Flux<WishList> findAllWhereCustomerIsNull();

    @Override
    <S extends WishList> Mono<S> save(S entity);

    @Override
    Flux<WishList> findAll();

    @Override
    Mono<WishList> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface WishListRepositoryInternal {
    <S extends WishList> Mono<S> save(S entity);

    Flux<WishList> findAllBy(Pageable pageable);

    Flux<WishList> findAll();

    Mono<WishList> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<WishList> findAllBy(Pageable pageable, Criteria criteria);

}
