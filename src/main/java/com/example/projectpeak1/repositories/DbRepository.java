package com.example.projectpeak1.repositories;

import com.example.projectpeak1.entities.Project;
import com.example.projectpeak1.entities.Task;
import com.example.projectpeak1.entities.User;
import com.example.projectpeak1.utility.DbManager;
import org.springframework.stereotype.Repository;

import javax.security.auth.login.LoginException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Repository("dbRepository")
public class DbRepository implements IRepository {

    @Override
    public User login(String email, String password) throws LoginException {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "SELECT * FROM user WHERE email = ? AND user_password = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("user_id");
                User user = new User(email, password);
                user.setUserId(id);
                return user;
            } else {
                throw new LoginException("Wrong email or password");
            }
        } catch (SQLException ex) {
            throw new LoginException(ex.getMessage());
        }

    }

    @Override
    public User createUser(User user) throws LoginException {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "INSERT INTO user (email, user_password, fullname) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.executeUpdate();
            ResultSet ids = ps.getGeneratedKeys();
            ids.next();
            int id = ids.getInt(1);
            User user1 = new User(user.getEmail(), user.getPassword());
            user.setUserId(id);
            return user1;
        } catch (SQLException ex) {
            throw new LoginException(ex.getMessage());
        }
    }
    @Override
    public User getUserFromId(int id) {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "SELECT * FROM USER WHERE USER_ID = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            User user1 = null;
            if (rs.next()) {
                int userId = rs.getInt("USER_ID");
                String fullName = rs.getString("FULLNAME");
                String email = rs.getString("EMAIL");
                String userPassword = rs.getString("USER_PASSWORD");
                String userRole = rs.getString("USER_PASSWORD");
                user1 = new User(userId, fullName, email, userPassword, userRole);
            }

            return user1;

        } catch (SQLException ex) {
            return null;
        }
    }
    @Override
    public void editUser(User user) throws LoginException {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "UPDATE USER SET USER_ID = ?, FULLNAME = ?, EMAIL = ?, USER_PASSWORD = ? WHERE user_id = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, user.getUserId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setInt(5, user.getUserId());
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new LoginException(ex.getMessage());
        }
    }



    @Override
    public void deleteUser(int userId) throws LoginException {
        try (Connection con = DbManager.getConnection()) {
            // delete all wishes associated with the user
            String SQL = "DELETE FROM WISH WHERE WISHLIST_ID IN " +
                    "(SELECT WISHLIST_ID FROM WISHLIST WHERE USER_ID = ?)";
            try (PreparedStatement stmt = con.prepareStatement(SQL)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            // delete all wishlists associated with the user
            SQL = "DELETE FROM WISHLIST WHERE USER_ID = ?";
            try (PreparedStatement stmt = con.prepareStatement(SQL)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            // delete the user record
            SQL = "DELETE FROM USER WHERE USER_ID = ?";
            try (PreparedStatement stmt = con.prepareStatement(SQL)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new LoginException(ex.getMessage());
        }
    }

    public Project createProject(Project project, int userId) {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "INSERT INTO Project (name, description, start_date, end_date, user_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, project.getProjectName());
            ps.setString(2, project.getProjectDescription());
            ps.setDate(3, Date.valueOf(project.getProjectStartDate()));
            ps.setDate(4, Date.valueOf(project.getProjectEndDate()));
            ps.setInt(5, userId);
            ps.executeUpdate();
            ResultSet ids = ps.getGeneratedKeys();
            ids.next();
            int id = ids.getInt(1);


            Project createdProject = new Project(project.getProjectName(), project.getProjectDescription(), project.getProjectStartDate(), project.getProjectEndDate(), project.getUserId());
            createdProject.setProjectId(id);
            return createdProject;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public List<Project> getAllProjectById(int userId) {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "SELECT * FROM project WHERE user_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Project> list = new ArrayList<>();
            while (rs.next()) {
                int projectId = rs.getInt("project_id");
                String projectName = rs.getString("name");
                String projectDescription = rs.getString("description");
                LocalDate projectStartDate = rs.getDate("start_date").toLocalDate();
                LocalDate projectEndDate = rs.getDate("end_date").toLocalDate();
                Project project = new Project(projectId, projectName, projectDescription, projectStartDate, projectEndDate, userId);
                list.add(project);
            }
            return list;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteProject(int projectId) throws LoginException {
        try (Connection con = DbManager.getConnection()) {
            // delete the project record
            String SQL = "DELETE FROM project WHERE project_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(SQL)) {
                stmt.setInt(1, projectId);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new LoginException(ex.getMessage());
        }
    }

    @Override
    public Project getProjectById(int id) {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "SELECT * FROM project WHERE project_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Project project = null;
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String projectName = rs.getString("name");
                String projectDescription = rs.getString("description");
                LocalDate projectStartDate = rs.getDate("start_date").toLocalDate();
                LocalDate projectEndDate = rs.getDate("end_date").toLocalDate();
                project = new Project(id, projectName, projectDescription, projectStartDate, projectEndDate, userId);
            }
            return project;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateProject(Project project) {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "UPDATE project SET name = ?, description = ?, start_date = ?, end_date = ? WHERE project_id = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, project.getProjectName());
            ps.setString(2, project.getProjectDescription());
            ps.setDate(3, Date.valueOf(project.getProjectStartDate()));
            ps.setDate(4, Date.valueOf(project.getProjectEndDate()));
            ps.setInt(5, project.getProjectId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Task createTask(Task task, int projectId) {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "INSERT INTO Task (name, description, start_date, end_date, status, project_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, task.getTaskName());
            ps.setString(2, task.getTaskDescription());
            ps.setDate(3, Date.valueOf(task.getTaskStartDate()));
            ps.setDate(4, Date.valueOf(task.getTaskEndDate()));
            ps.setString(5, task.getStatus());
            ps.setInt(6, projectId);
            ps.executeUpdate();
            ResultSet ids = ps.getGeneratedKeys();
            ids.next();
            int id = ids.getInt(1);


            Task createdTask = new Task(task.getTaskName(), task.getTaskDescription(), task.getTaskStartDate(), task.getTaskEndDate(), task.getStatus(), projectId);
            createdTask.setTaskId(id);
            return createdTask;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Task> getAllTaskById(int id) {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "SELECT * FROM Task WHERE project_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            List<Task> taskList = new ArrayList<>();

            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                String taskName = rs.getString("name");
                String taskDescription = rs.getString("description");
                LocalDate taskStartDate = rs.getDate("start_date").toLocalDate();
                LocalDate taskEndDate = rs.getDate("end_date").toLocalDate();
                String taskStatus = rs.getString("status");
                Task task = new Task(taskId, taskName, taskDescription, taskStartDate, taskEndDate, taskStatus, id);
                taskList.add(task);
            }
            return taskList;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Task getTaskById(int id) {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "SELECT * FROM Task WHERE task_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Task task = new Task();

            if (rs.next()) {
                int taskId = rs.getInt("task_id");
                String taskName = rs.getString("name");
                String taskDescription = rs.getString("description");
                LocalDate taskStartDate = rs.getDate("start_date").toLocalDate();
                LocalDate taskEndDate = rs.getDate("end_date").toLocalDate();
                String taskStatus = rs.getString("status");
                int projectId = rs.getInt("project_id");
                task = new Task(taskId, taskName, taskDescription, taskStartDate, taskEndDate, taskStatus, projectId);

            }
            return task;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void editTask(Task task) {
        try {
            Connection con = DbManager.getConnection();
            String SQL = "UPDATE task SET name = ?, description = ?, start_date = ?, end_date = ?, status = ? WHERE task_id = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, task.getTaskName());
            ps.setString(2, task.getTaskDescription());
            ps.setDate(3, Date.valueOf(task.getTaskStartDate()));
            ps.setDate(4, Date.valueOf(task.getTaskEndDate()));
            ps.setString(5, task.getStatus());
            ps.setInt(6, task.getTaskId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


}
