package pl.edu.ug.cmms.vendor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.edu.ug.cmms.machine.Machine;

import java.util.Optional;

@Component
public class VendorClient {
    private final RestClient restClient;

    VendorClient(RestClient.Builder builder, @Value("${cmms.vendor.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public VendorResponse getVendor(Machine.MachineType machineType) {
        try {
            Vendor v = this.restClient.get().uri("/api/maintenance-plans/{machineType}", machineType).retrieve().body(Vendor.class);
            if (v == null) {
                return new VendorResponse(Optional.empty(), VendorResponse.VendorStatus.UNAVAILABLE
                );
            }

            return new VendorResponse(Optional.of(v), VendorResponse.VendorStatus.AVAILABLE);
        } catch (HttpClientErrorException.NotFound e) {
            return new VendorResponse(Optional.empty(), VendorResponse.VendorStatus.NOT_FOUND);
        } catch (RestClientException e) {
            return new  VendorResponse(Optional.empty(), VendorResponse.VendorStatus.UNAVAILABLE);
        }
    }
}
