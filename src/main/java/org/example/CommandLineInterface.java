package org.example;

import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    private UserAuthenticator userAuthenticator;
    private DataAccess dataAccess;

    public CommandLineInterface() {
        userAuthenticator = new UserAuthenticator();
        dataAccess = new DataAccess();

    }
    public String authenticateUserAndGetRole(String username, String password) {
        String role = userAuthenticator.authenticateUserAndGetRole(username, password);
        return role != null ? role : "unauthenticated";
    }
    public void start() {
        System.out.println("Welcome to the Student Grading System CLI!");

        // Get user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String role = userAuthenticator.authenticateUserAndGetRole(username, password);
        User authenticatedUser = dataAccess.retrieveUserByUsernameAndRole(username, role);


        if (role != null) {
            System.out.println("Authentication successful. Access granted.");

            if ("admin".equals(role)) {
                handleAdminFunctionalities();
            } else if ("instructor".equals(role)) {
                handleInstructorFunctionalities(authenticatedUser);
            } else if ("student".equals(role)) {
                handleStudentFunctionalities(authenticatedUser);
            }
        } else {
            System.out.println("Authentication failed. Access denied.");
        }
    }
    private void handleInstructorFunctionalities(User instructor) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Available Courses:");
        List<Course> instructorCourses = dataAccess.retrieveCoursesByInstructorId(instructor.getUserId());
        for (Course course : instructorCourses) {
            System.out.println(course.getCourseId() + ". " + course.getCourseName());
        }

        System.out.print("Choose a course (Enter Course ID): ");
        int courseId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        Course selectedCourse = null;
        for (Course course : instructorCourses) {
            if (course.getCourseId() == courseId) {
                selectedCourse = course;
                break;
            }
        }

        if (selectedCourse != null) {
            System.out.println("Available Students:");
            List<User> students = dataAccess.retrieveStudentsByCourseId(selectedCourse.getCourseId());
            for (User student : students) {
                System.out.println(student.getUserId() + ". " + student.getName());
            }

            System.out.print("Choose a student (Enter Student ID): ");
            int studentId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            User selectedStudent = null;
            for (User student : students) {
                if (student.getUserId() == studentId) {
                    selectedStudent = student;
                    break;
                }
            }

            if (selectedStudent != null) {
                System.out.print("Enter the marks: ");
                int marks = scanner.nextInt();
                scanner.nextLine(); // Consume the newline

                dataAccess.assignMarks(selectedStudent, selectedCourse, marks);
                System.out.println("Marks assigned successfully.");
            } else {
                System.out.println("Invalid student ID.");
            }
        } else {
            System.out.println("Invalid course ID.");
        }
    }
    private void handleAdminFunctionalities() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome, Admin!");
            System.out.println("1. Add Student");
            System.out.println("2. Add Instructor");
            System.out.println("3. Add Course");
            System.out.println("4. Assign Student to Course");
            System.out.println("5. Assign Instructor to Course");
            System.out.println("6. Quit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addStudent(scanner);
                    break;
                case 2:
                    addInstructor(scanner);
                    break;
                case 3:
                    addCourse(scanner);
                    break;
                case 4:
                    assignStudentToCourse(scanner);
                    break;
                case 5:
                    assignInstructorToCourse(scanner);
                    break;
                case 6:
                    System.out.println("Exiting Admin functionalities.");
                    return;
                default:
                    System.out.println("Invalid choice. Please choose again.");
            }
        }
    }

    private  void addCourse(Scanner scanner) {
        System.out.print("Enter course name: ");
        String courseName = scanner.nextLine();
        System.out.print("Enter instructor ID: ");
        int instructorId = scanner.nextInt();
        dataAccess.insertCourse(courseName, instructorId);
        System.out.println("Course added successfully.");

    }


    private void addStudent(Scanner scanner) {
        System.out.print("Enter student's username: ");
        String username = scanner.nextLine();
        System.out.print("Enter student's name: ");
        String name = scanner.nextLine();
        System.out.print("Enter student's password: ");
        String password = scanner.nextLine();

        dataAccess.insertStudent(username, name, password);
        System.out.println("Student added successfully.");
    }

    private void addInstructor(Scanner scanner) {
        System.out.print("Enter instructor's username: ");
        String username = scanner.nextLine();
        System.out.print("Enter instructor's name: ");
        String name = scanner.nextLine();
        System.out.print("Enter instructor's password: ");
        String password = scanner.nextLine();

        dataAccess.insertInstructor(username, name, password);
        System.out.println("Instructor added successfully.");
    }

    private void assignStudentToCourse(Scanner scanner) {
        System.out.print("Enter student's username: ");
        String username = scanner.nextLine();
        System.out.print("Enter course name: ");
        String courseName = scanner.nextLine();

        User student = dataAccess.retrieveUserByUsernameAndRole(username, "student");
        Course course = dataAccess.retrieveCourseByName(courseName);

        if (student != null && course != null) {
            dataAccess.assignUserToCourse(student, course);
            System.out.println("Student assigned to the course successfully.");
        } else {
            System.out.println("Student or course not found.");
        }
    }

    private void assignInstructorToCourse(Scanner scanner) {
        System.out.print("Enter instructor's username: ");
        String username = scanner.nextLine();
        System.out.print("Enter course name: ");
        String courseName = scanner.nextLine();

        User instructor = dataAccess.retrieveUserByUsernameAndRole(username, "instructor");
        Course course = dataAccess.retrieveCourseByName(courseName);

        if (instructor != null && course != null) {
            dataAccess.assignUserToCourse(instructor, course);
            System.out.println("Instructor assigned to the course successfully.");
        } else {
            System.out.println("Instructor or course not found.");
        }
    }


    private void handleStudentFunctionalities(User student) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Available Courses:");
        List<Course> studentCourses = dataAccess.retrieveCoursesByStudentId(student.getUserId());
        for (Course course : studentCourses) {
            System.out.println(course.getCourseId() + ". " + course.getCourseName());
        }

        System.out.print("Choose a course (Enter Course ID): ");
        int courseId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        Course selectedCourse = null;
        for (Course course : studentCourses) {
            if (course.getCourseId() == courseId) {
                selectedCourse = course;
                break;
            }
        }

        if (selectedCourse != null) {
            int studentId = student.getUserId();
            int marks = dataAccess.retrieveMarks(studentId, selectedCourse.getCourseId());

            if (marks != -1) {
                System.out.println("Marks for " + selectedCourse.getCourseName() + ": " + marks);
            } else {
                System.out.println("Marks not available for this course.");
            }
        } else {
            System.out.println("Invalid course ID.");
        }
    }

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.start();
    }
}
