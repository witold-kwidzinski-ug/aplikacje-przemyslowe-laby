package pl.edu.ug.cmms;

import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(VendorClient.class)
public class TestVendorClient {

    @Autowired
    private VendorClient vendorClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void testResponse200() {

        mockServer.expect(requestTo("http://localhost:8090/api/maintenance-plans/CNC_LATHE"))
                .andRespond(withSuccess("""
                {"machineType": "CNC_LATHE", "inspectionIntervalDays": 30, "preventiveIntervalDays": 90}
                """, MediaType.APPLICATION_JSON));

        VendorResponse result = vendorClient.getVendor(Machine.MachineType.CNC_LATHE);

        assertAll("Response 200",
                () -> assertEquals(VendorResponse.VendorStatus.AVAILABLE, result.status()),
                () -> assertEquals(Machine.MachineType.CNC_LATHE, result.machineType()),
                () -> assertEquals(30, result.inspectionIntervalDays()),
                () -> assertEquals(90, result.preventiveIntervalDays())
                );

        mockServer.verify();

    }

}
