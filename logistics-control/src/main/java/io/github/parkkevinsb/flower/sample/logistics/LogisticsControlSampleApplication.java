package io.github.parkkevinsb.flower.sample.logistics;

import io.github.parkkevinsb.flower.sample.logistics.workflow.worker.WarehouseZoneWorker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogisticsControlSampleApplication implements ApplicationRunner {

    private final WarehouseZoneWorker zoneWorker;

    public LogisticsControlSampleApplication(WarehouseZoneWorker zoneWorker) {
        this.zoneWorker = zoneWorker;
    }

    public static void main(String[] args) {
        SpringApplication.run(LogisticsControlSampleApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        initializeWarehouseZoneFlows();
    }

    private void initializeWarehouseZoneFlows() {
        zoneWorker.submitZoneFlows();
    }
}
