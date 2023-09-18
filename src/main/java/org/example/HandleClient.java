package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
public class HandleClient implements Runnable {
    private final Socket clientSocket;
    private final Map<String, String> userRoleMap;
    private DataAccess dataAccess;
    private BufferedReader reader;
    private PrintWriter writer;

    public HandleClient(Socket clientSocket, Map<String, String> userRoleMap) {
        this.clientSocket = clientSocket;
        this.userRoleMap = userRoleMap;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            dataAccess = new DataAccess();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String[] commandParts = reader.readLine().split(" ");
            String command = commandParts[0];

            if ("authenticate".equals(command)) {
                String username = commandParts[1];
                String password = commandParts[2];
                String role = authenticateUser(username, password);
                writer.println(role);
                userRoleMap.put(username, role);
            } else if (userRoleMap.containsKey(command)) {
                String userRole = userRoleMap.get(command);

                switch (userRole) {
                    case "admin":
                        handleAdminActions(command, commandParts, writer);
                        break;
                    case "instructor":
                        handleInstructorActions(command, commandParts, writer);
                        break;
                    case "student":
                        handleStudentActions(command, commandParts, writer);
                        break;
                    default:
                        writer.println("Invalid user role.");
                }
            } else {
                writer.println("Invalid command or authentication required.");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String authenticateUser(String username, String password) {
        UserAuthenticator authenticator = new UserAuthenticator();


        if (authenticator.authenticateUser(username, password)) {
            return authenticator.authenticateUserAndGetRole(username, password);
        } else {
            return "unauthenticated";
        }
    }
    private void handleAdminActions(String command, String[] commandParts, PrintWriter writer) {
        DataAccess dataAccess = new DataAccess(); // Initialize DataAccess

        switch (commandParts[1]) {
            case "addStudent":
                String studentUsername = commandParts[2];
                String studentName = commandParts[3];
                String studentPassword = commandParts[4];

                dataAccess.insertStudent(studentUsername, studentName, studentPassword);

                writer.println("Student added successfully.");
                break;

            case "addInstructor":
                String instructorUsername = commandParts[2];
                String instructorName = commandParts[3];
                String instructorPassword = commandParts[4];

                dataAccess.insertInstructor(instructorUsername, instructorName, instructorPassword);

                writer.println("Instructor added successfully.");
                break;

            case "addCourse":
                String courseName = commandParts[2];
                int instructorId = Integer.parseInt(commandParts[3]);

                dataAccess.insertCourse(courseName, instructorId);

                writer.println("Course added successfully.");
                break;

            case "assignStudentToCourse":
                studentUsername = commandParts[2];
                courseName = commandParts[3];

                User student = dataAccess.retrieveUserByUsernameAndRole(studentUsername, "student");
                Course course = dataAccess.retrieveCourseByName(courseName);

                if (student != null && course != null) {
                    dataAccess.assignUserToCourse(student, course);

                    writer.println("Student assigned to course successfully.");
                } else {
                    writer.println("Student or course not found.");
                }
                break;

            case "assignInstructorToCourse":
                instructorUsername = commandParts[2];
                courseName = commandParts[3];

                User instructor = dataAccess.retrieveUserByUsernameAndRole(instructorUsername, "instructor");
                course = dataAccess.retrieveCourseByName(courseName);

                if (instructor != null && course != null) {
                    dataAccess.assignUserToCourse(instructor, course);

                    writer.println("Instructor assigned to course successfully.");
                } else {
                    writer.println("Instructor or course not found.");
                }
                break;

            case "logout":


                writer.println("Logged out.");
                break;

            default:
                writer.println("Invalid admin action.");
        }
    }


    private void handleInstructorActions(String command, String[] commandParts, PrintWriter writer) {

    }

    private void handleStudentActions(String command, String[] commandParts, PrintWriter writer) {

    }
}
