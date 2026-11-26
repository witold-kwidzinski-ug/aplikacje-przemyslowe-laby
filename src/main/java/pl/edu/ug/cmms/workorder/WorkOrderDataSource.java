package pl.edu.ug.cmms.workorder;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class WorkOrderDataSource {

    private final List<WorkOrder> workOrders;
    private final Map<WorkOrder.Priority, Duration> sla;
    private final Clock clock;

    public WorkOrderDataSource(List<WorkOrder> wo, Map<WorkOrder.Priority, Duration> sla, Clock clock) {
        this.workOrders = wo;
        this.sla = sla;
        this.clock = clock;
    }

    public Map<WorkOrder.Priority, Long> finishedOrdersOverSlaLimit() {
        Map<WorkOrder.Priority, Long> map = new HashMap<>();
        for (WorkOrder.Priority p: WorkOrder.Priority.values()) {
            map.put(p, 0L);
        }

        for (WorkOrder o : this.workOrders) {
            if (o.status() != WorkOrder.WorkOrderStatus.DONE) continue;
            Duration time = Duration.between(o.reportedAt(), o.closedAt());

            if (time.compareTo(this.sla.get(o.priority())) > 0) {
                map.put(o.priority(), map.get(o.priority()) + 1);
            }
        }

        return map;
    }

    public List<String> unfinishedOrdersOverLimit() {
        LocalDateTime now = LocalDateTime.now(this.clock);

        return this.workOrders.stream().filter(n -> n.status() == WorkOrder.WorkOrderStatus.OPEN || n.status() == WorkOrder.WorkOrderStatus.IN_PROGRESS)
                .filter(n -> Duration.between(n.reportedAt(), now).compareTo(this.sla.get(n.priority())) > 0)
                .map(WorkOrder::id)
                .toList();

    }

}
