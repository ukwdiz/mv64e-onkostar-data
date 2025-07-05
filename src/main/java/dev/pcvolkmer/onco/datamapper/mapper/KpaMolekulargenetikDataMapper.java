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

package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.*;
import dev.pcvolkmer.onco.datamapper.genes.GeneUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper class to load and map prozedur data from database table 'dk_molekulargenetik'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaMolekulargenetikDataMapper implements DataMapper<SomaticNgsReport> {

    private final MolekulargenetikCatalogue catalogue;
    private final MolekulargenuntersuchungCatalogue untersuchungCatalogue;

    public KpaMolekulargenetikDataMapper(
            final MolekulargenetikCatalogue catalogue,
            final MolekulargenuntersuchungCatalogue untersuchungCatalogue,
            final PropertyCatalogue propertyCatalogue
    ) {
        this.catalogue = catalogue;
        this.untersuchungCatalogue = untersuchungCatalogue;
    }

    /**
     * Loads and maps Prozedur related by database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded Procedure
     */
    @Override
    public SomaticNgsReport getById(final int id) {
        var data = catalogue.getById(id);

        var builder = SomaticNgsReport.builder();
        builder
                .id(data.getString("id"))
                .patient(data.getPatientReference())
                .issuedOn(data.getDate("datum"))
                .specimen(Reference.builder().id(data.getString("einsendenummer")).type("Specimen").build())
                .results(this.getNgsReportResults(id))
        ;

        return builder.build();

    }

    /**
     * Loads and maps all Prozedur related by KPA database id
     *
     * @param kpaId The database id of the KPA procedure data set
     * @return The loaded Procedures
     */
    public List<SomaticNgsReport> getAllByKpaId(final int kpaId) {
        return this.catalogue.getIdsByKpaId(kpaId).stream()
                .distinct()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    private NgsReportResults getNgsReportResults(int id) {
        var subforms = this.untersuchungCatalogue.getAllByParentId(id);

        var resultBuilder = NgsReportResults.builder();
        resultBuilder.simpleVariants(
                subforms.stream()
                        // P => Einfache Variante
                        .filter(subform -> "P".equals(subform.getString("ergebnis")))
                        .map(subform -> {
                            final var geneOptional = GeneUtils.findBySymbol(subform.getString("untersucht"));
                            if (geneOptional.isEmpty()) {
                                return null;
                            }

                            final var snvBuilder = Snv.builder()
                                    .id(subform.getString("id"))
                                    .patient(subform.getPatientReference())
                                    .gene(GeneUtils.toCoding(geneOptional.get()))
                                    .exonId(subform.getString("exon"))
                                    .dnaChange(subform.getString("cdnanomenklatur"))
                                    .proteinChange(subform.getString("proteinebenenomenklatur"));

                            if (null != subform.getLong("allelfrequenz")) {
                                snvBuilder.allelicFrequency(subform.getLong("allelfrequenz"));
                            }
                            if (null != subform.getLong("evreaddepth")) {
                                snvBuilder.readDepth(subform.getLong("evreaddepth"));
                            }
                            geneOptional.get().getSingleChromosomeInPropertyForm().ifPresent(snvBuilder::chromosome);

                            return snvBuilder.build();
                        })
                        .collect(Collectors.toList())
        );

        resultBuilder.copyNumberVariants(
                subforms.stream()
                        .filter(subform -> "CNV".equals(subform.getString("ergebnis")))
                        .map(subform -> {
                            final var geneOptional = GeneUtils.findBySymbol(subform.getString("untersucht"));
                            if (geneOptional.isEmpty()) {
                                return null;
                            }

                            final var reportedAffectedGenes = new ArrayList<String>();
                            reportedAffectedGenes.add(subform.getString("untersucht"));

                            // Weitere betroffene Gene aus Freitextfeld?
                            if (null != subform.getString("cnvbetroffenegene")) {
                                reportedAffectedGenes.addAll(
                                        Arrays.stream(subform.getString("cnvbetroffenegene").split("\\s")).collect(Collectors.toList())
                                );
                            }

                            final var cnvBuilder = Cnv.builder()
                                    .id(subform.getString("id"))
                                    .patient(subform.getPatientReference())
                                    .reportedAffectedGenes(
                                            reportedAffectedGenes.stream()
                                                    .distinct()
                                                    .map(GeneUtils::findBySymbol)
                                                    .filter(Optional::isPresent)
                                                    .map(gene -> GeneUtils.toCoding(gene.get()))
                                                    .collect(Collectors.toList())
                                    )
                                    .totalCopyNumber(subform.getLong("cnvtotalcn"));

                            geneOptional.get().getSingleChromosomeInPropertyForm().ifPresent(cnvBuilder::chromosome);

                            return cnvBuilder.build();
                        })
                        .collect(Collectors.toList())
        );
        return resultBuilder.build();
    }

}
