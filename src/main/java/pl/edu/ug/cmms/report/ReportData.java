package pl.edu.ug.cmms.report;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

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

