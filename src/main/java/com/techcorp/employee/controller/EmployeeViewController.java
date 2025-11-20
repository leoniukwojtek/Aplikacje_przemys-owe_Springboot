package com.techcorp.employee.controller;

import com.techcorp.employee.dto.EmployeeDTO;
import com.techcorp.employee.mapper.EmployeeMapper;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeViewController {

    private final EmployeeService employeeService;

    public EmployeeViewController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/details")
    public String list2(Model model) {
        System.out.println(employeeService.getAllEmployees().size());
//        model.addAttribute("employee", employeeService.getAllEmployees().getFirst());
        return "employees/emp";
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("employee", new EmployeeDTO());
        return "employees/add-form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Employee employee, RedirectAttributes ra) {
        try {
            employeeService.addEmployee(employee);
            ra.addFlashAttribute("message", "Pracownik dodany!");
        } catch (Exception e) {
            ra.addFlashAttribute("message", "Błąd: " + e.getMessage());
        }
        return "redirect:/employees";
    }

    @GetMapping("/edit/{email}")
    public String editForm(@PathVariable String email, Model model) {
        var dto = employeeService.getEmployeeByEmail(email);
        model.addAttribute("employee", EmployeeMapper.toEntity(dto));
        return "employees/edit-form";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute Employee employee, RedirectAttributes ra) {
        employeeService.updateEmployee(employee.getEmailAddress(), EmployeeMapper.toDTO(employee));
        ra.addFlashAttribute("message", "Zaktualizowano pracownika");
        return "redirect:/employees";
    }

    @GetMapping("/delete/{email}")
    public String delete(@PathVariable String email, RedirectAttributes ra) {
        employeeService.deleteEmployee(email);
        ra.addFlashAttribute("message", "Usunięto pracownika");
        return "redirect:/employees";
    }

    @GetMapping("/search")
    public String searchForm() {
        return "employees/search-form";
    }

    @PostMapping("/search")
    public String search(@RequestParam String company, Model model) {
        model.addAttribute("company", company);
        model.addAttribute("results", employeeService.findEmployeesByCompany(company));
        return "employees/search-results";
    }

    @GetMapping("/import")
    public String importForm() {
        return "employees/import-form";
    }

    @PostMapping("/import")
    public String importEmployees(@RequestParam("file") MultipartFile file,
                                  @RequestParam("fileType") String type,
                                  RedirectAttributes ra) {
        try {
            int n = employeeService.importFromCsv(file);
            ra.addFlashAttribute("message", "Zaimportowano: " + n);
        } catch (Exception e) {
            ra.addFlashAttribute("message", "Błąd: " + e.getMessage());
        }
        return "redirect:/employees";
    }
}
