package pl.edu.ug.cmms.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.ug.cmms.machine.ImportedMachineInfo;
import pl.edu.ug.cmms.machine.Machine;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
public class MachineController {

    private final List<Machine> machines;

    public MachineController(ImportedMachineInfo importedMachineInfo) {
        this.machines = importedMachineInfo.machines();
    }


    @GetMapping
    public List<Machine> machines(@RequestParam(required = false) String hall) {
        if (hall != null) {
            return this.machines.stream().filter(m -> m.hall().equals(hall)).toList();
        }
        return this.machines;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Machine> machine(@PathVariable String id) {
        return ResponseEntity.of(this.machines.stream().filter(m -> m.id().equals(id)).findFirst());
    }

}
