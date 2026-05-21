package io.github.parkkevinsb.flower.sample.durable.domain;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbc;

    public OrderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public OrderRecord createOrder(String orderId) {
        OrderRecord existing = find(orderId);
        if (existing != null) {
            return existing;
        }
        jdbc.update(
                "INSERT INTO sample_order "
                        + "(order_id, status, payment_received, inventory_reserved, shipped, completed, created_at, updated_at) "
                        + "VALUES (?, ?, false, false, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                orderId,
                OrderStatus.SUBMITTED.name());
        addAudit("ORDER_SUBMITTED", orderId + " submitted");
        return find(orderId);
    }

    public OrderRecord find(String orderId) {
        List<OrderRecord> out = jdbc.query(
                "SELECT order_id, status, payment_received, inventory_reserved, shipped, completed, created_at, updated_at "
                        + "FROM sample_order WHERE order_id = ?",
                (rs, rowNum) -> new OrderRecord(
                        rs.getString("order_id"),
                        OrderStatus.valueOf(rs.getString("status")),
                        rs.getBoolean("payment_received"),
                        rs.getBoolean("inventory_reserved"),
                        rs.getBoolean("shipped"),
                        rs.getBoolean("completed"),
                        toInstant(rs.getTimestamp("created_at")),
                        toInstant(rs.getTimestamp("updated_at"))),
                orderId);
        return out.isEmpty() ? null : out.get(0);
    }

    public List<OrderRecord> findAll() {
        return jdbc.query(
                "SELECT order_id, status, payment_received, inventory_reserved, shipped, completed, created_at, updated_at "
                        + "FROM sample_order ORDER BY updated_at DESC, order_id ASC",
                (rs, rowNum) -> new OrderRecord(
                        rs.getString("order_id"),
                        OrderStatus.valueOf(rs.getString("status")),
                        rs.getBoolean("payment_received"),
                        rs.getBoolean("inventory_reserved"),
                        rs.getBoolean("shipped"),
                        rs.getBoolean("completed"),
                        toInstant(rs.getTimestamp("created_at")),
                        toInstant(rs.getTimestamp("updated_at"))));
    }

    public void validate(String orderId) {
        OrderRecord order = require(orderId);
        if (order.getStatus() == OrderStatus.SUBMITTED) {
            updateStatus(orderId, OrderStatus.VALIDATED);
            addAudit("ORDER_VALIDATED", orderId + " validated");
        }
    }

    public void waitForPayment(String orderId) {
        OrderRecord order = require(orderId);
        if (order.getStatus() == OrderStatus.VALIDATED) {
            updateStatus(orderId, OrderStatus.WAITING_PAYMENT);
            addAudit("WAITING_PAYMENT", orderId + " waiting for payment");
        }
    }

    public void markPaid(String orderId) {
        require(orderId);
        jdbc.update(
                "UPDATE sample_order "
                        + "SET payment_received = true, status = ?, updated_at = CURRENT_TIMESTAMP "
                        + "WHERE order_id = ? AND status IN (?, ?)",
                OrderStatus.PAID.name(),
                orderId,
                OrderStatus.VALIDATED.name(),
                OrderStatus.WAITING_PAYMENT.name());
        addAudit("PAYMENT_RECEIVED", orderId + " marked paid");
    }

    public boolean isPaid(String orderId) {
        OrderRecord order = require(orderId);
        return order.isPaymentReceived();
    }

    public void reserveInventory(String orderId) {
        OrderRecord order = require(orderId);
        if (order.getStatus() == OrderStatus.PAID) {
            jdbc.update(
                    "UPDATE sample_order "
                            + "SET inventory_reserved = true, status = ?, updated_at = CURRENT_TIMESTAMP "
                            + "WHERE order_id = ?",
                    OrderStatus.INVENTORY_RESERVED.name(),
                    orderId);
            addAudit("INVENTORY_RESERVED", orderId + " inventory reserved");
        }
    }

    public void ship(String orderId) {
        OrderRecord order = require(orderId);
        if (order.getStatus() == OrderStatus.INVENTORY_RESERVED) {
            jdbc.update(
                    "UPDATE sample_order "
                            + "SET shipped = true, status = ?, updated_at = CURRENT_TIMESTAMP "
                            + "WHERE order_id = ?",
                    OrderStatus.SHIPPED.name(),
                    orderId);
            addAudit("ORDER_SHIPPED", orderId + " shipped");
        }
    }

    public void complete(String orderId) {
        OrderRecord order = require(orderId);
        if (order.getStatus() == OrderStatus.SHIPPED) {
            jdbc.update(
                    "UPDATE sample_order "
                            + "SET completed = true, status = ?, updated_at = CURRENT_TIMESTAMP "
                            + "WHERE order_id = ?",
                    OrderStatus.COMPLETED.name(),
                    orderId);
            addAudit("ORDER_COMPLETED", orderId + " completed");
        }
    }

    public void addAudit(String auditType, String message) {
        jdbc.update(
                "INSERT INTO sample_audit_event (audit_type, message, created_at) "
                        + "VALUES (?, ?, CURRENT_TIMESTAMP)",
                auditType,
                message);
    }

    public List<AuditEvent> auditEvents() {
        return jdbc.query(
                "SELECT id, audit_type, message, created_at "
                        + "FROM sample_audit_event ORDER BY id DESC LIMIT 80",
                (rs, rowNum) -> new AuditEvent(
                        rs.getLong("id"),
                        rs.getString("audit_type"),
                        rs.getString("message"),
                        toInstant(rs.getTimestamp("created_at"))));
    }

    public void resetAll() {
        jdbc.update("DELETE FROM flower_flow_checkpoint");
        jdbc.update("DELETE FROM sample_audit_event");
        jdbc.update("DELETE FROM sample_order");
    }

    private OrderRecord require(String orderId) {
        OrderRecord order = find(orderId);
        if (order == null) {
            throw new IllegalArgumentException("unknown order: " + orderId);
        }
        return order;
    }

    private void updateStatus(String orderId, OrderStatus status) {
        jdbc.update(
                "UPDATE sample_order SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?",
                status.name(),
                orderId);
    }

    private static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
