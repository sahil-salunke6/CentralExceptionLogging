package org.practice.controller;

import org.practice.entity.EmployeeDT;
import org.practice.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmpController {

    @Autowired
    EmpService empService;

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDT>> getAllEmp() {
        List<EmployeeDT> employees = empService.getAll();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("id/{emp_id}")
    public ResponseEntity<EmployeeDT> getEmpById(@PathVariable("emp_id") int id) {
        EmployeeDT employee = empService.getEmpById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/add")
    public ResponseEntity<EmployeeDT> addEmp(@RequestBody EmployeeDT employee) {
        EmployeeDT saved = empService.addEmp(employee);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/addMulti")
    public ResponseEntity<List<EmployeeDT>> addMultipleEmp(@RequestBody List<EmployeeDT> employees) {
        List<EmployeeDT> savedList = empService.addMultipleEmp(employees);
        return ResponseEntity.ok(savedList);
    }

    @PutMapping("/update/{emp_id}")
    public ResponseEntity<EmployeeDT> updateEmp(@PathVariable("emp_id") int id, @RequestBody EmployeeDT employee) {
        EmployeeDT updated = empService.updateEmp(id, employee);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("remove/{emp_id}")
    public ResponseEntity<Void> deleteEmp(@PathVariable("emp_id") int id) {
        empService.deleteEmp(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
