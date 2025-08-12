package com.hallbooking.bookingService.service;

import com.hallbooking.bookingService.entity.Bookings;
import com.hallbooking.userservice.dto.response.UserProfileResponse;
import com.hallbooking.userservice.service.serviceimpl.UserServiceImpl;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    @Autowired
    UserServiceImpl userService;

    public byte[] generateBookingPDF(Bookings booking, byte[] qrImageBytes) throws Exception {

        UserProfileResponse response = userService.getProfile();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("🎫 Booking Confirmation"));
        doc.add(new Paragraph("Booking ID: " + booking.getId()));
        doc.add(new Paragraph("Name: " +response.getName()));
        doc.add(new Paragraph("Email: " +response.getEmail()));
        doc.add(new Paragraph("MobileNumber: " +response.getMobileNumber()));
        doc.add(new Paragraph("Status: " + booking.getStatus()));
        doc.add(new Paragraph("Created: " + booking.getCheckin()));

        Image qrImage = new Image(ImageDataFactory.create(qrImageBytes));
        qrImage.setWidth(150);
        doc.add(qrImage);

        doc.close();
        return out.toByteArray();
    }
}
