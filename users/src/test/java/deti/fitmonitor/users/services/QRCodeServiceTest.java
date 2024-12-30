package deti.fitmonitor.users.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

class QRCodeServiceTest {

    private QRCodeService qrCodeService;

    @BeforeEach
    void setUp() {
        qrCodeService = new QRCodeService();
    }

    @Test
    void testGenerateQRCode_ValidContent() throws Exception {
        String content = "https://example.com";
        int width = 200;
        int height = 200;

        byte[] qrCodeBytes = qrCodeService.generateQRCode(content, width, height);

        assertNotNull(qrCodeBytes, "Generated QR Code bytes should not be null");
        assertTrue(qrCodeBytes.length > 0, "Generated QR Code should have content");

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(qrCodeBytes));
        assertNotNull(image, "Generated QR Code bytes should represent a valid image");
        assertEquals(width, image.getWidth(), "QR Code image width should match the specified width");
        assertEquals(height, image.getHeight(), "QR Code image height should match the specified height");
    }
}