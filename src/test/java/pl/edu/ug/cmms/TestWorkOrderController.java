package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.ug.cmms.controllers.WorkOrderController;
import pl.edu.ug.cmms.workorder.WorkOrder;
import pl.edu.ug.cmms.workorder.WorkOrderRequest;
import pl.edu.ug.cmms.workorder.WorkOrderService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkOrderController.class)
public class TestWorkOrderController {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private WorkOrderService workOrderService;

    @Test
    public void testAddWorkOrder() throws Exception {

        WorkOrder wo = new WorkOrder(
                "WO-0001",
                "M-06",
                WorkOrder.WorkOrderType.BREAKDOWN,
                WorkOrder.Priority.HIGH,
                WorkOrder.WorkOrderStatus.OPEN,
                LocalDateTime.parse("2026-09-21T06:00:00"),
                null
        );

        when(workOrderService.create(any(WorkOrderRequest.class))).thenReturn(wo);

        String request = """
                {
                    "machineId": "M-06",
                    "type": "BREAKDOWN",
                    "priority": "HIGH"
                }
                """;

        mvc.perform(post("/api/work-orders").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/work-orders/WO-0001"));

    }

    @Test
    public void testCloseWorkOrderConflict() throws Exception {
        when(workOrderService.close("WO-0001")).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        mvc.perform(post("/api/work-orders/WO-0001/close")).andExpect(status().isConflict());
    }

}
