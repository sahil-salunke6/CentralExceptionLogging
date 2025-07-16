package org.practice.service;

import jakarta.persistence.EntityNotFoundException;
import org.practice.entity.EmployeeDT;
import org.practice.repository.EmpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpService {

    @Autowired
    private EmpRepository repository;

    public List<EmployeeDT> getAll() {
        try {
            return repository.findAll();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while fetching all employees.", e);
        }
    }

    public EmployeeDT getEmpById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));
    }

    public EmployeeDT addEmp(EmployeeDT employee) {
        try {
            repository.save(employee);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while saving the employee.", e);
        }
        return employee;
    }

    public List<EmployeeDT> addMultipleEmp(List<EmployeeDT> employeeList) {
        try {
            repository.saveAll(employeeList);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while saving multiple employees.", e);
        }
        return employeeList;
    }

    public EmployeeDT updateEmp(int id, EmployeeDT updatedEmp) {
        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot update. Employee not found with ID: " + id));
        try {
            repository.save(updatedEmp);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while updating the employee.", e);
        }
        return updatedEmp;
    }

    public void deleteEmp(int id) {
        EmployeeDT employee = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot delete. Employee not found with ID: " + id));

        try {
            repository.delete(employee);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while deleting the employee.", e);
        }
    }
}
