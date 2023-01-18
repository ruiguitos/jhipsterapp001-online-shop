package com.gv.jhipsterapp001.repository.rowmapper;

import com.gv.jhipsterapp001.domain.Palha;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Palha}, with proper type conversions.
 */
@Service
public class PalhaRowMapper implements BiFunction<Row, String, Palha> {

    private final ColumnConverter converter;

    public PalhaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Palha} stored in the database.
     */
    @Override
    public Palha apply(Row row, String prefix) {
        Palha entity = new Palha();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setJulia(converter.fromRow(row, prefix + "_julia", String.class));
        return entity;
    }
}
