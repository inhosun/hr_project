package com.example.hr.controller;

import com.example.hr.domain.Employee;
import com.example.hr.dto.EmployeeForm;
import com.example.hr.service.DepartmentService;
import com.example.hr.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    /** 목록: 검색 + 정렬 + 페이징 (/employees?keyword=개발&sort=name&dir=asc&page=1) */
    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "id") String sort,
                       @RequestParam(defaultValue = "desc") String dir,
                       Model model) {
        Page<Employee> result = employeeService.search(keyword, page, sort, dir);

        // 화면 하단에 보여줄 페이지 번호 범위 (현재 페이지 기준 최대 5개)
        int totalPages = result.getTotalPages();
        int end = Math.min(totalPages - 1, Math.max(0, result.getNumber() - 2) + 4);
        int start = Math.max(0, end - 4);

        model.addAttribute("page", result);
        model.addAttribute("employees", result.getContent());
        model.addAttribute("startPage", start);
        model.addAttribute("endPage", end);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        return "employees/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("employeeForm", new EmployeeForm());
        addFormAttributes(model, null, null);
        return "employees/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute EmployeeForm employeeForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (employeeService.isEmailTaken(employeeForm.getEmail(), null)) {
            bindingResult.rejectValue("email", "duplicate", "이미 사용 중인 이메일입니다.");
        }
        String photoError = employeeService.validatePhoto(employeeForm.getPhoto());
        if (photoError != null) {
            bindingResult.rejectValue("photo", "invalid", photoError);
        }
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, null, null);
            return "employees/form";
        }
        employeeService.create(employeeForm);
        ra.addFlashAttribute("message", "직원이 등록되었습니다.");
        return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.get(id);
        model.addAttribute("employeeForm", EmployeeForm.from(employee));
        addFormAttributes(model, id, employee.getPhotoFileName());
        return "employees/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute EmployeeForm employeeForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (employeeService.isEmailTaken(employeeForm.getEmail(), id)) {
            bindingResult.rejectValue("email", "duplicate", "이미 사용 중인 이메일입니다.");
        }
        String photoError = employeeService.validatePhoto(employeeForm.getPhoto());
        if (photoError != null) {
            bindingResult.rejectValue("photo", "invalid", photoError);
        }
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, id, employeeService.get(id).getPhotoFileName());
            return "employees/form";
        }
        employeeService.update(id, employeeForm);
        ra.addFlashAttribute("message", "직원 정보가 수정되었습니다.");
        return "redirect:/employees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        employeeService.delete(id);
        ra.addFlashAttribute("message", "직원이 삭제되었습니다.");
        return "redirect:/employees";
    }

    /** 등록/수정 폼 화면에 공통으로 필요한 값 (부서 목록 포함) */
    private void addFormAttributes(Model model, Long employeeId, String currentPhoto) {
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("currentPhoto", currentPhoto);
        model.addAttribute("departments", departmentService.findAll());
    }
}
