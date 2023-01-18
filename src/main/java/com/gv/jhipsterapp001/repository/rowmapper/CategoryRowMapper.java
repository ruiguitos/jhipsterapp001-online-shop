package com.gv.jhipsterapp001.repository.rowmapper;

import com.gv.jhipsterapp001.domain.Category;
import com.gv.jhipsterapp001.domain.enumeration.CategoryStatus;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Category}, with proper type conversions.
 */
@Service
public class CategoryRowMapper implements BiFunction<Row, String, Category> {

    private final ColumnConverter converter;

    public CategoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Category} stored in the database.
     */
    @Override
    public Category apply(Row row, String prefix) {
        Category entity = new Category();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setSortOrder(converter.fromRow(row, prefix + "_sort_order", Integer.class));
        entity.setDateAdded(converter.fromRow(row, prefix + "_date_added", LocalDate.class));
        entity.setDateModified(converter.fromRow(row, prefix + "_date_modified", LocalDate.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", CategoryStatus.class));
        entity.setParentId(converter.fromRow(row, prefix + "_parent_id", Long.class));
        return entity;
    }
}
