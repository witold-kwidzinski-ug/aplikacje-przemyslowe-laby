package pl.edu.ug.cmms;

import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;


public record ReportData(long machines,

                         long workOrders,

                         List<PriorityCount> unfinishedOrders,

                         List<MachineBreakdown> topMachineBreakdowns,

                         List<HallMttr> mttr) {

    public record PriorityCount(@JacksonXmlProperty(isAttribute = true, localName = "name") String name, long count) {}

    public record MachineBreakdown(@JacksonXmlProperty(isAttribute = true, localName = "id") String id, @JacksonXmlProperty(isAttribute = true, localName = "breakdowns") long breakdowns) {}

    public record HallMttr(String name, String duration) {}
}

