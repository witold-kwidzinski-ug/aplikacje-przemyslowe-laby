package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static pl.edu.ug.cmms.WorkOrder.Priority.*;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderStatus.*;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderType.*;

public class TestWorkOrderDataSource {

    @Test
    void testFinishedOrdersOverSlaLimit() {
        try (MockedStatic<SampleData> sd = Mockito.mockStatic(SampleData.class)) {
            sd.when(SampleData::workOrders).thenReturn(
                    List.of(
                            new WorkOrder("WO-0001", "M-01", PREVENTIVE, NORMAL, DONE,
                                    LocalDateTime.parse("2026-09-20T15:45"),
                                    LocalDateTime.parse("2026-09-20T23:05")),

                            new WorkOrder("WO-0002", "M-02", PREVENTIVE, NORMAL, DONE,
                                    LocalDateTime.parse("2026-09-18T10:00"),
                                    LocalDateTime.parse("2026-09-20T00:30")),

                            new WorkOrder("WO-0003", "M-03", BREAKDOWN, HIGH, DONE,
                                    LocalDateTime.parse("2026-09-19T08:00"),
                                    LocalDateTime.parse("2026-09-20T02:30")),

                            new WorkOrder("WO-0004", "M-04", BREAKDOWN, LOW, OPEN,
                                    LocalDateTime.parse("2026-09-15T08:00"),
                                    null),

                            new WorkOrder("WO-0005", "M-05", BREAKDOWN, HIGH, OPEN,
                                    LocalDateTime.parse("2026-09-19T08:00"),
                                    null),

                            new WorkOrder("WO-0006", "M-06", BREAKDOWN, CRITICAL, IN_PROGRESS,
                                    LocalDateTime.parse("2026-09-20T20:00"),
                                    null)
                    )
            );

            Clock clock = Clock.fixed(LocalDateTime.of(2026, 9, 21, 6, 0)
                            .atZone(ZoneId.systemDefault())
                            .toInstant(),
                    ZoneId.systemDefault());

            Map<WorkOrder.Priority, Duration> sla = new HashMap<>();
            sla.put(WorkOrder.Priority.CRITICAL, Duration.ofHours(4));
            sla.put(WorkOrder.Priority.HIGH, Duration.ofHours(16));
            sla.put(WorkOrder.Priority.NORMAL, Duration.ofHours(36));
            sla.put(WorkOrder.Priority.LOW, Duration.ofHours(264));

            WorkOrderDataSource workOrderDataSource = new WorkOrderDataSource(SampleData.workOrders(), sla, clock);

            Map<WorkOrder.Priority, Long> res = workOrderDataSource.finishedOrdersOverSlaLimit();


            assertAll("Priorities over SLA limit",
                    () -> assertEquals(0, res.get(WorkOrder.Priority.CRITICAL)),
                    () -> assertEquals(1, res.get(WorkOrder.Priority.HIGH)),
                    () -> assertEquals(1, res.get(WorkOrder.Priority.NORMAL)),
                    () -> assertEquals(0, res.get(WorkOrder.Priority.LOW))
            );
        }
    }

    @Test
    void testUnfinishedOrdersOverLimit() {
        try (MockedStatic<SampleData> sd = Mockito.mockStatic(SampleData.class)) {
            sd.when(SampleData::workOrders).thenReturn(
                    List.of(
                            new WorkOrder("WO-0001", "M-01", PREVENTIVE, NORMAL, DONE,
                                    LocalDateTime.parse("2026-09-20T15:45"),
                                    LocalDateTime.parse("2026-09-20T23:05")),

                            new WorkOrder("WO-0002", "M-02", PREVENTIVE, NORMAL, DONE,
                                    LocalDateTime.parse("2026-09-18T10:00"),
                                    LocalDateTime.parse("2026-09-20T00:30")),

                            new WorkOrder("WO-0003", "M-03", BREAKDOWN, HIGH, DONE,
                                    LocalDateTime.parse("2026-09-19T08:00"),
                                    LocalDateTime.parse("2026-09-20T02:30")),

                            new WorkOrder("WO-0004", "M-04", BREAKDOWN, LOW, OPEN,
                                    LocalDateTime.parse("2026-09-15T08:00"),
                                    null),

                            new WorkOrder("WO-0005", "M-05", BREAKDOWN, HIGH, OPEN,
                                    LocalDateTime.parse("2026-09-19T08:00"),
                                    null),

                            new WorkOrder("WO-0006", "M-06", BREAKDOWN, CRITICAL, IN_PROGRESS,
                                    LocalDateTime.parse("2026-09-20T20:00"),
                                    null),
                            new WorkOrder("WO-0007", "M-07", BREAKDOWN, CRITICAL, CANCELLED,
                                    LocalDateTime.parse("2026-09-20T08:00"),
                                    null)
                    )
            );

            Clock clock = Clock.fixed(LocalDateTime.of(2026, 9, 21, 6, 0)
                            .atZone(ZoneId.systemDefault())
                            .toInstant(),
                    ZoneId.systemDefault());

            Map<WorkOrder.Priority, Duration> sla = new HashMap<>();
            sla.put(WorkOrder.Priority.CRITICAL, Duration.ofHours(4));
            sla.put(WorkOrder.Priority.HIGH, Duration.ofHours(16));
            sla.put(WorkOrder.Priority.NORMAL, Duration.ofHours(36));
            sla.put(WorkOrder.Priority.LOW, Duration.ofHours(264));

            WorkOrderDataSource workOrderDataSource = new WorkOrderDataSource(SampleData.workOrders(), sla, clock);

            List<String> unfinishedOrdersOverLimit = workOrderDataSource.unfinishedOrdersOverLimit();

            assertEquals(List.of("WO-0005", "WO-0006"), unfinishedOrdersOverLimit);
        }

    }
}
