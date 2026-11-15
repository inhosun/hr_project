package com.example.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepartmentSummary {
    private Long id;
    private String name;
    private Long employeeCount;
}
