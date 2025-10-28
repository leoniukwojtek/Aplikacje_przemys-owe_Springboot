package com.techcorp.employee.model;

import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
public class EmailSet {
    private final Set<String> emails = new HashSet<>();

    public boolean addEmail(String email) {
        return emails.add(email.toLowerCase());
    }

    public boolean removeEmail(String email) {
        return emails.remove(email.toLowerCase());
    }

    public boolean containsEmail(String email) {
        return email != null && emails.contains(email.toLowerCase());
    }

    public int size() {
        return emails.size();
    }

    public void clear() {
        emails.clear();
    }

    @Override
    public String toString() {
        return emails.toString();
    }
}
