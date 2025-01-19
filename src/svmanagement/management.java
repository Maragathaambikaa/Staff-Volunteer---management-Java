package svmanagement;

import java.sql.*;

import java.sql.Date;
import java.util.*;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


// Custom Exceptions
class StaffNotFoundException extends Exception {
    public StaffNotFoundException(String message) {
        super(message);
    }
}

class VolunteerNotAssignedException extends Exception {
    public VolunteerNotAssignedException(String message) {
        super(message);
    }
}

// Interface for Assignable tasks
interface Assignable {
    void assignTask(int volunteerId, int taskId) throws VolunteerNotAssignedException;
   void trackVolunteerHours(int volunteerId, int hours);
    void updateSchedule(int staffId, int volunteerId, int taskId, Date startDate, Date endDate);
}

// Base Staff Class
class Staff {
    protected int staffId;
    protected String name;

    public Staff(int staffId, String name) {
        this.staffId = staffId;
        this.name = name;
    }
}
//admin child class
class Admin extends Staff {
    public Admin(int staffId, String name) {
        super(staffId, name);
    }
}
//support staff child class
class Support extends Staff {
    public Support(int staffId, String name) {
        super(staffId, name);
    }
}

// Volunteer Class
class Volunteer {
    private int volunteerId;
    private String name;
    private int hoursWorked;

    public Volunteer(int volunteerId, String name,int hoursWorked ) {
        this.volunteerId = volunteerId;
        this.name = name;
        this.hoursWorked = 0;
    }

    public void addHours(int hours) {
        this.hoursWorked += hours;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }
}

// Task Class
class Task {
    private int taskId;
    private String description;
    private boolean isCompleted;

    public Task(int taskId, String description) {
        this.taskId = taskId;
        this.description = description;
        this.isCompleted = false;
    }

    public void completeTask() {
        this.isCompleted = true;
    }
}

// Main Management Class implementing Assignable interface
class StaffVolunteerManagement implements Assignable {
    private HashMap<Integer, Staff> staffList = new HashMap<>();
    private HashMap<Integer, Volunteer> volunteerList = new HashMap<>();
    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<Schedule> scheduleList = new ArrayList<>();
   
    public Connection con;
    public StaffVolunteerManagement(Connection con)
    {
    	this.con=con;
    }

    // Adding a staff member to the database
    public void addStaff(int staffId, String name, String role) {
        try {
            String query = "insert into staff (staff_id, name, role) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, staffId);
            pst.setString(2, name);
            pst.setString(3, role);
            int rs = pst.executeUpdate();
            if (rs > 0) {
                System.out.println("Staff added successfully.");
            }
        } catch (SQLException e) {
        	System.out.println("Error Ocurred");
        }
    }

    // Adding a volunteer to the database
    public void addVolunteer(int volunteerId, String name) {
        try {
            String query = "insert into volunteer (volunteer_id, name) values (?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, volunteerId);
            stmt.setString(2, name);
            int rs = stmt.executeUpdate();
            if (rs > 0) {
                System.out.println("Volunteer added successfully.");
            }
        } catch (SQLException e) {
        	System.out.println("Error Ocurred");
        }
    }

    // Adding a task to the database
    public void addTask(int taskId, String description) {
        try {
            String query = "insert into task (task_id, description)values (?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, taskId);
            stmt.setString(2, description);
            int rs = stmt.executeUpdate();
            if (rs > 0) {
                System.out.println("Task added successfully.");
            }
        } catch (SQLException e) {
        	
        	System.out.println("Error Ocurred");
        }
    }

    // Assign a task to a volunteer (update the database)
    @Override
    public void assignTask(int volunteerId, int taskId) throws VolunteerNotAssignedException {
        try {
            String query = "insert into volunteertask (volunterr_id, task_id) VALUES (?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, volunteerId);
            stmt.setInt(2, taskId);
            int rs = stmt.executeUpdate();
            if (rs > 0) {
                System.out.println("Task assigned to the volunteer successfully.");
            }
        } catch (SQLException e) {
           
            throw new VolunteerNotAssignedException("Volunteer or Task not found");
        }
    }

    // Track volunteer hours (update in database)
    @Override
    public void trackVolunteerHours(int volunteerId, int hours) {
        try {
            String query = "update volunteer set hours_worked = hours_worked + ? where volunteer_id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, hours);
            stmt.setInt(2, volunteerId);
            int rs = stmt.executeUpdate();
            if (rs > 0) {
                System.out.println("Volunteer hours updated.");
            }
        } catch (SQLException e) {
        	System.out.println("Error Ocurred");
        }
    }

