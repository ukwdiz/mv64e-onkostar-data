# Datenextraktion aus den Onkostar DNPM-Formularen

Ziel dieser Library ist die Bereitstellung von Methoden zum Laden von Daten der DNPM-Formulare aus der
Onkostar-Datenbank
und dem Mapping in das DNPM-Datenmodell 2.1.

## Beispiel

Beispiel zur Konfiguration der Datenbankanbindung, Abruf über eine Fallnummer im Formular DNPM Klinik/Anamnese und
Serialisierung als JSON-String.

```
var datasource = new MariaDbDataSource();
datasource.setUrl("jdbc:mariadb://localhost:3306/onkostar");
datasource.setUser("onkostar");
datasource.setPassword("devpass");

var mtbMapper = MtbDataMapper.create(datasource);

var jsonResult = Converter.toJsonString(
  mtbMapper.getByCaseId("16000123")
);
```

Es ist auch möglich, die Daten anhand der Patienten-ID und dem Tumoridentifikator zu ermitteln.
Hierbei wird das letzte Formular `DNPM Klinik/Anamnese` anhand des Anmeldedatums MTB
ausgewählt und verwendet.

```
...

var jsonResult = Converter.toJsonString(
  mtbMapper.getLatestByPatientIdAndTumorId("2000123456", 1))
);
```

## Status

Das Projekt befindet sich aktuell in einem sehr frühen Entwicklungsstand und kann daher auch bei Status ✅ Probleme
aufweisen.
Um Mithilfe wird gebeten.

| DNPM-Datenmodell 2.1 - Bereich   | Status | Anmerkung                                                                      |
|----------------------------------|--------|--------------------------------------------------------------------------------|
| MV Metadaten                     | ⛅      | MV Consent anhand DNPM-Formular. Kein Broad Consent                            |                    
| Patient                          | ✅      | Verwendet Patienten-ID, nicht Datenbank-ID. Keine Managing Site                |
| Episoden                         | ✅      |                                                                                |
| Diagnosen                        | ✅      | Entsprechend Formularaufbau nur Diagnose der aktuellen Episode                 |
| Verwandten-Diagnosen             | ✅      |                                                                                |
| Systemische Leitlinien-Therapien | ✅      | Siehe auch: https://github.com/dnpm-dip/mtb-model/issues/9                     |
| Leitlinien-Prozeduren            | ✅      | Siehe auch: https://github.com/dnpm-dip/mtb-model/issues/9                     |
| ECOG-Verlauf                     | ✅      |                                                                                |
| Tumor-Proben                     | ⛅      | Best effort: Formular OS.Molekulargenetik erfüllt nicht alle Anforderungen (1) |
| vorherige Molekular-Diagnostik   | ✅      |                                                                                |
| Histologie-Berichte              | ✅      |                                                                                |
| IHC-Berichte                     | -      | Aktuell nicht vorgesehen                                                       |
| MSI-Befunde                      | ⌛      | Aktuell in Arbeit, https://github.com/dnpm-dip/mtb-model/issues/10 ist behoben |
| NGS-Berichte                     | ⛅      | Best effort: Formular OS.Molekulargenetik erfüllt nicht alle Anforderungen (2) |
| MTB-Beschlüsse                   | ✅      | Stützende molekulare Alteration(en) für einfache Variante und CNV (3)          |
| Follow-Up Verlauf                | -      | Späterer Zeitpunkt                                                             |
| Antrag Kostenübernahme           | -      | Späterer Zeitpunkt                                                             |
| Antwort Kostenübernahme          | -      | Späterer Zeitpunkt                                                             |
| Therapien                        | -      | Späterer Zeitpunkt                                                             |
| Response Befunde                 | -      | Späterer Zeitpunkt                                                             |

### Hinweise

1. Nicht alle möglichen Ausprägungen in `OS.Molekulargenetik` vorhanden.
2. Aktuell nicht alle Angaben effektiv im Formular `OS.Molekulargenetik` wie gefordert angebbar.
   Hinweis: Tumorzellgehalt-Methode problematisch, wenn auch im NGS-Bericht histologisch festgestellt.
3. Implementierung des Mappings von HGNC-Symbol (Gen-Name) zu HGNC-ID über enthaltene Gen-Liste.

## Enthaltene Liste mit Genen

Es ist eine Liste mit über 43000 Genen
von [https://genenames.org](https://www.genenames.org/cgi-bin/download/custom?col=gd_hgnc_id&col=gd_app_sym&col=gd_app_name&col=gd_pub_chrom_map&col=md_ensembl_id&status=Approved&hgnc_dbtag=on&order_by=gd_app_sym_sort&format=text&submit=submit)
enthalten.

Diese Liste der Gene unterliegt der folgenden Lizenz und ist frei
verfügbar: [Creative Commons Public Domain (CC0) License](https://creativecommons.org/public-domain/cc0/).

## Zusätzliche Informationen

Weitere benötigte Informationen können ebenfalls abgerufen werden.

```java
var customMetadataMapper = new CustomMetadataDataMapper(
        KpaCatalogue.create(new JdbcTemplate(datasource)),
        PatientCatalogue.create(new JdbcTemplate(datasource))
);

var additional1 = customMetadataMapper.getByCaseId("16000123");
// oder
var additional2 = customMetadataMapper.getLatestByPatientIdAndTumorId("2000123456", 1);
```

Aktuell wird hier die Fallnummer und Krankenversicherungsnummer abgerufen.
