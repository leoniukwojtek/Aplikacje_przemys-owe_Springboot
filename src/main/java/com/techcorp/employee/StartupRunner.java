package com.techcorp.employee;

import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.ImportSummary;
import com.techcorp.employee.model.CompanyStatistics;
import com.techcorp.employee.service.EmployeeService;
import com.techcorp.employee.service.ImportService;
import com.techcorp.employee.service.ApiService;
import com.techcorp.employee.exception.ApiException;
import com.techcorp.employee.exception.InvalidDataException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class StartupRunner implements CommandLineRunner {

    private final EmployeeService employeeService;
    private final ImportService importService;
    private final ApiService apiService;

    public StartupRunner(EmployeeService employeeService,
                         ImportService importService,
                         ApiService apiService) {
        this.employeeService = employeeService;
        this.importService = importService;
        this.apiService = apiService;
    }

    @Override
    public void run(String... args) {

        //  Wyświetlenie pracowników dodanych z beanów XML (już dodani w EmployeeService)
        System.out.println("\n📌 Pracownicy załadowani z beanów XML:");
        employeeService.printAllEmployees();

        // Import danych z pliku CSV
        System.out.println("\n📌 Pracownicy załadowani pliku CSV:");
        ImportSummary summary = importService.importFromCsv();

        System.out.println("\n📥 Import z pliku CSV zakończony:");
        System.out.println("Zaimportowano pracowników: " + summary.getImportedCount());
        if (!summary.getErrors().isEmpty()) {
            System.out.println("Błędy podczas importu:");
            summary.getErrors().forEach(System.out::println);
        }

        // Pobranie danych z REST API
        try {
            List<Employee> apiEmployees = apiService.fetchEmployeesFromApi();

            System.out.println("\n🌐 Import z API zakończony. Dodano pracowników: " + apiEmployees.size());
            System.out.println("Pobrani pracownicy:");
            apiEmployees.forEach(emp -> System.out.println(emp.showFullDetails() + "\n"));

            // Dodanie do EmployeeService
            for (Employee e : apiEmployees) {
                try {
                    employeeService.addEmployee(e);
                } catch (InvalidDataException ex) {
                    System.out.println("❌ Nie dodano pracownika z API: " + ex.getMessage());
                }
            }

        } catch (ApiException e) {
            System.out.println("❌ Błąd podczas pobierania danych z API: " + e.getMessage());
        }


        //  Statystyki firmowe
        Map<String, CompanyStatistics> statsMap = employeeService.getCompanyStatistics();
        System.out.println("\n📊 Statystyki firmowe:");
        statsMap.forEach((company, stats) -> {
            System.out.println("Firma: " + company);
            System.out.println(stats.toString());
            System.out.println();
        });
    }
}
