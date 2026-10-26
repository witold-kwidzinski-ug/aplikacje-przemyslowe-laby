package pl.edu.ug.cmms;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

import static pl.edu.ug.cmms.Machine.MachineType.*;
import static pl.edu.ug.cmms.WorkOrder.Priority.*;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderStatus.*;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderType.*;
import pl.edu.ug.cmms.WorkOrder.Priority;
import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;
import pl.edu.ug.cmms.WorkOrder.WorkOrderType;


public class TestReport {

    private static WorkOrder order(String id, String machineId, WorkOrderType type, Priority priority,
                                   WorkOrderStatus status, String reportedAt, String closedAt) {
        return new WorkOrder(id, machineId, type, priority, status, LocalDateTime.parse(reportedAt),
                closedAt == null ? null : LocalDateTime.parse(closedAt));
    }


    @Test
    void MTTRTest() {
        List<Machine> machines = List.of(
                new Machine(
                        "M-01", "Maszyna A",
                        CNC_LATHE,
                        "A", LocalDate.parse("2025-01-01")
                ),
                new Machine(
                        "M-02", "Maszyna B",
                        CONVEYOR,
                        "B", LocalDate.parse("2025-01-01")
                )
        );

        List<WorkOrder> orders = List.of(
                order(
                        "WO-01", "M-01",
                        BREAKDOWN,
                        HIGH,
                        DONE,
                        "2025-01-01T08:00",
                        "2025-01-01T10:30"
                ),
                order(
                        "WO-02", "M-01",
                        BREAKDOWN,
                        NORMAL,
                        DONE,
                        "2025-01-02T08:00",
                        "2025-01-02T11:30"
                ),
                order(
                        "WO-03", "M-02",
                        BREAKDOWN,
                        HIGH,
                        DONE,
                        "2025-01-03T08:00",
                        "2025-01-03T12:00"
                )
        );

        List<Map.Entry<String, Long>> result =
                Report.mttr(orders, machines);

        Map<String, Long> mttr = result.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        assertEquals(3 * 60 * 60, mttr.get("A"));
        assertEquals(4 * 60 * 60, mttr.get("B"));



    }

}
