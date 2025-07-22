package com.railbit.TicketManagementSystem.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.railbit.TicketManagementSystem.Entity.Department;
import com.railbit.TicketManagementSystem.Repository.DepartmentRepository;

@Service
public class DepartmentService {
     
    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public void saveDepartment(Department department) {
        departmentRepository.save(department);
    }

    public Department getById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }
    public void deleteById(Long id) {
        departmentRepository.deleteById(id);
    }
    
    public Optional<Department> getByName(String name) {
        Optional<Department> dept = departmentRepository.findByName(name);
        System.out.println("Searching for department '" + name + "': found? " + dept.isPresent());
        return dept;
    }



}
