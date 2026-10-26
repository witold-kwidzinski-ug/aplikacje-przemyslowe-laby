package pl.edu.ug.cmms;

import java.time.LocalDate;
import java.util.Objects;

public record Machine (
    String id,
    String name,
    MachineType type,
    String hall,
    LocalDate createdAt
    ) {
    public enum MachineType {
        CNC_LATHE,
        CONVEYOR,
        MILLING_MACHINE,
        WELDING_ROBOT,
        INJECTION_MOLDER,
        HYDRAULIC_PRESS,
        COMPRESSOR
    }

    public Machine {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id can't be blank.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name can't be blank.");
        }
        if (hall == null || hall.isBlank()) {
            throw new IllegalArgumentException("Hall can't be blank.");
        }

        Objects.requireNonNull(type, "Type can't be null.");
        Objects.requireNonNull(createdAt, "CreatedAt can't be null.");

    }

}
