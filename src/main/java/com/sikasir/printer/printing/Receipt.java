package com.sikasir.printer.printing;

import com.sikasir.printer.config.PrinterConfig;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

class Receipt implements Printable {
    PrinterConfig conf;
    String doc;

    public Receipt(String receipt, PrinterConfig conf) {
        this.conf = conf;
        this.doc = receipt;
    }

    public int print(Graphics g, PageFormat pageFormat, int page)
            throws PrinterException {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.setPaint(Color.black);

        int font_size = this.conf.getFontSize();
        g2d.setFont(new Font(this.conf.getFont(), 0, font_size));

        float x = (float) pageFormat.getImageableX();
        float y = (float) pageFormat.getImageableY();
        double line_height = font_size * 1.05D;
        StringTokenizer st = new StringTokenizer(this.doc, "#");
        String line = null;
        while (st.hasMoreTokens()) {
            line = st.nextToken();
            if (line.length() > 34) {
                byte[] decodedBytes = Base64.getDecoder().decode(line);
                InputStream inputStream = new ByteArrayInputStream(decodedBytes);
                BufferedImage image = null;
                try {
                    image = ImageIO.read(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                g2d.drawImage(image, -15, (int) y, null);
                y = (float) (y + image.getHeight());
            } else if (line.length() > 0) {
                g2d.drawString(line, x, y);
            } else {
                g2d.drawString(" ", x, y);
            }
            y = (float) (y + line_height);
        }

        return 0;
    }
}
