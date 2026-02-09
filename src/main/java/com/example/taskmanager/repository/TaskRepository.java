package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("SELECT task FROM Task task WHERE task.category.name = :name")
    List<Task> findByCategory_Name(String name);

    @Query("SELECT task FROM Task task WHERE task.type = :type")
    List<Task> findByType(TaskType type);
}
