package pl.edu.ug.cmms;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;
import java.util.Map;

@JsonRootName("report")
public record XmlReportData(long machines,
                            long workOrders,
                            PriorityList unfinishedOrders,
                            MachineList topMachineBreakdowns,
                            Map<String, String> mttr) {

    public record PriorityList(@JacksonXmlElementWrapper(useWrapping = false) List<ReportData.PriorityCount> priority) {}

    public record MachineList(@JacksonXmlElementWrapper(useWrapping = false) List<ReportData.MachineBreakdown> machine) {}

}
