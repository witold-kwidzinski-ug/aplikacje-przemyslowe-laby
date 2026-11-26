package pl.edu.ug.cmms.vendor;

import java.util.Optional;

public record VendorResponse(Optional<Vendor> vendor, VendorStatus status) {
    public enum VendorStatus {
        AVAILABLE,
        NOT_FOUND,
        UNAVAILABLE
    }
}
