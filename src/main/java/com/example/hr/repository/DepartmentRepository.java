package com.example.hr.repository;

import com.example.hr.domain.Department;
import com.example.hr.dto.DepartmentSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAllByOrderByNameAsc();

    boolean existsByName(String name);

    // 부서별 소속 직원 수 (직원이 없는 부서도 나오도록 left join)
    @Query("select new com.example.hr.dto.DepartmentSummary(d.id, d.name, count(e)) " +
           "from Department d left join Employee e on e.department = d " +
           "group by d.id, d.name order by d.name")
    List<DepartmentSummary> findAllWithEmployeeCount();
}
