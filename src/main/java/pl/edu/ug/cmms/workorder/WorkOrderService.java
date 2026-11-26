package pl.edu.ug.cmms.workorder;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.ug.cmms.machine.ImportedMachineInfo;
import pl.edu.ug.cmms.machine.Machine;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkOrderService {
    private final List<WorkOrder> workOrders;
    private long nextId;
    private final Clock clock;
    private final List<Machine> machines;

    public WorkOrderService(List<WorkOrder> workOrders, Clock clock, ImportedMachineInfo machines) {
        this.workOrders = new ArrayList<>(workOrders);
        this.nextId = workOrders.size() + 1;
        this.clock = clock;
        this.machines = machines.machines();
    }

    public List<WorkOrder> get(Optional<WorkOrder.WorkOrderStatus> status) {
        if (status.isEmpty()) {
            return this.workOrders;
        }

        return workOrders.stream().filter(w -> w.status().equals(status.get())).toList();
    }

    public WorkOrder create(WorkOrderRequest wor) {
        if (this.machines.stream().noneMatch(m -> m.id().equals(wor.machineId()))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        WorkOrder wo = new WorkOrder(String.format("WO-%04d", this.nextId), wor.machineId(), wor.type(), wor.priority(), WorkOrder.WorkOrderStatus.OPEN, LocalDateTime.now(this.clock).truncatedTo(ChronoUnit.SECONDS), null);
        workOrders.add(wo);
        this.nextId++;
        return wo;
    }

    public WorkOrder close(String id) {
        WorkOrder wo = workOrders.stream()
                .filter(w -> w.id().equals(id))
                .findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (wo.status().equals(WorkOrder.WorkOrderStatus.CANCELLED) || wo.status().equals(WorkOrder.WorkOrderStatus.DONE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        WorkOrder newWorkOrder = new WorkOrder(wo.id(), wo.machineId(), wo.type(), wo.priority(), WorkOrder.WorkOrderStatus.DONE, wo.reportedAt(), LocalDateTime.now(this.clock).truncatedTo(ChronoUnit.SECONDS));

        int index = workOrders.indexOf(wo);
        this.workOrders.set(index, newWorkOrder);

        return newWorkOrder;
    }



}
