package com.example.hr.service;

import com.example.hr.domain.Department;
import com.example.hr.dto.DepartmentSummary;
import com.example.hr.exception.BusinessException;
import com.example.hr.exception.NotFoundException;
import com.example.hr.repository.DepartmentRepository;
import com.example.hr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public List<Department> findAll() {
        return departmentRepository.findAllByOrderByNameAsc();
    }

    public List<DepartmentSummary> findAllWithEmployeeCount() {
        return departmentRepository.findAllWithEmployeeCount();
    }

    public Department get(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 부서입니다. (id=" + id + ")"));
    }

    public boolean isNameTaken(String name) {
        return departmentRepository.existsByName(name.trim());
    }

    @Transactional
    public Long create(String name) {
        return departmentRepository.save(new Department(name.trim())).getId();
    }

    @Transactional
    public void delete(Long id) {
        Department department = get(id);
        long count = employeeRepository.countByDepartmentId(id);
        if (count > 0) {
            throw new BusinessException(
                    "'" + department.getName() + "' 부서에 소속 직원이 " + count + "명 있어 삭제할 수 없습니다.");
        }
        departmentRepository.delete(department);
    }
}
