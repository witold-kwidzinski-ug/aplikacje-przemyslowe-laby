package pl.edu.ug.cmms;

import java.util.List;

public record ImportedMachineInfo(List<Machine> machines, List<String> rejectedMachines, long accepted, long rejected) {}
