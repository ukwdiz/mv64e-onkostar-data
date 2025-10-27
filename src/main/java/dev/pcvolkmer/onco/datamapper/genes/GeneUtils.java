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

package dev.pcvolkmer.onco.datamapper.genes;

import dev.pcvolkmer.mv64e.mtb.Coding;
import org.apache.commons.csv.CSVFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for genes
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class GeneUtils {

    private static final Logger logger = LoggerFactory.getLogger(GeneUtils.class);

    private GeneUtils() {
        // Empty
    }

    public static Optional<Gene> findByHgncId(String hgncId) {
        return genes().stream().filter(gene -> gene.getHgncId().equalsIgnoreCase(hgncId)).findFirst();
    }

    public static Optional<Gene> findBySymbol(String symbol) {
        return genes().stream().filter(gene -> gene.getSymbol().equalsIgnoreCase(symbol)).findFirst();
    }

    public static Coding toCoding(Gene gene) {
        return Coding.builder()
                .code(gene.getHgncId())
                .display(gene.getSymbol())
                .system("https://www.genenames.org/")
                .build();
    }

    private static List<Gene> genes() {
        var result = new ArrayList<Gene>();

        try {
            var inputStream = GeneUtils.class.getClassLoader().getResourceAsStream("genes.csv");
            CSVFormat format;

            // Fallback for local build dependencie issues: check csv package version.
            String csvVersion = CSVFormat.class.getPackage().getImplementationVersion();
            if (csvVersion == null || csvVersion.startsWith("1.10.")) {
                        format = CSVFormat.RFC4180
                        .withHeader()
                        .withSkipHeaderRecord()
                        .withDelimiter('\t');
            } else {
                format = CSVFormat.RFC4180.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setDelimiter('\t')
                        .get();
            }
            var parser = format.parse(new InputStreamReader(inputStream));

            for (var row : parser) {
                result.add(
                        new Gene(
                                row.get("HGNC ID"),
                                row.get("Ensembl ID(supplied by Ensembl)"),
                                row.get("Approved symbol"),
                                row.get("Approved name"),
                                row.get("Chromosome")
                        )
                );
            }

            return result;
        } catch (IOException e) {
            return List.of();
        } catch (NoSuchMethodError e) {
            logger.error(
                    "CSVFormat.get() not found! VERSION: " +
                            org.apache.commons.csv.CSVFormat.class.getPackage().getImplementationVersion(),
                    e);
            throw e;
        }
    }

}
