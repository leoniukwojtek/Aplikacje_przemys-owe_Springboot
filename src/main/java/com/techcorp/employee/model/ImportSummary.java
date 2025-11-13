package com.techcorp.employee.model;

import java.util.Collections;
import java.util.List;

public class ImportSummary {
    private final int importedCount;
    private final List<String> errors;
    private final List<Employee> importedEmployees; // nowa lista zaimportowanych pracowników

    public ImportSummary(int importedCount, List<String> errors, List<Employee> importedEmployees) {
        this.importedCount = importedCount;
        this.errors = errors != null ? errors : Collections.emptyList();
        this.importedEmployees = importedEmployees != null ? importedEmployees : Collections.emptyList();
    }

    public int getImportedCount() {
        return importedCount;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<Employee> getImportedEmployees() {
        return importedEmployees;
    }

    @Override
    public String toString() {
        return "ImportSummary{" +
                "importedCount=" + importedCount +
                ", errors=" + errors +
                ", importedEmployees=" + importedEmployees +
                '}';
    }
}
