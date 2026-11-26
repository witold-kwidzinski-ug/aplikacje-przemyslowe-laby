package pl.edu.ug.cmms.workorder;

public record WorkOrderRequest(String machineId, WorkOrder.WorkOrderType type, WorkOrder.Priority priority) {
}
