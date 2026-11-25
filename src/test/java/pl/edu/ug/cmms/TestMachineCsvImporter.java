package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class TestMachineCsvImporter {

    @TempDir
    Path dir;

    @Test
    void testImportWithWrongCsvValues() throws IOException {
        String csv = "id;name;type;hall;installedOn\n" +
                "                M-25;Tokarka CNC 4;CNC_LATHE;A;2024-03-11\n" +
                "                M-26;Wtryskarka 3;INJECTION_MOLDER;B;2025-06-02\n" +
                "                M-27;Wycinarka laserowa 1;LASER_CUTTER;C;2025-01-15\n" +
                "                M-28;Frezarka 5;MILLING_MACHINE;C;2023-11-20\n" +
                "                M-29;Prasa hydrauliczna 4;HYDRAULIC_PRESS;A;2025-02-30\n" +
                "                M-01;Sprężarka 4;COMPRESSOR;B;2022-05-05\n" +
                "                M-31;Przenośnik taśmowy 5;CONVEYOR;B;2021-04-01\n" +
                "                M-25;Tokarka CNC 5;CNC_LATHE;A;2024-04-01\n" +
                "                M-32;Frezarka 6;MILLING_MACHINE;A;2026-01-10";

        Path file = dir.resolve("machines.csv");
        Files.writeString(file, csv);

        MachineCsvImporter importer = new MachineCsvImporter(file);

        List<Machine> existingMachines = List.of(
                new Machine(
                        "M-01",
                        "Tokarka testowa",
                        Machine.MachineType.CNC_LATHE,
                        "A",
                        LocalDate.of(2020, 1, 10)
                ),
                new Machine(
                        "M-02",
                        "Frezarka testowa",
                        Machine.MachineType.MILLING_MACHINE,
                        "B",
                        LocalDate.of(2021, 5, 15)
                )
        );

        ImportedMachineInfo info = importer.importMachines(existingMachines);

        assertAll("Czytanie Csv",
            () -> assertEquals(5, info.accepted()),
            () -> assertEquals(4, info.rejected()),
            () -> assertEquals(7, info.machines().size()),
            () -> assertEquals(4, info.rejectedMachines().size()),
            () -> assertTrue(info.rejectedMachines().stream()
                    .anyMatch(row -> row.contains("4. Nieznany typ maszyny: LASER_CUTTER"))),

            () -> assertTrue(info.rejectedMachines().stream()
                    .anyMatch(row -> row.contains("6. Niepoprawna data: 2025-02-30"))),

            () -> assertTrue(info.rejectedMachines().stream()
                    .anyMatch(row -> row.contains("7. ID już istnieje: M-01"))),

            () -> assertTrue(info.rejectedMachines().stream()
                    .anyMatch(row -> row.contains("9. Powtórzone ID w pliku CSV: M-25")))
        );

    }
}
