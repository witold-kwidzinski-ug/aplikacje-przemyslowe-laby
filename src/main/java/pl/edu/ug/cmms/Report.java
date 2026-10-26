package pl.edu.ug.cmms;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static pl.edu.ug.cmms.WorkOrder.WorkOrderStatus.*;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderType.*;

public class Report {
    private Report() {}


    public static Map<WorkOrder.Priority, Long> unfinishedOrders(List<WorkOrder> wo) {
        Map<WorkOrder.Priority, Long> res = wo.stream().filter(n -> n.status().equals(OPEN) || n.status().equals(IN_PROGRESS))
                .collect(Collectors.groupingBy(WorkOrder::priority, TreeMap::new, Collectors.counting()));

        for (WorkOrder.Priority p: WorkOrder.Priority.values()) {
            res.putIfAbsent(p, 0L);
        }

        return res;
    }

    public static List<Map.Entry<String, Long>> topMachineBreakdowns(List<WorkOrder> wo) {
        return wo.stream().filter(n -> n.type().equals(BREAKDOWN) && !n.status().equals(CANCELLED))
                .collect(Collectors.groupingBy(WorkOrder::machineId, Collectors.counting()))
                .entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .limit(3)
                .toList();
    }

    public static List<Map.Entry<String, Long>> mttr(List<WorkOrder> wo, List<Machine> m) {
        Map<String, List<String>> machines = m.stream().collect(Collectors.groupingBy(Machine::hall, Collectors.mapping(Machine::id, Collectors.toList())));

        Map<String, Long> hallSeconds = new TreeMap<>();
        Map<String, Long> hallAmount = new HashMap<>();

        for (String k: machines.keySet()) {
            hallSeconds.putIfAbsent(k, 0L);
            hallAmount.putIfAbsent(k, 0L);
        }

        List<WorkOrder> filteredWo = wo.stream().filter(n -> n.type().equals(BREAKDOWN) && n.status().equals(DONE)).toList();

        for (WorkOrder w: filteredWo) {
            String hall = machines.entrySet().stream().filter(n -> n.getValue().contains(w.machineId())).map(Map.Entry::getKey).findFirst().orElseThrow();

            Duration d = Duration.between(w.reportedAt(), w.closedAt());

            hallSeconds.put(hall, hallSeconds.get(hall) + d.toSeconds());
            hallAmount.put(hall, hallAmount.get(hall) + 1);
        }

        for (String k: hallSeconds.keySet()) {
            if (hallAmount.get(k) == 0) {
                continue;
            }

            long average = hallSeconds.get(k) / hallAmount.get(k);

            hallSeconds.put(k, average);
        }


        return hallSeconds.entrySet().stream().toList();
    }
}
