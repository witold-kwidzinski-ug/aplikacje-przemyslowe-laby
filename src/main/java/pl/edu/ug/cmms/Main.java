package pl.edu.ug.cmms;

import java.time.Duration;
import java.util.List;

import java.util.Map;
import java.util.TreeMap;

import static pl.edu.ug.cmms.WorkOrder.Priority.*;

public class Main {

    public static void main(String[] args) {
        List<Machine> m = SampleData.machines();
        List<WorkOrder> wo = SampleData.workOrders();


        System.out.println("Ilosc maszyn: " + m.size());
        System.out.println("Ilosc zadan: " + wo.size());


        Map<WorkOrder.Priority, Long> uO = new TreeMap<>(Report.unfinishedOrders(wo));

        System.out.println("\n===== Nieskonczone zlecenia =====");
        for (WorkOrder.Priority k: List.of(CRITICAL, HIGH, NORMAL, LOW)) {
            System.out.println(k + ": " + uO.get(k));
        }

        System.out.println("\n===== Ranking awarii (top 3) =====");
        for (Map.Entry<String, Long> entry: Report.topMachineBreakdowns(wo)) {
            System.out.println(entry.getKey() + " (" + entry.getValue() + ")");
        }

        System.out.println("\n===== MTTR =====");
        for (Map.Entry<String, Long> entry: Report.mttr(wo, m)) {
            long seconds = entry.getValue();
            System.out.println(entry.getKey() + " " + Duration.ofSeconds(seconds).toHoursPart() + " h " + Duration.ofSeconds(seconds).toMinutesPart() + " m");
        }


    }

}
