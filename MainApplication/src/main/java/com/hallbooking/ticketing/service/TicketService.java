package com.hallbooking.ticketing.service;

import com.hallbooking.enums.TicketStatus;
import com.hallbooking.ticketing.dto.TicketRequest;
import com.hallbooking.ticketing.entity.Ticket;

import java.util.List;

public interface TicketService {
    Ticket createTicket(TicketRequest request);
    List<Ticket> getUserTickets(Long userId);
    List<Ticket> getTicketsByStatus(TicketStatus status);
    Ticket updateTicketStatus(Long ticketId, TicketStatus status);
    Ticket getTicketById(Long ticketId);
}
