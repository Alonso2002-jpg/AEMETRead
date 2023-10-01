DROP TABLE IF EXISTS AEMET;
CREATE TABLE IF NOT EXISTS AEMET (
                                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                                 ActualDate DATE NOT NULL,
                                 Provincia TEXT NOT NULL,
                                 Localidad TEXT NOT NULL,
                                 MaxDegrees REAL,
                                 MinDegrees REAL,
                                 Precipitation REAL,
                                 MaxTempHour TEXT,
                                 MinTempHour TEXT
);

