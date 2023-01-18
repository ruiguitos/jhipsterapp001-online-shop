package com.gv.jhipsterapp001.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.gv.jhipsterapp001.domain.WishList;
import com.gv.jhipsterapp001.repository.rowmapper.CustomerRowMapper;
import com.gv.jhipsterapp001.repository.rowmapper.WishListRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the WishList entity.
 */
@SuppressWarnings("unused")
class WishListRepositoryInternalImpl extends SimpleR2dbcRepository<WishList, Long> implements WishListRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerRowMapper customerMapper;
    private final WishListRowMapper wishlistMapper;

    private static final Table entityTable = Table.aliased("wish_list", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("customer", "customer");

    public WishListRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerRowMapper customerMapper,
        WishListRowMapper wishlistMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(WishList.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
        this.wishlistMapper = wishlistMapper;
    }

    @Override
    public Flux<WishList> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<WishList> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = WishListSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, WishList.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<WishList> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<WishList> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private WishList process(Row row, RowMetadata metadata) {
        WishList entity = wishlistMapper.apply(row, "e");
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends WishList> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
