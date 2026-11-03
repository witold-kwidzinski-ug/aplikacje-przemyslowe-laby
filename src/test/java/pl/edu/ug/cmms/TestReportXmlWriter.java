package pl.edu.ug.cmms;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestReportXmlWriter {

    @TempDir
    Path dir;

    @Test
    void testWriteThenReadXmlReport() throws IOException {
        ReportData rd = new ReportData(
                9,
                45,
                List.of(
                        new ReportData.PriorityCount("LOW", 1),
                        new ReportData.PriorityCount("NORMAL", 1),
                        new ReportData.PriorityCount("HIGH", 3),
                        new ReportData.PriorityCount("CRITICAL", 0)
                ),
                List.of(
                        new ReportData.MachineBreakdown("M-04", 5),
                        new ReportData.MachineBreakdown("M-08", 4),
                        new ReportData.MachineBreakdown("M-05", 3)
                ),
                List.of(
                        new ReportData.HallMttr("A", "13 h 51 min"),
                        new ReportData.HallMttr("B", "13 h 6 min"),
                        new ReportData.HallMttr("C", "16 h 45 min")
                )
        );

        Path file = dir.resolve("report.xml");

        ReportXmlWriter xmlWriter = new ReportXmlWriter();

        xmlWriter.write(rd, file.toString());

        assertTrue(Files.exists(file), "Plik XML powinien zostac utworzony");

        ReportData rdFromFile = xmlWriter.read(file.toString());

        assertNotNull(rdFromFile);

        assertEquals(rd, rdFromFile);


    }

}
