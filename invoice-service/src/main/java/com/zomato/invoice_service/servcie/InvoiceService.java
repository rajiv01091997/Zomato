package com.zomato.invoice_service.servcie;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.zomato.invoice_service.dto.InvoiceDto;
import com.zomato.invoice_service.dto.ItemDto;
import com.zomato.invoice_service.dto.mail.MailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
public class InvoiceService {
    @Autowired
    private KafkaTemplate<String, MailDto> kafkaTemplate;
    @Value("${customer.acknowledgement.topic}")
    private String customerTopic;
    @Value("${restaurant.acknowledgement.topic}")
    private String restaurantTopic;
    @Value("${rider.acknowledgement.topic}")
    private String riderTopic;



    private static final BaseColor BRAND_GREEN = new BaseColor(46, 125, 50);
    private static final BaseColor LIGHT_GRAY = new BaseColor(245, 245, 245);

    public void generateInvoicePdf(String logoFilePath, InvoiceDto invoice) throws Exception {

        Document document = new Document(PageSize.A4, 35, 35, 35, 35);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, baos);
        document.open();

        // ============================
        // HEADER SECTION - LOGO CENTERED ON TOP
        // ============================
        Image logo = Image.getInstance(logoFilePath);
        logo.scaleToFit(120, 80);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);

        // App Name
        Font brandFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BRAND_GREEN);
        Paragraph brandName = new Paragraph("NutriMatrix", brandFont);
        brandName.setAlignment(Element.ALIGN_CENTER);
        brandName.setSpacingBefore(6f);
        brandName.setSpacingAfter(2f);
        document.add(brandName);

        // Tagline
        Font taglineFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY);
        Paragraph tagline = new Paragraph("Food at lightning speed", taglineFont);
        tagline.setAlignment(Element.ALIGN_CENTER);
        tagline.setSpacingAfter(12f);
        document.add(tagline);

        addSeparatorLine(document);

        // ============================
        // INVOICE TITLE & META INFO
        // ============================
        Font invoiceTitleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph invoiceTitle = new Paragraph("INVOICE", invoiceTitleFont);
        invoiceTitle.setAlignment(Element.ALIGN_CENTER);
        invoiceTitle.setSpacingAfter(12f);
        document.add(invoiceTitle);

        // Meta info in two columns
        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);

        Font metaLabelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font metaValueFont = new Font(Font.FontFamily.HELVETICA, 10);

        // Left column
        PdfPCell metaLeft = new PdfPCell();
        metaLeft.setBorder(Rectangle.NO_BORDER);
        metaLeft.setPadding(5);
        metaLeft.addElement(new Paragraph("Invoice Number:", metaLabelFont));
        metaLeft.addElement(new Paragraph(invoice.getInvoiceNumber(), metaValueFont));
        metaLeft.addElement(new Paragraph("Order ID:", metaLabelFont));
        metaLeft.addElement(new Paragraph(invoice.getOrderId().toString(), metaValueFont));

        // Right column
        PdfPCell metaRight = new PdfPCell();
        metaRight.setBorder(Rectangle.NO_BORDER);
        metaRight.setPadding(5);
        metaRight.addElement(new Paragraph("Date:", metaLabelFont));
        metaRight.addElement(new Paragraph(invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")), metaValueFont));

        metaTable.addCell(metaLeft);
        metaTable.addCell(metaRight);
        metaTable.setSpacingAfter(12f);
        document.add(metaTable);

        addSeparatorLine(document);

        // ============================
        // RESTAURANT & CUSTOMER INFO
        // ============================
        Font sectionLabelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BRAND_GREEN);
        Font infoFont = new Font(Font.FontFamily.HELVETICA, 9);

        PdfPTable partyTable = new PdfPTable(2);
        partyTable.setWidthPercentage(100);
        partyTable.setSpacingBefore(8f);
        partyTable.setSpacingAfter(12f);

        // Restaurant info
        PdfPCell restaurantCell = new PdfPCell();
        restaurantCell.setBorder(Rectangle.BOX);
        restaurantCell.setBorderColor(LIGHT_GRAY);
        restaurantCell.setPadding(8);
        restaurantCell.addElement(new Paragraph("RESTAURANT", sectionLabelFont));
        restaurantCell.addElement(new Paragraph(invoice.getRestaurantName(), infoFont));
        restaurantCell.addElement(new Paragraph("Email: " + invoice.getRestaurantEmail(), infoFont));
        restaurantCell.addElement(new Paragraph("Phone: " + invoice.getRestaurantContact(), infoFont));
        restaurantCell.addElement(new Paragraph(invoice.getRestaurantAddress(), infoFont));

        // Customer info
        PdfPCell customerCell = new PdfPCell();
        customerCell.setBorder(Rectangle.BOX);
        customerCell.setBorderColor(LIGHT_GRAY);
        customerCell.setPadding(8);
        customerCell.addElement(new Paragraph("CUSTOMER", sectionLabelFont));
        customerCell.addElement(new Paragraph(invoice.getCustomerName(), infoFont));
        customerCell.addElement(new Paragraph("Email: " + invoice.getCustomerEmail(), infoFont));
        customerCell.addElement(new Paragraph("Phone: " + invoice.getCustomerContact(), infoFont));
        customerCell.addElement(new Paragraph(invoice.getCustomerAddress(), infoFont));

        partyTable.addCell(restaurantCell);
        partyTable.addCell(customerCell);
        document.add(partyTable);

        addSeparatorLine(document);

        // ============================
        // ITEMS TABLE
        // ============================
        PdfPTable itemsTable = new PdfPTable(4);
        itemsTable.setWidths(new float[]{45, 15, 20, 20});
        itemsTable.setWidthPercentage(100);
        itemsTable.setSpacingBefore(8f);
        itemsTable.setSpacingAfter(8f);

        addTableHeader(itemsTable);

        boolean alternate = false;
        for (ItemDto item : invoice.getItems()) {
            BaseColor rowBg = alternate ? LIGHT_GRAY : BaseColor.WHITE;
            addItemRow(itemsTable, item, rowBg);
            alternate = !alternate;
        }

        document.add(itemsTable);

        addSeparatorLine(document);

        // ============================
        // BILL SUMMARY
        // ============================
        PdfPTable billTable = new PdfPTable(2);
        billTable.setWidthPercentage(50);
        billTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        billTable.setSpacingBefore(8f);

        Font billLabelFont = new Font(Font.FontFamily.HELVETICA, 10);
        Font billValueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        addBillRow(billTable, "Subtotal", invoice.getSubtotal(), billLabelFont, billValueFont);
        addBillRow(billTable, "Coupon Discount", -invoice.getCouponDiscount(), billLabelFont, billValueFont);
        addBillRow(billTable, "Delivery Charge", invoice.getDeliveryCharge(), billLabelFont, billValueFont);
        addBillRow(billTable, "Platform Fee", invoice.getPlatformFee(), billLabelFont, billValueFont);
        addBillRow(billTable, "GST (18%)", invoice.getGstAmount(), billLabelFont, billValueFont);

        // Total row
        Font totalLabelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BRAND_GREEN);
        Font totalValueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BRAND_GREEN);

        PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL", totalLabelFont));
        PdfPCell totalValue = new PdfPCell(new Phrase("₹" + String.format("%.2f", invoice.getTotalPayable()), totalValueFont));

        totalLabel.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        totalValue.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        totalLabel.setBorderWidth(2);
        totalValue.setBorderWidth(2);
        totalLabel.setBorderColor(BRAND_GREEN);
        totalValue.setBorderColor(BRAND_GREEN);

        totalLabel.setPadding(8);
        totalValue.setPadding(8);
        totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

        billTable.addCell(totalLabel);
        billTable.addCell(totalValue);

        document.add(billTable);

        // ============================
        // TERMS & CONDITIONS - ALL IN ONE BLOCK
        // ============================
        document.add(Chunk.NEWLINE);
        addSeparatorLine(document);

        Font termsHeadingFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BRAND_GREEN);
        Font termsTextFont = new Font(Font.FontFamily.HELVETICA, 9);

        // Create single paragraph with all terms together
        Paragraph termsBlock = new Paragraph();
        termsBlock.setSpacingBefore(8f);
        termsBlock.setSpacingAfter(8f);

        // Heading
        Paragraph termsHeading = new Paragraph("Terms & Conditions", termsHeadingFont);
        termsHeading.setSpacingAfter(4f);
        termsBlock.add(termsHeading);

        // Terms lines with minimal spacing between them
        Paragraph term1 = new Paragraph("• Once an order is delivered and accepted by the customer, refunds or returns are not applicable, except in cases of incorrect or damaged items as per our policy.", termsTextFont);
        term1.setSpacingAfter(1f);
        termsBlock.add(term1);

        Paragraph term2 = new Paragraph("• Delivery times displayed in the app are only estimates and may vary due to traffic, weather, or restaurant preparation delays.", termsTextFont);
        term2.setSpacingAfter(1f);
        termsBlock.add(term2);

        Paragraph term3 = new Paragraph("• Customers must ensure successful payment completion." +
                "Nutrimatrix is not responsible for delays or failures caused by banks, UPI apps, or payment gateways.", termsTextFont);
        term3.setSpacingAfter(0f);
        termsBlock.add(term3);

        Paragraph term4 = new Paragraph("• Food preparation and quality are the responsibility of the listed restaurants." +
                "NutriMatrix ensures only safe delivery and order handling.", termsTextFont);
        term4.setSpacingAfter(0f);
        termsBlock.add(term4);

        Paragraph term5 = new Paragraph("• For any order-related concerns, customers must contact NutriMatrix Support within 30 minutes of delivery for resolution.", termsTextFont);
        term5.setSpacingAfter(0f);
        termsBlock.add(term5);

        document.add(termsBlock);

        addSeparatorLine(document);

        // ============================
        // SUPPORT & CLOSING
        // ============================
        Font supportHeadingFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font supportFont = new Font(Font.FontFamily.HELVETICA, 9);

        Paragraph supportHeading = new Paragraph("Need Support?", supportHeadingFont);
        supportHeading.setAlignment(Element.ALIGN_CENTER);
        supportHeading.setSpacingBefore(8f);
        supportHeading.setSpacingAfter(4f);
        document.add(supportHeading);

        Paragraph supportInfo = new Paragraph("Email: nutrimatrix@zohomail.in | Phone: 6398109021", supportFont);
        supportInfo.setAlignment(Element.ALIGN_CENTER);
        supportInfo.setSpacingAfter(12f);
        document.add(supportInfo);

        Font thanksFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BRAND_GREEN);
        Paragraph thanks = new Paragraph("Thank you for choosing NutriMatrix!", thanksFont);
        thanks.setAlignment(Element.ALIGN_CENTER);
        thanks.setSpacingAfter(6f);
        document.add(thanks);

        Font disclaimerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
        Paragraph disclaimer = new Paragraph("This is an electronically generated invoice and does not require a physical signature.", disclaimerFont);
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        document.add(disclaimer);

        document.close();
        System.out.println("Invoice generated");
        //call method to send invoice
        mailOrderAcknowledement(baos.toByteArray(),invoice);
        //return baos.toByteArray();
    }

    // ===============================================
    // Utility Functions
    // ===============================================

    private void addSeparatorLine(Document document) throws DocumentException {
        Paragraph sep = new Paragraph(" ");
        sep.setSpacingAfter(6f);
        LineSeparator ls = new LineSeparator(1, 100, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, 0);
        sep.add(ls);
        document.add(sep);
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);

        Stream.of("Item Name", "Qty", "Unit Price", "Subtotal")
                .forEach(col -> {
                    PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
                    cell.setBackgroundColor(new BaseColor(46, 125, 50));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(8);
                    table.addCell(cell);
                });
    }

    private void addItemRow(PdfPTable table, ItemDto item, BaseColor bg) {
        Font itemFont = new Font(Font.FontFamily.HELVETICA, 9);

        PdfPCell nameCell = new PdfPCell(new Phrase(item.getName(), itemFont));
        nameCell.setBackgroundColor(bg);
        nameCell.setPadding(6);
        table.addCell(nameCell);

        PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), itemFont));
        qtyCell.setBackgroundColor(bg);
        qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qtyCell.setPadding(6);
        table.addCell(qtyCell);

        PdfPCell priceCell = new PdfPCell(new Phrase("₹" + String.format("%.2f", item.getUnitPrice()), itemFont));
        priceCell.setBackgroundColor(bg);
        priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        priceCell.setPadding(6);
        table.addCell(priceCell);

        PdfPCell subtotalCell = new PdfPCell(new Phrase("₹" + String.format("%.2f", item.getSubtotal()), itemFont));
        subtotalCell.setBackgroundColor(bg);
        subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        subtotalCell.setPadding(6);
        table.addCell(subtotalCell);
    }

    private void addBillRow(PdfPTable table, String label, double amount, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);

        PdfPCell valueCell = new PdfPCell(new Phrase("₹" + String.format("%.2f", amount), valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    public void mailOrderAcknowledement(byte[] attachment,InvoiceDto invoice)
    {   //inform customer about order
         MailDto dto=MailDto.builder()
                 .orderId(invoice.getOrderId())
                 .email(invoice.getCustomerEmail())
                 .attachment(attachment)
                 .attachmentName(invoice.getCustomerName()+"_"+invoice.getOrderId()+".pdf")
                 .userName(invoice.getCustomerName())
                 .creationTime(invoice.getInvoiceDate())
                 .build();
         kafkaTemplate.send(customerTopic,invoice.getCustomerEmail(),dto);
         System.out.println("Invoice sent to customer");

         //--------------------------------
        //inform restaurant about order
        MailDto dto2=MailDto.builder()
                .orderId(invoice.getOrderId())
                .email(invoice.getRestaurantEmail())
                .attachment(attachment)
                .attachmentName(invoice.getCustomerName()+"_"+invoice.getOrderId()+".pdf")
                .restaurantName(invoice.getRestaurantName())
                .creationTime(invoice.getInvoiceDate())
                .build();

         kafkaTemplate.send(restaurantTopic,invoice.getRestaurantEmail(),dto2);
        System.out.println("Notification data sent to topic restaurant of kafka");

        //----------------------------------
        //inform rider about order
        MailDto dto3=MailDto.builder()
                .orderId(invoice.getOrderId())
                .email(invoice.getRiderEmail())
                .restaurantName(invoice.getRestaurantName())
                .creationTime(invoice.getInvoiceDate())
                .userName(invoice.getRiderName())
                .extraInfo(invoice.getRiderOtp())
                .attachmentName(invoice.getCustomerName()+"_"+invoice.getOrderId()+".pdf")
                .attachment(attachment)
                .build();
        kafkaTemplate.send(riderTopic,invoice.getRiderEmail(),dto3);
        System.out.println("Notification data sent to rider topic of kafka");
    }
}
