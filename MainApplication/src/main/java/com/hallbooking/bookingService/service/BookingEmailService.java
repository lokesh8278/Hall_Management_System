package com.hallbooking.bookingService.service;

import com.hallbooking.bookingService.entity.Bookings;
import com.hallbooking.userservice.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BookingEmailService {

    private final JavaMailSender mailSender;
    private final QrCodeService qrCodeService;
    private final PdfGeneratorService pdfGeneratorService;

    public void sendBookingConfirmation(User user, Bookings booking, boolean attachPdf) throws Exception {
        String to = user.getEmail();
        String subject = "Your Booking is Confirmed — #" + booking.getId();

        String qrText = "Booking ID: " + booking.getId()
                + ", User: " + user.getId()
                + ", Status: " + booking.getStatus();

        byte[] qrPng = qrCodeService.generateQRCode(
                "Your Booking Is Completed Successfully\n" + qrText, 200, 200);

        byte[] pdfBytes = attachPdf ? pdfGeneratorService.generateBookingPDF(booking, qrPng) : null;

        String html = buildHtml(user, booking);

        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        helper.addInline("bookingQrCode", new ByteArrayResource(qrPng), "image/png");
        if (pdfBytes != null) {
            helper.addAttachment("booking-" + booking.getId() + ".pdf",
                    new ByteArrayResource(pdfBytes), "application/pdf");
        }

        mailSender.send(mime);
    }

    private String buildHtml(User user, Bookings booking) {
        DateTimeFormatter d = DateTimeFormatter.ofPattern("M/d/yyyy");
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a");
        NumberFormat inr = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        String checkIn  = formatDate(booking.getCheckin(), d);
        String checkOut = formatDate(booking.getCheckout(), d);
        String created  = formatDateTime(booking.getCreatedat(), dt);

        String hallName = booking.getHall() != null ? safe(booking.getHall().getName()) : "-";
        String roomName = booking.getRoom() != null ? safe(booking.getRoom().getName()) : "-";
        double hallPrice = 0.0;
        if (booking.getHall() != null) {
            hallPrice = booking.getHall().getBaseprice(); // primitive double, no null check
        }

        double roomPrice = 0.0;
        if (booking.getRoom() != null) {
            roomPrice = booking.getRoom().getPrice(); // primitive double
        }

        String total = inr.format(hallPrice + roomPrice);


        String appBase = "http://localhost:5173"; // TODO: externalize
        String viewUrl = appBase + "/my-bookings";
        String manageUrl = appBase + "/my-bookings";

        return """
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Booking Confirmation</title></head>
<body style="margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,sans-serif;">
  <table align="center" width="100%%" cellpadding="0" cellspacing="0"
         style="max-width:600px;background-color:#ffffff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.1);">
    <tr>
      <td style="padding:20px;background-color:#0d47a1;color:#ffffff;text-align:center;">
        <h2 style="margin:0;font-size:22px;">Booking Confirmation</h2>
      </td>
    </tr>

    <tr>
      <td style="padding:20px;">
        <h3 style="color:#1565c0;margin:0 0 12px 0;">Booking ID #%d</h3>
        <p style="margin:4px 0;"><strong>Name:</strong> %s</p>
        <p style="margin:4px 0;"><strong>Email:</strong> %s</p>
        <p style="margin:4px 0;"><strong>Mobile:</strong> %s</p>

        <p style="margin:8px 0;"><strong>Check-In:</strong> %s</p>
        <p style="margin:4px 0;"><strong>Check-Out:</strong> %s</p>
        <p style="margin:4px 0;"><strong>Hall:</strong> %s</p>
        <p style="margin:4px 0;"><strong>Room:</strong> %s</p>
        <p style="margin:4px 0;"><strong>Total Price:</strong> %s</p>
        <p style="margin:4px 0;"><strong>Created:</strong> %s</p>

        <div style="margin-top:18px;text-align:center;">
          <img src="cid:bookingQrCode" alt="QR Code"
               style="width:160px;height:160px;border:1px solid #ccc;border-radius:6px;">
          <p style="font-size:12px;color:#555;margin-top:6px;">Show this QR at the venue</p>
        </div>

        <div style="margin-top:18px;text-align:center;">
          <a href="%s" style="background-color:#1565c0;color:#ffffff;padding:10px 18px;border-radius:4px;
             text-decoration:none;font-weight:bold;display:inline-block;">📄 View in App</a>
          <a href="%s" style="background-color:#c62828;color:#ffffff;padding:10px 18px;border-radius:4px;
             text-decoration:none;font-weight:bold;display:inline-block;margin-left:10px;">❌ Manage Booking</a>
        </div>
      </td>
    </tr>
  </table>

  <p style="text-align:center;font-size:12px;color:#777;margin-top:12px;">
    © 2025 HallBooking. All rights reserved.
  </p>
</body>
</html>
""".formatted(
                booking.getId(),
                safe(user.getName()), safe(user.getEmail()), safe(user.getMobile()),
                checkIn, checkOut, hallName, roomName, total, created,
                viewUrl, manageUrl
        );
    }

    /* ---------- helpers ---------- */

    private static String formatDate(Date date, DateTimeFormatter fmt) {
        if (date == null) return "-";
        LocalDate ld = Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return fmt.format(ld);
        // If your field is already LocalDate/LocalDateTime, overload this method as needed.
    }

    private static String formatDateTime(Date date, DateTimeFormatter fmt) {
        if (date == null) return "-";
        LocalDateTime ldt = Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return fmt.format(ldt);
    }

    private static String safe(Object v) {
        return v == null ? "-" : v.toString();
    }
}
