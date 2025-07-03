/*
 * This file is part of mv64e-onkostar-data
 *
 * Copyright (C) 2025  Paul-Christian Volkmer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Common implementations for all data catalogues
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public abstract class AbstractDataCatalogue implements DataCatalogue {

    protected final JdbcTemplate jdbcTemplate;

    protected AbstractDataCatalogue(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected abstract String getTableName();

    /**
     * Get procedure result set by procedure id
     *
     * @param id The procedure id
     * @return The procedure id
     */
    @Override
    public ResultSet getById(int id) {
        var result = this.jdbcTemplate.queryForList(
                String.format(
                        "SELECT * FROM %s JOIN prozedur ON (prozedur.id = %s.id) WHERE geloescht = 0 AND prozedur.id = ?",
                        getTableName(),
                        getTableName()
                ),
                id);

        if (result.isEmpty()) {
            throw new DataAccessException("No record found for id: " + id);
        } else if (result.size() > 1) {
            throw new DataAccessException("Multiple records found for id: " + id);
        }

        return ResultSet.from(result.get(0));
    }

    /**
     * Returns related diseases
     * @param procedureId The procedure id
     * @return the diseases
     */
    public List<ResultSet> getDiseases(int procedureId) {
        return this.jdbcTemplate.queryForList(
                        String.format(
                                "SELECT * FROM erkrankung_prozedur JOIN erkrankung ON (erkrankung.id = erkrankung_prozedur.erkrankung_id) WHERE erkrankung_prozedur.prozedur_id = ?",
                                getTableName(),
                                getTableName()
                        ),
                        procedureId)
                .stream()
                .map(ResultSet::from)
                .collect(Collectors.toList());
    }
}
