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

package dev.pcvolkmer.onco.datamapper.app;

import dev.pcvolkmer.mv64e.mtb.Converter;
import dev.pcvolkmer.mv64e.mtb.TumorCellContentMethodCodingCode;
import dev.pcvolkmer.onco.datamapper.mapper.MtbDataMapper;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.help.HelpFormatter;
import org.mariadb.jdbc.MariaDbDataSource;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Scanner;

public class ExportApplication {

    public static void main(String[] args) throws Exception {
        final var parsedCliArgs = DefaultParser.builder().get().parse(getCliOptions(), args);

        if (parsedCliArgs.hasOption("help") || !parsedCliArgs.hasOption("case-id")) {
            HelpFormatter.builder().setShowSince(false).get().printHelp(
                    "java -jar <dateiname>.jar",
                    "",
                    getCliOptions(),
                    "",
                    true);
            return;
        }

        var user = parsedCliArgs.getOptionValue("U", "root");
        var host = parsedCliArgs.getOptionValue("H", "localhost");
        var port = parsedCliArgs.getParsedOptionValue("P", 3306);
        var database = parsedCliArgs.getOptionValue("D", "onkostar");
        var caseId = "";

        if (parsedCliArgs.hasOption("case-id")) {
            caseId = parsedCliArgs.getParsedOptionValue("case-id");
            System.err.println(String.format("Exportiere Fallnummer '%s'", caseId));
        }

        System.err.print("Password: ");
        final Scanner scanner = new Scanner(System.in);
        var password = scanner.nextLine();

        var datasource = new MariaDbDataSource();
        datasource.setUrl(String.format("jdbc:mariadb://%s:%d/%s", host, port, database));
        datasource.setUser(user);
        datasource.setPassword(password);

        var mtbMapper = MtbDataMapper.create(datasource);
        if (parsedCliArgs.hasOption("filter-incomplete")) {
            mtbMapper = mtbMapper.filterIncomplete();
        }
        if (parsedCliArgs.hasOption("histologic-tumor-cell-count")) {
            mtbMapper = mtbMapper.tumorCellContentMethod(TumorCellContentMethodCodingCode.HISTOLOGIC);
        }
        var json = Converter.toJsonString(mtbMapper.getByCaseId(caseId));

        if (parsedCliArgs.hasOption("filename")) {
            var writer = new PrintWriter(Path.of(parsedCliArgs.getOptionValue("filename")).toFile());
            writer.println(json);
            writer.close();
        } else {
            System.out.println(json);
        }
    }

    private static Options getCliOptions() {
        Options options = new Options();
        options.addOption(Option.builder().longOpt("help").desc("Zeige diese Nachricht").get());
        options.addOption(Option.builder("U").longOpt("user").hasArg().desc("Database username (Standard: 'root')").get());
        options.addOption(Option.builder("H").longOpt("host").hasArg().desc("Database host (Standard: 'localhost')").get());
        options.addOption(Option.builder("P").longOpt("port").hasArg().type(Integer.class).desc("Database port (Standard: '3306')").get());
        options.addOption(Option.builder("D").longOpt("database").hasArg().desc("Database name (Standard: 'onkostar')").get());
        options.addOption(Option.builder().longOpt("case-id").hasArg().desc("MV §64e Fallnummer (Erforderlich!)").get());
        options.addOption(Option.builder().longOpt("filename").hasArg().desc("Ausgabe in Datei").get());
        options.addOption(Option.builder().longOpt("filter-incomplete").desc("Filtere unvollständige Items").get());
        options.addOption(Option.builder().longOpt("histologic-tumor-cell-count").desc("Nimm histologische Ermittlung des Tumorzellgehalts an").get());
        return options;
    }

}
