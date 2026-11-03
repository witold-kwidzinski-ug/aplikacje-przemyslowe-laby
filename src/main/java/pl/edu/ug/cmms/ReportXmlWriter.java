package pl.edu.ug.cmms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tools.jackson.dataformat.xml.XmlMapper;

public class ReportXmlWriter {

    private final XmlMapper xmlMapper = XmlMapper.builder()
            .enable(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT)
            .build();

    private ReportData fromXml(XmlReportData xrd) {

        List<ReportData.HallMttr> mttr = xrd.mttr().entrySet().stream().map(n -> new ReportData.HallMttr(n.getKey(), n.getValue())).toList();

        return new ReportData(xrd.machines(), xrd.workOrders(), xrd.unfinishedOrders().priority(), xrd.topMachineBreakdowns().machine(), mttr);

    }

    private XmlReportData toXml(ReportData rd) {
        Map<String, String> mttr = new LinkedHashMap<>();

        rd.mttr().stream().forEach(mt -> mttr.put(mt.name(), mt.duration()));

        return new XmlReportData(rd.machines(), rd.workOrders(), new XmlReportData.PriorityList(rd.unfinishedOrders()), new XmlReportData.MachineList(rd.topMachineBreakdowns()), mttr);

    }

    public void write(ReportData rd, String file) throws IOException {
        Path path = Path.of(file).toAbsolutePath();

        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        XmlReportData xml = toXml(rd);

        xmlMapper.writeValue(path.toFile(), xml);

    }

    public ReportData read(String file) throws IOException {
        XmlReportData xml = xmlMapper.readValue(Path.of(file).toFile(), XmlReportData.class);

        return fromXml(xml);
    }

}
