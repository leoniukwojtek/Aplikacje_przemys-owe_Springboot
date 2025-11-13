package com.techcorp.employee.model;

import com.techcorp.employee.exception.InvalidDataException;
import java.util.Comparator;
import java.util.Objects;

public class Employee {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String companyName;
    private String jobTitle;
    private double salary;
    private EmploymentStatus status;
    private String photoFileName;

    // Konstruktor
    public Employee(String firstName, String lastName, String emailAddress,
                    String companyName, String jobTitle, double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.status = EmploymentStatus.ACTIVE; // default
    }

    // Walidacja
    public void validate() throws InvalidDataException {
        if (firstName == null || firstName.isBlank()) throw new InvalidDataException("Imię nie może być puste.");
        if (lastName == null || lastName.isBlank()) throw new InvalidDataException("Nazwisko nie może być puste.");
        if (emailAddress == null || emailAddress.isBlank()) throw new InvalidDataException("Email nie może być pusty.");
        if (companyName == null || companyName.isBlank()) throw new InvalidDataException("Nazwa firmy nie może być pusta.");
        if (jobTitle == null || jobTitle.isBlank()) throw new InvalidDataException("Stanowisko nie może być puste.");
    }

    // Gettery i settery
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmailAddress() { return emailAddress; }
    public String getCompanyName() { return companyName; }
    public String getJobTitle() { return jobTitle; }
    public double getSalary() { return salary; }
    public EmploymentStatus getStatus() { return status; }

    public void setSalary(double salary) { this.salary = salary; }
    public void setStatus(EmploymentStatus status) { this.status = status; }
    public String getPhotoFileName() { return photoFileName; }
    public void setPhotoFileName(String photoFileName) { this.photoFileName = photoFileName; }

    @Override
    public String toString() { return firstName + " " + lastName; }

    public String showFullDetails() {
        return "Name: " + firstName + " " + lastName + "\n" +
                "Email: " + emailAddress + "\n" +
                "Company: " + companyName + "\n" +
                "Job Title: " + jobTitle + "\n" +
                "Salary: " + salary + "\n" +
                "Status: " + status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(emailAddress, employee.emailAddress);
    }

    @Override
    public int hashCode() { return Objects.hash(emailAddress); }

    public static Comparator<Employee> getAlphabeticalComparator() {
        return Comparator.comparing(Employee::getLastName)
                .thenComparing(Employee::getFirstName);
    }

    public static Comparator<Employee> getSalaryComparator() {
        return Comparator.comparingDouble(Employee::getSalary);
    }
}
