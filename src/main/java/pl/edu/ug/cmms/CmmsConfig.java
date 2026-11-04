package pl.edu.ug.cmms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CmmsConfig {

    @Bean
    public List<Machine> machines() {
        return SampleData.machines();
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
    public ReportRunner reportRunner(List<Machine> machines, List<WorkOrder> workOrders, @Value("${cmms.report.top}") long top, @Value("${cmms.report.file}") String file, ReportXmlWriter xmlWriter) {
        return new ReportRunner(machines, workOrders, top, file, xmlWriter);
    }

}
