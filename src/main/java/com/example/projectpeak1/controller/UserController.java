package com.example.projectpeak1.controller;

import com.example.projectpeak1.entities.Project;
import com.example.projectpeak1.entities.User;
import com.example.projectpeak1.repositories.IRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping({""})
public class UserController {

    IRepository repository;

    public UserController(ApplicationContext context, @Value("${projectPeak.repository.impl}") String impl) {
        repository = (IRepository) context.getBean(impl);
    }

    private int getUserId(HttpServletRequest request) {
        int userId = (int) request.getSession().getAttribute("userId");
        return userId;
    }

    @GetMapping(value = {"/userFrontend"})
    public String index(HttpServletRequest request, Model model) {
        int userId = getUserId(request);
        if (userId == 0) {
            return "login";
        }
        User user = repository.getUserFromId(userId);
        model.addAttribute("user", user);

        List<Project> list = repository.getAllProjectById(userId);
        model.addAttribute("projects", list);
        return "userFrontend";
    }

    @GetMapping("/createProject")
    public String createProject(HttpServletRequest request, Model model) {
        int userId = getUserId(request);
        if (userId == 0) {
            return "login";
        }
        User user = repository.getUserFromId(userId);
        model.addAttribute("user", user);

        model.addAttribute("project", new Project());
        return "createProject";
    }


    @PostMapping(value = {"/createProject"})
    public String processCreateProject(HttpServletRequest request, @ModelAttribute Project project) {
        int userId = getUserId(request);

        repository.createProject(project, userId);
        return "redirect:/userFrontend";
    }


}

