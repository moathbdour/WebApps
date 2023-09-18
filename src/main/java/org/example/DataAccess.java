package org.example;

import org.example.Course;
import org.example.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataAccess {
     public void insertStudent(String username, String name, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, name, role, password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, "student");
                preparedStatement.setString(4, password);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertInstructor(String username, String name, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, name, role, password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, "instructor");
                preparedStatement.setString(4, password);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User retrieveUserByUsernameAndRole(String username, String role) {
        User user = null;
        String query = "SELECT * FROM users WHERE username = ? AND role = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, role);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("name"),
                            resultSet.getString("role"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Course retrieveCourseByName(String courseName) {
        Course course = null;
        String query = "SELECT * FROM courses WHERE course_name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, courseName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    course = new Course(
                            resultSet.getInt("course_id"),
                            resultSet.getString("course_name"),
                            resultSet.getInt("instructor_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }

    public void assignUserToCourse(User user, Course course) {
        if (user.getRole().equals("student")) {
            String query = "INSERT INTO students_courses (student_id, course_id) VALUES (?, ?)";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, user.getUserId());
                preparedStatement.setInt(2, course.getCourseId());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Only students can be assigned to courses.");
        }
    }

    public void insertCourse(String courseName, int instructorId) {
        User instructor = retrieveUserByIdAndRole(instructorId, "instructor");
        if (instructor != null) {
            String query = "INSERT INTO courses (course_name, instructor_id) VALUES (?, ?)";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, courseName);
                preparedStatement.setInt(2, instructorId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid instructor ID. Only instructors can create courses.");
        }
    }

    public User retrieveUserByIdAndRole(int userId, String role) {
        User user = null;
        String query = "SELECT * FROM users WHERE user_id = ? AND role = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, role);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("name"),
                            resultSet.getString("role"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    public List<Course> retrieveCoursesByInstructorId(int instructorId) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM courses WHERE instructor_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, instructorId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course(
                            resultSet.getInt("course_id"),
                            resultSet.getString("course_name"),
                            resultSet.getInt("instructor_id")
                    );
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<User> retrieveStudentsByCourseId(int courseId) {
        List<User> students = new ArrayList<>();
        String query = "SELECT u.* FROM users u " +
                "INNER JOIN students_courses sc ON u.user_id = sc.student_id " +
                "WHERE sc.course_id = ? AND u.role = 'student'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, courseId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User student = new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("name"),
                            resultSet.getString("role"),
                            resultSet.getString("password")
                    );
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public void assignMarks(User student, Course course, int marks) {
        String query = "UPDATE students_courses SET grade = ? " +
                "WHERE student_id = ? AND course_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, marks);
            preparedStatement.setInt(2, student.getUserId());
            preparedStatement.setInt(3, course.getCourseId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Course> retrieveCoursesByStudentId(int studentId) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.* FROM courses c " +
                "INNER JOIN students_courses sc ON c.course_id = sc.course_id " +
                "WHERE sc.student_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course(
                            resultSet.getInt("course_id"),
                            resultSet.getString("course_name"),
                            resultSet.getInt("instructor_id")
                    );
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public int retrieveMarks(int studentId, int courseId) {
        int marks = -1;
        String query = "SELECT grade FROM students_courses " +
                "WHERE student_id = ? AND course_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    marks = resultSet.getInt("grade");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marks;
    }

}
