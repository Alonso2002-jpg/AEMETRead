
DROP TABLE IF EXISTS AEMET;
CREATE TABLE IF NOT EXISTS AEMET (
                                 Actual_Date DATE NOT NULL,
                                 Provincia TEXT NOT NULL,
                                 Localidad TEXT NOT NULL,
                                 MaxDegrees REAL,
                                 MinDegrees REAL,
                                 Precipitation REAL,
                                 MaxTempHour TEXT,
                                 MinTempHour TEXT,
);

