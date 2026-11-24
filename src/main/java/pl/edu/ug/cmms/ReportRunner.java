package pl.edu.ug.cmms;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.edu.ug.cmms.ReportData.*;


import static pl.edu.ug.cmms.WorkOrder.Priority.*;

public class ReportRunner implements ApplicationRunner {
    private final List<Machine> m;
    private final List<WorkOrder> wo;
    private final long top;
    private final String xml;
    private final ReportXmlWriter xmlWriter;
    private final WorkOrderDataSource workOrderDataSource;

    public ReportRunner(List<Machine> machines, List<WorkOrder> workOrders, long top, String xmlFile, ReportXmlWriter xmlWriter,  WorkOrderDataSource workOrderDataSource) {
        this.m = machines;
        this.wo = workOrders;
        this.top = top;
        this.xml = xmlFile;
        this.xmlWriter = xmlWriter;
        this.workOrderDataSource = workOrderDataSource;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        long machineAmount = m.size();
        long orderAmount = wo.size();

        System.out.println("Ilosc maszyn: " + machineAmount);
        System.out.println("Ilosc zadan: " + orderAmount);


        Map<WorkOrder.Priority, Long> uO = new TreeMap<>(Report.unfinishedOrders(this.wo));

        System.out.println("\n===== Nieskonczone zlecenia =====");
        for (WorkOrder.Priority k: List.of(CRITICAL, HIGH, NORMAL, LOW)) {
            System.out.println(k + ": " + uO.get(k));
        }

        List<Map.Entry<String, Long>> machineBreakdowns = Report.topMachineBreakdowns(this.wo, this.top);

        System.out.println("\n===== Ranking awarii (top " + this.top + ") =====");
        for (Map.Entry<String, Long> entry: machineBreakdowns) {
            System.out.println(entry.getKey() + " (" + entry.getValue() + ")");
        }

        List<Map.Entry<String, Long>> mttr = Report.mttr(wo, m);

        System.out.println("\n===== MTTR =====");
        for (Map.Entry<String, Long> entry: mttr) {
            long seconds = entry.getValue();
            System.out.println(entry.getKey() + " " + Duration.ofSeconds(seconds).toHoursPart() + " h " + Duration.ofSeconds(seconds).toMinutesPart() + " m");
        }

        Map<WorkOrder.Priority, Long> finishedOrdersOverSla = this.workOrderDataSource.finishedOrdersOverSlaLimit();

        System.out.println("\n===== Zakończone zlecenia ponad limit SLA =====");
        for (WorkOrder.Priority k: List.of(CRITICAL, HIGH, NORMAL, LOW)) {
            System.out.println(k + ": " + finishedOrdersOverSla.get(k));
        }

        List<String> unfinishedOrdersOverLimit = this.workOrderDataSource.unfinishedOrdersOverLimit();

        System.out.println("\n===== Nieskończone zlecenia poza limitem czasowym =====");
        System.out.println(unfinishedOrdersOverLimit);



        List<PriorityCount> unfinishedOrders = uO.entrySet().stream().map(n -> new PriorityCount(n.getKey().name(), n.getValue())).toList();
        List<MachineBreakdown> topMachineBreakdowns = machineBreakdowns.stream().map(n -> new MachineBreakdown(n.getKey(), n.getValue())).toList();
        List<HallMttr> convertedMttr = mttr.stream().map(n -> new HallMttr(n.getKey(), formatDuration(n.getValue()))).toList();

        ReportData rd = new ReportData(machineAmount, orderAmount, unfinishedOrders, topMachineBreakdowns, convertedMttr);

        this.xmlWriter.write(rd, this.xml);
        System.out.println("Zapisano do " + this.xml);



    }
    private String formatDuration(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        return duration.toHours() + " h "
                + duration.toMinutesPart() + " min";
    }
}
