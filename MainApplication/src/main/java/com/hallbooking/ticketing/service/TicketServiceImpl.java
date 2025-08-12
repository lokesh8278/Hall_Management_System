package com.hallbooking.ticketing.service;

import com.hallbooking.enums.NotificationType;
import com.hallbooking.enums.TicketStatus;
import com.hallbooking.notification.dto.NotificationRequest;
import com.hallbooking.notification.service.NotificationService;
import com.hallbooking.ticketing.dto.TicketRequest;
import com.hallbooking.ticketing.entity.Ticket;
import com.hallbooking.ticketing.repository.TicketRepository;
import com.hallbooking.ticketing.service.TicketService;
import com.hallbooking.userservice.entity.User;
import com.hallbooking.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public Ticket createTicket(TicketRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = Ticket.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(TicketStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        Ticket saved = ticketRepository.save(ticket);

        sendTicketNotification(saved, "🎫 Ticket Raised", "Your ticket #" + saved.getId() + " has been created successfully.");

        return saved;
    }

    @Override
    public List<Ticket> getUserTickets(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    @Override
    public List<Ticket> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    @Override
    public Ticket updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updated = ticketRepository.save(ticket);
        sendTicketNotification(updated, "📢 Ticket Status Updated", "Your ticket #" + ticket.getId() + " is now " + newStatus.name());

        return updated;
    }

    @Override
    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    private void sendTicketNotification(Ticket ticket, String subject, String message) {
        NotificationRequest email = NotificationRequest.builder()
                .recipient(ticket.getEmail())
                .subject(subject)
                .message(message)
                .type(NotificationType.EMAIL)
                .build();

        NotificationRequest sms = NotificationRequest.builder()
                .recipient(ticket.getMobile())
                .message(message)
                .type(NotificationType.SMS)
                .build();

        notificationService.sendNotification(email);
        notificationService.sendNotification(sms);
    }
}
