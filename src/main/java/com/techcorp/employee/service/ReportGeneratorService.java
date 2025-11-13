package com.techcorp.employee.service;

import com.techcorp.employee.model.CompanyStatistics;
import com.techcorp.employee.model.Employee;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

@Service
public class ReportGeneratorService {

    // ---------------- CSV ----------------
    public Resource generateCsv(List<Employee> employees) {
        StringBuilder sb = new StringBuilder();
        sb.append("First Name,Last Name,Email,Company,Job Title,Salary,Status\n");
        for (Employee e : employees) {
            sb.append(e.getFirstName()).append(",");
            sb.append(e.getLastName()).append(",");
            sb.append(e.getEmailAddress()).append(",");
            sb.append(e.getCompanyName()).append(",");
            sb.append(e.getJobTitle()).append(",");
            sb.append(e.getSalary()).append(",");
            sb.append(e.getStatus()).append("\n");
        }
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(bytes);
    }

    public Resource generateCsv(List<Employee> employees, String company) {
        List<Employee> filtered = employees.stream()
                .filter(e -> e.getCompanyName().equalsIgnoreCase(company))
                .toList();
        return generateCsv(filtered);
    }

    // ---------------- PDF ze statystyk firmy ----------------
    public Resource generatePdfStatistics(List<Employee> employees, String companyName) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Tytuł
            document.add(new Paragraph("Company Statistics: " + companyName).setBold().setFontSize(16));

            // Podsumowanie statystyk
            DoubleSummaryStatistics stats = employees.stream()
                    .mapToDouble(Employee::getSalary)
                    .summaryStatistics();

            CompanyStatistics companyStats = new CompanyStatistics(
                    stats.getCount(),
                    stats.getAverage(),
                    stats.getMax()
            );

            document.add(new Paragraph("Number of Employees: " + companyStats.getEmployeeCount()));
            document.add(new Paragraph("Average Salary: " + companyStats.getAverageSalary()));
            document.add(new Paragraph("Max Salary: " + companyStats.getMaxSalary()));

            // Tabela szczegółowa
            Table table = new Table(new float[]{3, 3, 4, 3, 3, 2});
            table.addHeaderCell("First Name");
            table.addHeaderCell("Last Name");
            table.addHeaderCell("Email");
            table.addHeaderCell("Job Title");
            table.addHeaderCell("Salary");
            table.addHeaderCell("Status");

            for (Employee e : employees) {
                table.addCell(e.getFirstName());
                table.addCell(e.getLastName());
                table.addCell(e.getEmailAddress());
                table.addCell(e.getJobTitle());
                table.addCell(String.valueOf(e.getSalary()));
                table.addCell(String.valueOf(e.getStatus()));
            }

            document.add(table);
            document.close();

            return new ByteArrayResource(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Błąd generowania PDF: " + e.getMessage(), e);
        }
    }
}
