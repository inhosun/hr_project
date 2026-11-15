package com.example.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentForm {

    @NotBlank(message = "부서명을 입력하세요.")
    @Size(max = 50, message = "부서명은 50자 이하로 입력하세요.")
    private String name;
}
