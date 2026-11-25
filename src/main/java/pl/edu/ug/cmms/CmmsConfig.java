package pl.edu.ug.cmms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.time.Clock;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class CmmsConfig {

    @Bean
    public ImportedMachineInfo machines(MachineCsvImporter importer) throws IOException {
        ImportedMachineInfo imported = importer.importMachines(SampleData.machines());
        return imported;
    }
    @Bean
    public List<WorkOrder> workOrders() {
        return SampleData.workOrders();
    }

    @Bean
    public ReportXmlWriter xmlWriter() {
        return new ReportXmlWriter();
    }

    @Bean
    @Profile("!demo")
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    @Profile("demo")
    public Clock demoClock() {
        return Clock.fixed(LocalDateTime.of(2026, 9, 21, 6, 0)
                    .atZone(ZoneId.systemDefault())
                    .toInstant(),
                ZoneId.systemDefault());
    }


    @Bean public WorkOrderDataSource workOrderDataSource(Clock clock, List<WorkOrder> workOrders, @Value("${cmms.sla.critical}") Duration critical, @Value("${cmms.sla.high}") Duration high, @Value("${cmms.sla.normal}") Duration normal, @Value("${cmms.sla.low}") Duration low) {
        Map<WorkOrder.Priority, Duration> map = new HashMap<>();
        map.put(WorkOrder.Priority.CRITICAL, critical);
        map.put(WorkOrder.Priority.HIGH, high);
        map.put(WorkOrder.Priority.NORMAL, normal);
        map.put(WorkOrder.Priority.LOW, low);

        return new WorkOrderDataSource(workOrders, map, clock);
    }

    @Bean
    public ReportRunner reportRunner(ImportedMachineInfo machines, List<WorkOrder> workOrders, @Value("${cmms.report.top}") long top, @Value("${cmms.report.file}") String file, ReportXmlWriter xmlWriter, WorkOrderDataSource workOrderDataSource, VendorClient vendorClient) {
        return new ReportRunner(machines, workOrders, top, file, xmlWriter, workOrderDataSource, vendorClient);
    }

}
