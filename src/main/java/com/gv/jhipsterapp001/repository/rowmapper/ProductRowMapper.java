package com.gv.jhipsterapp001.repository.rowmapper;

import com.gv.jhipsterapp001.domain.Product;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Product}, with proper type conversions.
 */
@Service
public class ProductRowMapper implements BiFunction<Row, String, Product> {

    private final ColumnConverter converter;

    public ProductRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Product} stored in the database.
     */
    @Override
    public Product apply(Row row, String prefix) {
        Product entity = new Product();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setKeywords(converter.fromRow(row, prefix + "_keywords", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Integer.class));
        entity.setDateAdded(converter.fromRow(row, prefix + "_date_added", LocalDate.class));
        entity.setDateModified(converter.fromRow(row, prefix + "_date_modified", LocalDate.class));
        entity.setWishListId(converter.fromRow(row, prefix + "_wish_list_id", Long.class));
        return entity;
    }
}
