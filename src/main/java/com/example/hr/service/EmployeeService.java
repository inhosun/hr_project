package com.example.hr.service;

import com.example.hr.domain.Department;
import com.example.hr.domain.Employee;
import com.example.hr.dto.EmployeeForm;
import com.example.hr.exception.NotFoundException;
import com.example.hr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    /** 한 페이지에 보여줄 직원 수 */
    public static final int PAGE_SIZE = 5;

    /** 정렬을 허용하는 항목 (화면에서 넘어온 값 -> 엔티티 필드명). 목록에 없는 값은 무시 */
    private static final Map<String, String> SORTABLE = Map.of(
            "id", "id",
            "name", "name",
            "salary", "salary",
            "hireDate", "hireDate");

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final PhotoStorageService photoStorage;

    public Page<Employee> search(String keyword, int page, String sort, String dir) {
        String k = keyword == null ? "" : keyword.trim();
        String property = (sort == null) ? "id" : SORTABLE.getOrDefault(sort, "id");
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sorting = Sort.by(direction, property);
        if (!"id".equals(property)) {
            // 값이 같을 때 페이지마다 순서가 뒤섞이지 않도록 id를 보조 정렬 기준으로 추가
            sorting = sorting.and(Sort.by(Sort.Direction.DESC, "id"));
        }
        return employeeRepository.search(k, PageRequest.of(Math.max(page, 0), PAGE_SIZE, sorting));
    }

    public Employee get(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 직원입니다. (id=" + id + ")"));
    }

    public boolean isEmailTaken(String email, Long excludeId) {
        return excludeId == null
                ? employeeRepository.existsByEmail(email)
                : employeeRepository.existsByEmailAndIdNot(email, excludeId);
    }

    public String validatePhoto(MultipartFile photo) {
        return photoStorage.validate(photo);
    }

    @Transactional
    public Long create(EmployeeForm f) {
        Department department = departmentService.get(f.getDepartmentId());
        String photoFileName = photoStorage.store(f.getPhoto());
        Employee e = Employee.builder()
                .name(f.getName())
                .department(department)
                .position(f.getPosition())
                .email(f.getEmail())
                .salary(f.getSalary())
                .hireDate(f.getHireDate())
                .photoFileName(photoFileName)
                .build();
        return employeeRepository.save(e).getId();
    }

    @Transactional
    public void update(Long id, EmployeeForm f) {
        Employee e = get(id);
        Department department = departmentService.get(f.getDepartmentId());
        // 변경 감지(Dirty Checking)로 UPDATE 쿼리가 자동 실행됨
        e.update(f.getName(), department, f.getPosition(),
                f.getEmail(), f.getSalary(), f.getHireDate());

        String oldPhoto = e.getPhotoFileName();
        if (f.getPhoto() != null && !f.getPhoto().isEmpty()) {
            e.changePhoto(photoStorage.store(f.getPhoto()));
            photoStorage.delete(oldPhoto);
        } else if (f.isDeletePhoto()) {
            e.changePhoto(null);
            photoStorage.delete(oldPhoto);
        }
    }

    @Transactional
    public void delete(Long id) {
        Employee e = get(id);
        photoStorage.delete(e.getPhotoFileName());
        employeeRepository.delete(e);
    }
}
