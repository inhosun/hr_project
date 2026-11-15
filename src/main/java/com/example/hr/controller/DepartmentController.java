package com.example.hr.controller;

import com.example.hr.dto.DepartmentForm;
import com.example.hr.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("departments", departmentService.findAllWithEmployeeCount());
        model.addAttribute("departmentForm", new DepartmentForm());
        return "departments/list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute DepartmentForm departmentForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        // 이름 검증을 통과했을 때만 중복 검사
        if (!bindingResult.hasFieldErrors("name") && departmentService.isNameTaken(departmentForm.getName())) {
            bindingResult.rejectValue("name", "duplicate", "이미 존재하는 부서입니다.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.findAllWithEmployeeCount());
            return "departments/list";
        }
        departmentService.create(departmentForm.getName());
        ra.addFlashAttribute("message", "부서가 등록되었습니다.");
        return "redirect:/departments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        // 소속 직원이 있으면 BusinessException -> GlobalExceptionHandler가 처리
        departmentService.delete(id);
        ra.addFlashAttribute("message", "부서가 삭제되었습니다.");
        return "redirect:/departments";
    }
}
