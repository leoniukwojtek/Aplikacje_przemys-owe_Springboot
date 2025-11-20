package com.techcorp.employee.controller;

import com.techcorp.employee.mapper.EmployeeMapper;
import com.techcorp.employee.model.Department;
import com.techcorp.employee.service.DepartmentService;
import com.techcorp.employee.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/departments")
public class DepartmentViewController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;


    public DepartmentViewController(DepartmentService departmentService,
                                    EmployeeService employeeService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public String list(Model model) {
        List<Department> deptemp = departmentService.getAllDepartments();
        System.out.println(deptemp.size());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments/list";
    }


    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("managers", employeeService.getAllEmployees());
        return "departments/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Department department, RedirectAttributes ra) {
        departmentService.addDepartment(department);
        ra.addFlashAttribute("message", "Dodano departament");
        return "redirect:/departments";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model) {

        Department d = departmentService.getDepartmentById(id);
        model.addAttribute("department", d);

        var manager = employeeService.getEmployeeByEmail(d.getManagerEmail());
        model.addAttribute("managerName",
                manager != null ? manager.getFirstName()+" "+manager.getLastName() : "Unknown");

        var employees = employeeService.getAllEmployees().stream()
                .filter(e -> e.getDepartmentId() != null && e.getDepartmentId().equals(id))
                .map(EmployeeMapper::toEntity)
                .toList();

        model.addAttribute("employees", employees);

        return "departments/details";
    }
}
