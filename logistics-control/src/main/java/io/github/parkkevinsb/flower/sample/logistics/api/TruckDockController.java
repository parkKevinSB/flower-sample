package io.github.parkkevinsb.flower.sample.logistics.api;

import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseConveyor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/truck-dock")
public final class TruckDockController {

    private final WarehouseConveyor conveyor;

    public TruckDockController(WarehouseConveyor conveyor) {
        this.conveyor = conveyor;
    }

    @GetMapping
    public TruckDockResponse get() {
        return TruckDockResponse.of(conveyor.truckDock());
    }

    @PostMapping("/depart")
    public ResponseEntity<TruckDockResponse> depart() {
        try {
            return ResponseEntity.accepted()
                    .body(TruckDockResponse.of(conveyor.departTruck()));
        } catch (IllegalStateException error) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, error.getMessage(), error);
        }
    }
}
