package com.gv.jhipsterapp001.repository.rowmapper;

import com.gv.jhipsterapp001.domain.WishList;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link WishList}, with proper type conversions.
 */
@Service
public class WishListRowMapper implements BiFunction<Row, String, WishList> {

    private final ColumnConverter converter;

    public WishListRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link WishList} stored in the database.
     */
    @Override
    public WishList apply(Row row, String prefix) {
        WishList entity = new WishList();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setRestricted(converter.fromRow(row, prefix + "_restricted", Boolean.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
