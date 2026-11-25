package pl.edu.ug.cmms;

public record Vendor(Machine.MachineType machineType, long inspectionIntervalDays, long preventiveIntervalDays) {}
