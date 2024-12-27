package com.psychiatryclinic.core.utilities.invoice;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.psychiatryclinic.entities.Invoice;
import com.psychiatryclinic.entities.Patient;
import com.psychiatryclinic.business.abstracts.PatientService;
import com.psychiatryclinic.business.responses.patient.GetPatientResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class InvoiceGenerator {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceGenerator.class);
    private static Font TITLE_FONT;
    private static Font HEADER_FONT;
    private static Font NORMAL_FONT;
    private final PatientService patientService;
    
    @PostConstruct
    public void init() {
        try {
            // Varsayılan fontları kullan
            TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
            HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
            NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
            
            logger.info("PDF fontları başarıyla yüklendi");
        } catch (Exception e) {
            logger.error("Font yüklenirken hata oluştu", e);
            throw new RuntimeException("Font yükleme hatası", e);
        }
    }
    
    public byte[] generatePdf(Invoice invoice) {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            
            document.open();
            addMetaData(document);
            addContent(document, invoice);
            document.close();
            
            logger.info("PDF fatura oluşturuldu. Fatura ID: {}", invoice.getId());
            return out.toByteArray();
            
        } catch (Exception e) {
            logger.error("PDF fatura oluşturulurken hata oluştu. Fatura ID: {}", invoice.getId(), e);
            throw new RuntimeException("PDF oluşturma hatası", e);
        }
    }
    
    private void addMetaData(Document document) {
        document.addTitle("Psikiyatri Kliniği Fatura");
        document.addSubject("Fatura");
        document.addKeywords("Psikiyatri, Klinik, Fatura");
        document.addAuthor("Psikiyatri Kliniği");
        document.addCreator("Psikiyatri Kliniği Fatura Sistemi");
    }
    
    private void addContent(Document document, Invoice invoice) throws DocumentException {
        // Başlık
        Paragraph title = new Paragraph("PSİKİYATRİ KLİNİĞİ FATURA", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);
        
        // Fatura Detayları
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        // Hasta bilgilerini servisten al
        GetPatientResponse patient = patientService.getById(invoice.getPatientId());
        String patientFullName = patient.getUser().getFirstName() + " " + patient.getUser().getLastName();
        
        addTableRow(table, "Fatura No:", invoice.getId());
        addTableRow(table, "Tarih:", formatDate(invoice.getCreatedAt()));
        addTableRow(table, "Hasta Adı:", patientFullName);
        addTableRow(table, "Tutar:", formatAmount(invoice.getAmount()));
        addTableRow(table, "Son Ödeme Tarihi:", formatDate(invoice.getDueDate()));
        addTableRow(table, "Açıklama:", invoice.getDescription());
        addTableRow(table, "Ödeme Durumu:", invoice.isPaid() ? "Ödendi" : "Ödenmedi");
        
        document.add(table);
        
        // Alt Bilgi
        document.add(Chunk.NEWLINE);
        Paragraph footer = new Paragraph("Bu fatura elektronik olarak oluşturulmuştur.", NORMAL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
    
    private void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, HEADER_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private String formatDate(java.time.LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    private String formatAmount(java.math.BigDecimal amount) {
        return String.format("%,.2f TL", amount);
    }
} 