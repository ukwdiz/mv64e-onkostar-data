# Datenextraktion aus den Onkostar DNPM-Formularen

Ziel dieser Library ist die Bereitstellung von Methoden zum Laden von Daten der DNPM-Formulare aus der Onkostar-Datenbank
und dem Mapping in das DNPM-Datenmodell 2.1.

## Beispiel

Beispiel zur Konfiguration der Datenbankanbindung, Abruf über eine Fallnummer im Formular DNPM Klinik/Anamnese und Serialisierung als JSON-String.

```
var datasource = new MariaDbDataSource();
datasource.setUrl("jdbc:mariadb://localhost:3306/onkostar");
datasource.setUser("onkostar");
datasource.setPassword("devpass");

var mtbMapper = MtbDataMapper.create(datasource);

var jsonResult = Converter.toJsonString(mtbMapper.getByCaseId("16000123"));
```
