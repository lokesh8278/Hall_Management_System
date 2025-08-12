package com.hallbooking.ticketing.controller;


import com.hallbooking.enums.TicketStatus;
import com.hallbooking.ticketing.dto.TicketRequest;
import com.hallbooking.ticketing.entity.Ticket;
import com.hallbooking.ticketing.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/create")
    public ResponseEntity<Ticket> create(@RequestBody TicketRequest request) {
        return ResponseEntity.ok(ticketService.createTicket(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getUserTickets(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> getByStatus(@PathVariable TicketStatus status) {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<Ticket> updateStatus(@PathVariable Long ticketId, @RequestParam TicketStatus status) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(ticketId, status));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.getTicketById(ticketId));
    }
}

