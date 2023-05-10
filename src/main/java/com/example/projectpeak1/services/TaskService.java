package com.example.projectpeak1.services;


import com.example.projectpeak1.entities.Task;
import com.example.projectpeak1.repositories.IRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    IRepository repository;

    public TaskService(ApplicationContext context, @Value("${projectPeak.repository.impl}") String impl) {
        repository = (IRepository) context.getBean(impl);
    }


    public void createTask(Task task, int projectId){
        repository.createTask(task, projectId);
    }

    public Task getTaskById(int id){
        return repository.getTaskById(id);
    }

    public void editTask(Task task){
        repository.editTask(task);
    }


}
