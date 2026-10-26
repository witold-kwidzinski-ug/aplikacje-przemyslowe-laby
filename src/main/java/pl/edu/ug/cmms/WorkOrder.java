package pl.edu.ug.cmms;

import java.time.LocalDateTime;
import java.util.Objects;

public record WorkOrder (String id, String machineId, WorkOrderType type, Priority priority, WorkOrderStatus status, LocalDateTime reportedAt, LocalDateTime closedAt) {
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        CRITICAL
    }

    public enum WorkOrderStatus {
        OPEN,
        DONE,
        CANCELLED,
        IN_PROGRESS
    }

    public enum WorkOrderType {
        BREAKDOWN,
        INSPECTION,
        PREVENTIVE
    }

    public WorkOrder {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id can't be blank.");
        }
        if (machineId == null || machineId.isBlank()) {
            throw new IllegalArgumentException("MachineId can't be blank.");
        }

        Objects.requireNonNull(type, "Type can't be null.");
        Objects.requireNonNull(priority, "Priority can't be null.");
        Objects.requireNonNull(status, "Status can't be null.");
        Objects.requireNonNull(reportedAt, "ReportedAt can't be null.");

        if (status == WorkOrderStatus.DONE && closedAt == null) {
            throw new IllegalArgumentException("Done work order must have a closing date.");
        }

        if (closedAt != null && closedAt.isBefore(reportedAt)) {
            throw new IllegalArgumentException("Closing date can't be before reported date");
        }

    }

}



