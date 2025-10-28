package com.techcorp.employee.model;

public class CompanyStatistics {
    private final long count;
    private final double averageSalary;
    private final double maxSalary;

    public CompanyStatistics(long count, double averageSalary, double maxSalary) {
        this.count = count;
        this.averageSalary = averageSalary;
        this.maxSalary = maxSalary;
    }

    public long getEmployeeCount() {
        return count;
    }

    public double getAverageSalary() {
        return averageSalary;
    }

    public double getMaxSalary() {
        return maxSalary;
    }

    @Override
    public String toString() {
        return "Employees: " + count + ", Avg Salary: " + averageSalary + ", Max Salary: " + maxSalary;
    }
}
