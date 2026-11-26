package pl.edu.ug.cmms.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.ug.cmms.workorder.WorkOrder;
import pl.edu.ug.cmms.workorder.WorkOrderRequest;
import pl.edu.ug.cmms.workorder.WorkOrderService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping
    public List<WorkOrder> getWorkOrders(@RequestParam(required = false) WorkOrder.WorkOrderStatus status) {
        return this.workOrderService.get(Optional.ofNullable(status));
    }

    @PostMapping
    public ResponseEntity<WorkOrder> createWorkOrder(@RequestBody WorkOrderRequest wor) {
        WorkOrder wo = this.workOrderService.create(wor);
        return ResponseEntity.created(URI.create("/api/work-orders/" + wo.id())).body(wo);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<WorkOrder> closeWorkOrder(@PathVariable String id) {
        WorkOrder wo = this.workOrderService.close(id);
        return ResponseEntity.ok(wo);

    }

}
