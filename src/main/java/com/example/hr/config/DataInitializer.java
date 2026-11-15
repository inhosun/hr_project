package com.example.hr.config;

import com.example.hr.domain.Department;
import com.example.hr.domain.Employee;
import com.example.hr.repository.DepartmentRepository;
import com.example.hr.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

/** 앱 시작 시 샘플 데이터 입력 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        return args -> {
            Department[] departments = {
                    departmentRepository.save(new Department("개발팀")),
                    departmentRepository.save(new Department("인사팀")),
                    departmentRepository.save(new Department("영업팀")),
                    departmentRepository.save(new Department("마케팅팀"))
            };
            // 직원이 없는 부서 (삭제 가능한 부서 확인용)
            departmentRepository.save(new Department("기획팀"));

            String[] names = {"김민수", "이서연", "박지훈", "최유진", "정하늘", "강도현", "조수빈",
                              "윤재원", "임나연", "한지호", "오세린", "서준혁", "신아름"};
            String[] positions = {"사원", "대리", "과장", "차장", "부장"};

            for (int i = 0; i < names.length; i++) {
                employeeRepository.save(Employee.builder()
                        .name(names[i])
                        .department(departments[i % departments.length])
                        .position(positions[i % positions.length])
                        .email("emp" + (i + 1) + "@example.com")
                        .salary(3_000_000L + i * 250_000L)
                        .hireDate(LocalDate.of(2015 + i % 9, 1 + i % 12, 1 + i))
                        .build());
            }
        };
    }
}
