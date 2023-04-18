package com.sikasir.printer.printing;

import com.sikasir.printer.config.PrinterConfig;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;

public class PrintReceiptPaper {
    public static final int POINT_PER_INCH = 72;
    public static final double INCH_TO_MM = 25.399999999999999D;
    String receipt;

    public PrintReceiptPaper(String receipt, PrinterConfig conf) throws PrinterException {
        this.receipt = receipt;
        String hasil = "";

        PrinterJob printJob = PrinterJob.getPrinterJob();

        Book book = new Book();

        PageFormat documentPageFormat = new PageFormat();

        documentPageFormat.setOrientation(conf.getOrientation());
        documentPageFormat.setPaper(createPaper(conf));
        if (this.receipt.startsWith("[")) {
            JSONArray array = new JSONArray(this.receipt);

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("type").equals("string")) {
                    hasil = hasil.concat(object.getString("content")).concat("#");
                } else if (object.getString("type").equals("img/png")) {
                    hasil = hasil.concat(object.getString("content").substring(22)).concat("#");
                }
            }
        } else {
            hasil = this.receipt;
        }

        book.append(new Receipt(hasil, conf), documentPageFormat);

        printJob.setPageable(book);
        printJob.print();

    }

    public Paper createPaper(PrinterConfig conf) {
        StringTokenizer st = new StringTokenizer(this.receipt, "#");
        int totalLine = st.countTokens();
        Paper paper = new Paper();
        double width = 0.0D;
        double height = 0.0D;
        double left = conf.getLeft() / 25.399999999999999D * 72.0D;
        double right = conf.getRight() / 25.399999999999999D * 72.0D;
        double top = conf.getTop() / 25.399999999999999D * 72.0D;
        double bottom = conf.getBottom() / 25.399999999999999D * 72.0D;

        if (conf.getPaperType() == 1) {
            paper.setSize(594.0D, 841.0D);
            width = 594.0D - (left + right);
            height = 841.0D - (top + bottom);
        } else if (conf.getPaperType() == 2) {
            paper.setSize(420.0D, 594.0D);
            width = 420.0D - (left + right);
            height = 594.0D - (top + bottom);
        } else if (conf.getPaperType() == 3) {
            paper.setSize(612.0D, 1008.0D);
            width = 612.0D - (left + right);
            height = 1008.0D - (top + bottom);
        } else if (conf.getPaperType() == 4) {
            paper.setSize(612.0D, 792.0D);
            width = 612.0D - (left + right);
            height = 792.0D - (top + bottom);
        } else if (conf.getPaperType() == 5) {
            paper.setSize(594.0D, 420.0D);
            width = 594.0D - (left + right);
            height = 420.0D - (top + bottom);
        } else if (conf.getPaperType() == 6) {
            double temp = conf.getFontSize() * 1.05D * totalLine + top + bottom;
            paper.setSize(216.0D, temp);
            width = 216.0D - (left + right);
            height = temp - (top + bottom);
        }

        paper.setImageableArea(left, top, width, height);
        return paper;
    }
}