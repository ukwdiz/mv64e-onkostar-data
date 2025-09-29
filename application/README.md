# Beispielanwendung zur Datenextraktion aus den Onkostar DNPM-Formularen

Diese Beispielanwendung ermöglicht den Export aus der Onkostar-Datenbank.

## Anwendung

Die Jar-Datei enthält alle Abhängigkeiten und lässt sich mit folgendem Befehl ausführen: 

```
java -jar <dateiname>.jar [OPTIONS] 
```

Folgende Optionen sind möglich: 

```
            Options                              Description               
--help                            Zeige diese Nachricht                   
-U, --user <arg>                  Database username (Standard: 'root')    
-H, --host <arg>                  Database host (Standard: 'localhost')   
-P, --port <arg>                  Database port (Standard: '3306')        
-D, --database <arg>              Database name (Standard: 'onkostar')    
--case-id <arg>                   MV §64e Fallnummer (Erforderlich!)      
--filename <arg>                  Ausgabe in Datei                        
--filter-incomplete               Filtere unvollständige Items            
--histologic-tumor-cell-count     Histologische Ermittlung des Tumorzellgehalts  
```

Die Angabe eines Wertes für die Option `--case-id` ist obligatorisch.

Wird die Option `--filename` nicht verwendet, wird der JSON-String auf der Konsole ausgegeben.
