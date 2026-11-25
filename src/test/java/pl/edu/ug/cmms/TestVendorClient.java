package pl.edu.ug.cmms;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;
import pl.edu.ug.cmms.machine.Machine;
import pl.edu.ug.cmms.vendor.Vendor;
import pl.edu.ug.cmms.vendor.VendorClient;
import pl.edu.ug.cmms.vendor.VendorResponse;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(VendorClient.class)
public class TestVendorClient {

    @Autowired
    private VendorClient vendorClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void testResponseAvailable() {

        mockServer.expect(requestTo("http://localhost:8090/api/maintenance-plans/CNC_LATHE"))
                .andRespond(withSuccess("""
                        {"machineType": "CNC_LATHE", "inspectionIntervalDays": 30, "preventiveIntervalDays": 90}
                        """, MediaType.APPLICATION_JSON));

        VendorResponse result = vendorClient.getVendor(Machine.MachineType.CNC_LATHE);

        Vendor v = result.vendor().orElseThrow();

        assertAll("Response 200",
                () -> assertEquals(VendorResponse.VendorStatus.AVAILABLE, result.status()),
                () -> assertEquals(Machine.MachineType.CNC_LATHE, v.machineType()),
                () -> assertEquals(30, v.inspectionIntervalDays()),
                () -> assertEquals(90, v.preventiveIntervalDays())
                );

        mockServer.verify();

    }

    @Test
    void testResponseNotFound() {
        mockServer.expect(requestTo("http://localhost:8090/api/maintenance-plans/COMPRESSOR"))
                .andRespond(withResourceNotFound());

        VendorResponse result = vendorClient.getVendor(Machine.MachineType.COMPRESSOR);

        assertAll("Response 404",
                () -> assertEquals(VendorResponse.VendorStatus.NOT_FOUND, result.status()),
                () -> assertTrue(result.vendor().isEmpty())
        );

        mockServer.verify();
    }

    @Test
    void testResponseUnavailable() {
        mockServer.expect(requestTo("http://localhost:8090/api/maintenance-plans/CONVEYOR"))
                .andRespond(withServerError());

        VendorResponse result = vendorClient.getVendor(Machine.MachineType.CONVEYOR);

        assertAll("Response 404",
                () -> assertEquals(VendorResponse.VendorStatus.UNAVAILABLE, result.status()),
                () -> assertTrue(result.vendor().isEmpty())
        );

        mockServer.verify();
    }

}
