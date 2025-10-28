package com.techcorp.employee.model;

public enum JobTitle {
    PRESIDENT("President", 25000, 1),
    VICE_PRESIDENT("Vice President", 18000, 2),
    MANAGER("Manager", 12000, 3),
    DEVELOPER("Developer", 8000, 4),
    INTERN("Intern", 3000, 5);

    private final String displayName;
    private final double baseSalary;
    private final int hierarchyLevel;

    JobTitle(String displayName, double baseSalary, int hierarchyLevel) {
        this.displayName = displayName;
        this.baseSalary = baseSalary;
        this.hierarchyLevel = hierarchyLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public String toString() {
        return displayName;
    }
}

