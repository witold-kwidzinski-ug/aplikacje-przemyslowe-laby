package pl.edu.ug.cmms.report;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.edu.ug.cmms.report.ReportData.*;
import pl.edu.ug.cmms.machine.ImportedMachineInfo;
import pl.edu.ug.cmms.machine.Machine;
import pl.edu.ug.cmms.vendor.VendorClient;
import pl.edu.ug.cmms.vendor.VendorResponse;
import pl.edu.ug.cmms.workorder.WorkOrder;
import pl.edu.ug.cmms.workorder.WorkOrderDataSource;


import static pl.edu.ug.cmms.workorder.WorkOrder.Priority.*;

public class ReportRunner implements ApplicationRunner {
    private final ImportedMachineInfo m;
    private final List<WorkOrder> wo;
    private final long top;
    private final String xml;
    private final ReportXmlWriter xmlWriter;
    private final WorkOrderDataSource workOrderDataSource;
    private final VendorClient vendorClient;

    public ReportRunner(ImportedMachineInfo machines, List<WorkOrder> workOrders, long top, String xmlFile, ReportXmlWriter xmlWriter, WorkOrderDataSource workOrderDataSource, VendorClient vendorClient) {
        this.m = machines;
        this.wo = workOrders;
        this.top = top;
        this.xml = xmlFile;
        this.xmlWriter = xmlWriter;
        this.workOrderDataSource = workOrderDataSource;
        this.vendorClient = vendorClient;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (!m.rejectedMachines().isEmpty()) {
            System.out.println("Niektóre maszyny nie przeszły:");
            for (String e: m.rejectedMachines()) {
                System.out.println(e);
            }
        }
        System.out.println(String.format("Zaakceptowano: %d", m.accepted()));
        System.out.println(String.format("Odrzucono: %d", m.rejected()));

        long machineAmount = m.machines().size();
        long orderAmount = wo.size();

        System.out.println("\nIlosc maszyn: " + machineAmount);
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

        List<Map.Entry<String, Long>> mttr = Report.mttr(wo, m.machines());

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

        System.out.println("\n===== Plany maintanence =====");
        for (Machine.MachineType m : Machine.MachineType.values()) {
            VendorResponse v = this.vendorClient.getVendor(m);
            switch (v.status()) {
                case AVAILABLE:
                    System.out.println(String.format("%s: %d", m, v.vendor().orElseThrow().preventiveIntervalDays()));
                    break;
                case NOT_FOUND:
                    System.out.println(String.format("Brak w katalogu: %s", m));
                    break;
                case UNAVAILABLE:
                    System.out.println(String.format("Usługa niedostępna: %s", m));
            }
        }


        List<PriorityCount> unfinishedOrders = uO.entrySet().stream().map(n -> new PriorityCount(n.getKey().name(), n.getValue())).toList();
        List<MachineBreakdown> topMachineBreakdowns = machineBreakdowns.stream().map(n -> new MachineBreakdown(n.getKey(), n.getValue())).toList();
        List<HallMttr> convertedMttr = mttr.stream().map(n -> new HallMttr(n.getKey(), formatDuration(n.getValue()))).toList();

        ReportData rd = new ReportData(machineAmount, orderAmount, unfinishedOrders, topMachineBreakdowns, convertedMttr);

        this.xmlWriter.write(rd, this.xml);
        System.out.println("\nZapisano do " + this.xml);



    }
    private String formatDuration(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        return duration.toHours() + " h "
                + duration.toMinutesPart() + " min";
    }
}
