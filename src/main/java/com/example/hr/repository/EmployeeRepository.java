package com.example.hr.repository;

import com.example.hr.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * 이름 또는 부서명으로 검색 (페이징 + 정렬)
     * - @EntityGraph: 부서를 직원과 한 번에 조회 (목록에서 부서명을 쓰므로 N+1 문제 방지)
     * - 검색어가 빈 문자열("")이면 like '%%' 이므로 전체가 조회됨
     * - Pageable을 쓰면 total count 쿼리가 필요해서 countQuery를 따로 지정
     */
    @EntityGraph(attributePaths = "department")
    @Query(value = "select e from Employee e " +
                   "where e.name like concat('%', :keyword, '%') " +
                   "   or e.department.name like concat('%', :keyword, '%')",
           countQuery = "select count(e) from Employee e " +
                        "where e.name like concat('%', :keyword, '%') " +
                        "   or e.department.name like concat('%', :keyword, '%')")
    Page<Employee> search(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    // 부서에 소속된 직원 수 (부서 삭제 가능 여부 확인용)
    long countByDepartmentId(Long departmentId);
}
