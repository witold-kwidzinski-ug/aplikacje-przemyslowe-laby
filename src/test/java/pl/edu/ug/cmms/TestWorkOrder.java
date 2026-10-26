package pl.edu.ug.cmms;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import static pl.edu.ug.cmms.WorkOrder.Priority.*;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderStatus.*;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderType.*;


public class TestWorkOrder {

    @Test
    void createWorkOrderValid() {
        WorkOrder w = new WorkOrder("WO-0001", "M-06", PREVENTIVE, NORMAL, DONE, LocalDateTime.parse("2026-03-02T15:45"), LocalDateTime.parse("2026-03-02T23:05"));
        assertEquals("WO-0001", w.id());
        assertEquals("M-06", w.machineId());
        assertEquals(PREVENTIVE, w.type());
        assertEquals(NORMAL, w.priority());
        assertEquals(DONE, w.status());
        assertEquals(LocalDateTime.parse("2026-03-02T15:45"), w.reportedAt());
        assertEquals(LocalDateTime.parse("2026-03-02T23:05"), w.closedAt());
    }

    @Test
    void createWorkOrderWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder(null, "M-06", PREVENTIVE, NORMAL, DONE, LocalDateTime.parse("2026-03-02T15:45"), LocalDateTime.parse("2026-03-02T23:05")));
    }

    @Test
    void createWorkOrderWithIncompatibleDates() {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder("WO-0001", "M-06", PREVENTIVE, NORMAL, DONE, LocalDateTime.parse("2026-03-02T23:05"), LocalDateTime.parse("2026-03-02T15:45")));
    }

    @Test
    void createWorkOrderWithNoClosedAt() {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder("WO-0001", "M-06", PREVENTIVE, NORMAL, DONE, LocalDateTime.parse("2026-03-02T15:45"), null));
    }

    @Test
    void createWorkOrderNotDoneWithNoClosedAt() {
        WorkOrder w = new WorkOrder("WO-0001", "M-06", PREVENTIVE, NORMAL, OPEN, LocalDateTime.parse("2026-03-02T15:45"), null);

        assertNull(w.closedAt());
        assertEquals(OPEN, w.status());
    }

}
