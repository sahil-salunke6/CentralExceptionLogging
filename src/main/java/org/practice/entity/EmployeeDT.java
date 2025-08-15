package org.practice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@Data
public class EmployeeDT {

    @Id
    private long emp_id;
    private String emp_name;
    private String designation;
}
