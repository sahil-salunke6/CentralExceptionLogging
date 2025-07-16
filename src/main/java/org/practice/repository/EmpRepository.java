package org.practice.repository;

import org.practice.entity.EmployeeDT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpRepository extends JpaRepository<EmployeeDT, Integer> {
}
