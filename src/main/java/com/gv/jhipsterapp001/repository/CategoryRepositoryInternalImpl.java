package com.gv.jhipsterapp001.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.gv.jhipsterapp001.domain.Category;
import com.gv.jhipsterapp001.domain.Product;
import com.gv.jhipsterapp001.domain.enumeration.CategoryStatus;
import com.gv.jhipsterapp001.repository.rowmapper.CategoryRowMapper;
import com.gv.jhipsterapp001.repository.rowmapper.CategoryRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
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
 * Spring Data R2DBC custom repository implementation for the Category entity.
 */
@SuppressWarnings("unused")
class CategoryRepositoryInternalImpl extends SimpleR2dbcRepository<Category, Long> implements CategoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategoryRowMapper categoryMapper;

    private static final Table entityTable = Table.aliased("category", EntityManager.ENTITY_ALIAS);
    private static final Table parentTable = Table.aliased("category", "parent");

    private static final EntityManager.LinkTable productLink = new EntityManager.LinkTable(
        "rel_category__product",
        "category_id",
        "product_id"
    );

    public CategoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategoryRowMapper categoryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Category.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Flux<Category> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Category> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CategorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategorySqlHelper.getColumns(parentTable, "parent"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(parentTable)
            .on(Column.create("parent_id", entityTable))
            .equals(Column.create("id", parentTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Category.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Category> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Category> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Category> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Category> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Category> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Category process(Row row, RowMetadata metadata) {
        Category entity = categoryMapper.apply(row, "e");
        entity.setParent(categoryMapper.apply(row, "parent"));
        return entity;
    }

    @Override
    public <S extends Category> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Category> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(productLink, entity.getId(), entity.getProducts().stream().map(Product::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(productLink, entityId);
    }
}
