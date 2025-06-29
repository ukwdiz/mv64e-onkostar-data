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
Hierbei wird das letzte Formular `DNPM Klinik/Anamnese` andhand des Anmeldedatums MTB
ausgewählt und verwendet.

```
...

var jsonResult = Converter.toJsonString(
  mtbMapper.getLatestByPatientIdAndTumorId("2000123456", 1))
);
```

## Status

| DNPM-Datenmodell 2.1 - Bereich   | Status | Anmerkung                                                                  |
|----------------------------------|--------|----------------------------------------------------------------------------|
| Patient                          | ✅      | Verwendet Datenbank-ID, keine managing Site                                |
| Episoden                         | ✅      |                                                                            |
| Diagnosen                        | ✅      | Entsprechend Formularaufbau nur Diagnose der aktuellen Episode             |
| Verwandten-Diagnosen             | ✅      |                                                                            |
| Systemische Leitlinien-Therapien | ✅      | Siehe auch: https://github.com/dnpm-dip/mtb-model/issues/9                 |
| Leitlinien-Prozeduren            | ✅      | Siehe auch: https://github.com/dnpm-dip/mtb-model/issues/9                 |
| ECOG-Verlauf                     | ✅      |                                                                            |
| Tumor-Proben                     | ⛅      | Best effort: Formular OS.Molekulargenetik erfüllt nicht alle Anforderungen |
| vorherige Molekular-Diagnostik   | ⌛      | Aktuell in Arbeit                                                          |
| Histologie-Berichte              | ⌛      | Aktuell in Arbeit                                                          |
| IHC-Berichte                     |        |                                                                            |
| MSI-Befunde                      |        |                                                                            |
| NGS-Berichte                     | ⌛      | Aktuell in Arbeit                                                          |
| MTB-Beschlüsse                   | ⌛      | Aktuell in Arbeit                                                          |
| Follow-Up Verlauf                |        |                                                                            |
| Antrag Kostenübernahme           |        |                                                                            |
| Antwort Kostenübernahme          |        |                                                                            |
| Therapien                        |        |                                                                            |
| Response Befunde                 |        |                                                                            |

