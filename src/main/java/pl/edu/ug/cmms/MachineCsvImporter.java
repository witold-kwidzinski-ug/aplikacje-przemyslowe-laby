package pl.edu.ug.cmms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;;


@Component
public class MachineCsvImporter {
    private final Path file;

    public MachineCsvImporter(@Value("${cmms.import.machines-file}") Path file) {
        this.file = file;
    }

    public ImportedMachineInfo importMachines(List<Machine> existingMachines) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

        List<Machine> machines = new ArrayList<>(existingMachines);

        List<String> rejectedMachines = new ArrayList<>();

        Set<String> existingIds = existingMachines.stream().map(Machine::id).collect(Collectors.toSet());
        Set<String> importedIds = new HashSet<>();

        long accepted = 0;
        long rejected = 0;

        for (int i = 1; i < lines.size(); i++) {
            String[] lineArray = lines.get(i).split(";", -1);

            if (lineArray.length != 5) {
                rejectedMachines.add(String.format("%d. Niepoprawna liczba kolumn", i + 1));
                rejected++;
                continue;
            }

            try {
                String id = lineArray[0].trim();
                if (existingIds.contains(id)) {
                    rejectedMachines.add(String.format("%d. ID już istnieje: %s", i + 1, id));
                    rejected++;
                    continue;
                }
                if (importedIds.contains(id)) {
                    rejectedMachines.add(String.format("%d. Powtórzone ID w pliku CSV: %s", i + 1, id));
                    rejected++;
                    continue;
                }

                String name = lineArray[1].trim();
                Machine.MachineType type;
                try {
                    type = Machine.MachineType.valueOf(lineArray[2].trim());
                } catch (IllegalArgumentException e) {
                    rejectedMachines.add(String.format("%d. Nieznany typ maszyny: %s", i+1, lineArray[2]));
                    rejected++;
                    continue;
                }
                String hall = lineArray[3].trim();

                LocalDate createdAt;
                try {
                    createdAt = LocalDate.parse(lineArray[4].trim());
                }  catch (DateTimeParseException e) {
                    rejectedMachines.add(String.format("%d. Niepoprawna data: %s", i+1, lineArray[4]));
                    rejected++;
                    continue;
                }

                Machine m =  new Machine(id, name, type, hall, createdAt);

                machines.add(m);
                importedIds.add(id);
                accepted++;

            } catch (IllegalArgumentException e) {
                rejectedMachines.add(String.format("%d. Niepoprawne dane maszyny: %s", i+1, e.getMessage()));
                rejected++;
            }
        }

        return new ImportedMachineInfo(machines, rejectedMachines, accepted, rejected);

    }

}
