package pl.edu.ug.cmms;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import static pl.edu.ug.cmms.Machine.MachineType.*;


public class TestMachine {

    @Test
    void createMachineValid() {
        Machine m = new Machine("1", "maszyna", MILLING_MACHINE, "A", LocalDate.parse("2026-10-23"));
        assertEquals("1", m.id());
        assertEquals("maszyna", m.name());
        assertEquals(MILLING_MACHINE, m.type());
        assertEquals("A", m.hall());
        assertEquals(LocalDate.parse("2026-10-23"), m.createdAt());
    }

    @Test
    void createMachineWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> new Machine(null, "maszyna", MILLING_MACHINE, "A", LocalDate.parse("2026-10-23")));
    }


}
