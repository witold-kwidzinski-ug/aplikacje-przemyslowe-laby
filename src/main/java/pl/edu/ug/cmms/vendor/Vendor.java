package pl.edu.ug.cmms.vendor;

import pl.edu.ug.cmms.machine.Machine;

public record Vendor(Machine.MachineType machineType, long inspectionIntervalDays, long preventiveIntervalDays) {}
