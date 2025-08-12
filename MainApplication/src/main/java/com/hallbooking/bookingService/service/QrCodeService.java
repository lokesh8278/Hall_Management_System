package com.hallbooking.bookingService.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class QrCodeService {

    public byte[] generateQRCode(String text, int width, int height) throws Exception {
        BitMatrix bitMatrix = new MultiFormatWriter()
                .encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", stream);
        return stream.toByteArray();
    }
}
