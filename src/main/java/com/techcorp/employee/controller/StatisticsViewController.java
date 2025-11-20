package com.techcorp.employee.controller;

import com.techcorp.employee.service.DepartmentService;
import com.techcorp.employee.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistics")
public class StatisticsViewController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public StatisticsViewController(EmployeeService employeeService,
                                    DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String statistics(Model model) {

        model.addAttribute("employeeCount", employeeService.getAllEmployees().size());
        model.addAttribute("averageSalary", employeeService.calculateAverageSalary());
        model.addAttribute("departmentCount", departmentService.getAllDepartments().size());

        model.addAttribute("companyStats", employeeService.getCompanyStatistics().entrySet());

        return "statistics/index";
    }
}