    // Update staff-volunteer schedule (update in database)
    @Override
    public void updateSchedule(int staffId, int volunteerId, int taskId, Date startDate, Date endDate) {
        try {
            String query = "insert into schedule (staff_id, volunteer_id, task_id, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, staffId);
            pst.setInt(2, volunteerId);
            pst.setInt(3, taskId);
            pst.setDate(4, startDate);
            pst.setDate(5, endDate);
            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Schedule updated.");
            }
        } catch (SQLException e) {
        	System.out.println("Error Ocurred");
        }
    }

   public void viewSchedule() {
	   String query="Select *from schedule";
	   try
   	{
		
   		PreparedStatement pst=con.prepareStatement(query);
   		ResultSet rs = pst.executeQuery();
   		while(rs.next())
   		{
   			String result=rs.getInt("schedule_id")+rs.getInt("staff_id")+"\t"+rs.getInt("volunteer_id")+"\t"+rs.getInt("task_id")+"\t"+rs.getDate("start_date")+rs.getDate("end_date")+"\t";
   			System.out.println(result);
   		   try(FileWriter f= new FileWriter("schedule.txt",true);//true to avoid overwrite
   			BufferedWriter bf=new BufferedWriter(f);)	
   		 {
   			//manipulate the text as per convienient
   			bf.write(result);
   		}
   		catch(Exception e)
   		{
   			
   		}
   		}
   	}catch(SQLException e)
   	{
   		
   	}
}
    // Retrieve(display) staff details from database
    public Staff getStaffById(int staffId) {
        try {
            String query = "select * from staff where staff_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, staffId);
            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String role = resultSet.getString("role");
                return new Staff(staffId, name);
            }
        } catch (SQLException e) {
        	System.out.println("Error Ocurred");
        }
        return null;
    }

    // Display volunteer details from database
    public Volunteer getVolunteerById(int volunteerId) {
        try {
            String query = "select * from volunteer Where volunteer_id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int hours_worked = rs.getInt("hours_worked");
                return new Volunteer(volunteerId, name,hours_worked);
            }
        } catch (SQLException e) {
        	System.out.println("Error Ocurred");
        }
        return null;
    }

    // Display task details from database
    public Task getTaskById(int taskId) {
        try {
            String query = "select* from task where task_id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, taskId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String description = resultSet.getString("description");
                return new Task(taskId, description);
            }
        } catch (SQLException e) {
        	System.out.println("Error Ocurred");
        }
        return null;
    }
}

// Schedule Class
class Schedule {
    private int staffId;
    private int volunteerId;
    private int taskId;
    private Date startDate;
    private Date endDate;

    public Schedule(int staffId, int volunteerId, int taskId, Date startDate, Date endDate) {
        this.staffId = staffId;
        this.volunteerId = volunteerId;
        this.taskId = taskId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Staff ID: " + staffId + ", Volunteer ID: " + volunteerId + ", Task ID: " + taskId + ", Start: " + startDate + ", End: " + endDate;
    }
}

// Main Class for Running the Application
public class management {
    public static void main(String[] args) {
    	try {
    	Connection con=DBConnection.getConnection();
        
    	//StaffVolunteerManagement sv = new StaffVolunteerManagement(con);
        Scanner sc = new Scanner(System.in);
        StaffVolunteerManagement management = new StaffVolunteerManagement(con);
        
        boolean run = true;

        while (run) {
            System.out.println("Menu:");
            System.out.println("1. Add Staff");
            System.out.println("2. Add Volunteer");
            System.out.println("3. Add Task");
            System.out.println("4. Assign Task to Volunteer");
            System.out.println("5. Track Volunteer Hours");
            System.out.println("6. Update Schedule");
            System.out.println("7. Display Schedule");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) 
            {
                case 1:
                    System.out.print("Enter staff ID: ");
                    int staffId = sc.nextInt();
                    System.out.print("Enter name: ");
                    String staffName = sc.next();
                    System.out.print("Enter role (Admin or Support): ");
                    String role = sc.next();
                    management.addStaff(staffId, staffName, role);
                    break;
                case 2:
                    System.out.print("Enter volunteer ID: ");
                    int volunteerId = sc.nextInt();
                    System.out.print("Enter volunteer name: ");
                    String volunteerName = sc.next();
                    management.addVolunteer(volunteerId, volunteerName);
                    break;
                case 3:
                    System.out.print("Enter task ID: ");
                    int taskId = sc.nextInt();
                    System.out.print("Enter task description: ");
                    String description = sc.next();
                    management.addTask(taskId, description);
                    break;
                case 4:
                    System.out.print("Enter volunteer ID: ");
                    int vId = sc.nextInt();
                    System.out.print("Enter task ID: ");
                    int tId = sc.nextInt();
                    try {
                        management.assignTask(vId, tId);
                    } catch (VolunteerNotAssignedException e) {
                       
                    }
                    break;
                case 5:
                    System.out.print("Enter volunteer ID: ");
                    int volunteerHoursId = sc.nextInt();
                    System.out.print("Enter hours worked: ");
                    int hours = sc.nextInt();
                    management.trackVolunteerHours(volunteerHoursId, hours);
                    break;
                case 6:
                    System.out.println("Enter staff ID: ");
                    int staffScheduleId = sc.nextInt();
                    System.out.println("Enter volunteer ID: ");
                    int volunteerScheduleId = sc.nextInt();
                    System.out.println("Enter task ID: ");
                    int taskScheduleId = sc.nextInt();
                    System.out.println("Enter start date (YYYY-MM-DD): ");
                    String startDate = sc.next();
                    System.out.println("Enter end date (YYYY-MM-DD): ");
                    String endDate = sc.next();
                    management.updateSchedule(staffScheduleId, volunteerScheduleId, taskScheduleId, Date.valueOf(startDate), Date.valueOf(endDate));
                    break;
                	
                case 7:
                	System.out.println("Displaying the schedules:");
                	management.viewSchedule();
                	break;
                
                case 8:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");          
        
            }
            
        }
        sc.close();
        }
        catch(SQLException e) {
		
			System.out.println("not connected");
    }
      
    }
}