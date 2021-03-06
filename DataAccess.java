import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/*
 * This class contains all the implementations of DataAccess methods for communicating with
 * the database.
 */
public class DataAccess{
    private static Connection connection = null;

    // Constructor
    DataAccess() {
        connectDatabase();
    }

    private static void connectDatabase() {

        //Check if there's postgresql driver is included when compile
        try{
            Class.forName("org.postgresql.Driver");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
            return;
        }

        //Connect to database
        try{
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://107.170.244.89:5432/datacenter", "datacenter", "thereisnopassword");
        }catch(SQLException e){
            System.out.println("Unable to connect to database. Check credentials and host.");
            System.exit(1);
        }
    }

    //Return "invalid", "provider", member", "suspended", "manager"
    public static String userVerification(int userID) {
        connectDatabase();
        String query = "SELECT * FROM organization WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userID); 
            ResultSet results = preparedStatement.executeQuery();

            if(results.next()) {
                String status = results.getString("status");
                return status;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return "invalid";
    }

    public boolean serviceVerification(int serviceID) {
        String query = "SELECT * FROM provider_directory WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, serviceID); 
            ResultSet results = preparedStatement.executeQuery();

            if(results.next())
                return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Changes a member's status to suspended in the database
    public boolean suspendMember(int memberID) {
        String query = "UPDATE organization set status = 'suspended' where id= ? and status = 'member'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memberID); 
            preparedStatement.executeUpdate();

            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Changes a member's standing from suspended back to member
    public boolean unsuspendMember(int memberID) {
        String query = "UPDATE organization set status = 'member' where id= ? and status = 'suspended'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memberID); 
            preparedStatement.executeUpdate();

            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Query that will add a new member or provider
    public boolean addOrganization(int userID, String name, String street, 
                                   String city, String state, int zipcode, String status) {
        String query = "INSERT INTO organization (id, name, street, city, state, zipcode, status)"
                        +" VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userID); 
            preparedStatement.setString(2, name); 
            preparedStatement.setString(3, street); 
            preparedStatement.setString(4, city); 
            preparedStatement.setString(5, state); 
            preparedStatement.setInt(6, zipcode); 
            preparedStatement.setString(7, status); 

            preparedStatement.executeUpdate();
            System.out.println("\033[0;32m Successfully added a new " + status + "\033[0m");
            return true;

        } catch(SQLException e) {
            System.out.println("Fail adding new member");
            e.printStackTrace();
        }
        return false;
    }

    // Updates an organizations information
    public boolean updateOrganization(int userID, String name, String street, 
                                   String city, String state, int zipcode, String status) {
        String query = "UPDATE organization SET name = ?, street = ?, city = ?, state = ?, zipcode = ?, status = ?"
                        +" WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name); 
            preparedStatement.setString(2, street); 
            preparedStatement.setString(3, city); 
            preparedStatement.setString(4, state); 
            preparedStatement.setInt(5, zipcode); 
            preparedStatement.setString(6, status); 
            preparedStatement.setInt(7, userID); 

            preparedStatement.executeUpdate();
            return true;

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    //A query that will create bill for member
    public boolean createBill(int memberID, int providerID, int serviceID, int nuConsultation, java.sql.Date provideddate, String comment) {
        String query = "INSERT INTO report (memberid, providerid, serviceid, numberconsultation, provideddate, comment)"
                        + " VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memberID); 
            preparedStatement.setInt(2, providerID); 
            preparedStatement.setInt(3, serviceID); 
            preparedStatement.setInt(4, nuConsultation); 
            preparedStatement.setDate(5, provideddate); 
            preparedStatement.setString(6, comment); 

            preparedStatement.executeUpdate();
            System.out.println("Successfully added a report");
            return true;

        } catch(SQLException e) {
            System.out.println("Fail adding report");
            e.printStackTrace();
        }
        return false;
    }

    //Query Provider directory and return it as a String
    public String directoryLookUp() {
        String query = "SELECT * FROM Provider_directory";
        StringBuilder directory = new StringBuilder();

        try { 
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet results = preparedStatement.executeQuery();

            while(results.next()) {
                directory.append("\n");
                directory.append("id: " + results.getInt("id") + "\n");
                directory.append("name: " + results.getString("name") + "\n");
                directory.append("fee: $" + results.getInt("fee") + "\n");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return directory.toString();
	}

	// Query database and return organization information for each provider or member in the database
    public List<Organization> getOrganizationList(String user)
    {
        String query = "SELECT * FROM organization WHERE status = ?";
        List<Organization> organizations = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user);
            ResultSet results = preparedStatement.executeQuery();

            while(results.next()) {
                Organization org = new Organization();
                org.Id = results.getInt("id");
                org.Name = results.getString("name");
                org.Street = results.getString("street");
                org.City = results.getString("city");
                org.State = results.getString("state");
                org.ZipCode = results.getInt("zipcode");
                org.Status = results.getString("status");
                organizations.add(org);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return organizations;
    }
	
    // This function removes a member or provider from the database
    public boolean removeOrganization(int memberID) {
        String query = "delete from organization WHERE  id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memberID);
            preparedStatement.executeUpdate();

            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Query that will return member bill as a string
    public String getMemberBill(int memberID) {
        String query = "SELECT r.provideddate, r.providerid, o.name, r.memberid, r.serviceid, pd.fee, r.comment "+
                        "FROM organization o JOIN report r ON o.id = r.memberid "+
                        "JOIN provider_directory pd ON r.serviceid = pd.id WHERE r.memberid = ?";

        StringBuilder directory = new StringBuilder();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memberID); 
            ResultSet results = preparedStatement.executeQuery();

            if(results.next()) {
                directory.append("Date service was provided: " + 
                                results.getDate("provideddate") + "\n");
                directory.append("Provider number: " + 
                                results.getInt("providerid") + "\n");
                directory.append("Member name: " + results.getString("name") + "\n");
                directory.append("Member number: " + results.getInt("memberid") + "\n");
                directory.append("Service code: " + results.getInt("serviceid") + "\n");
                directory.append("Fee to be paid: $" + results.getInt("fee") + "\n");
                directory.append("Comments: " + results.getString("comment") + "\n");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return directory.toString();
    }

    // Query the database and get a list of all service reports related to a provider or member
    public List<ServiceReport> getServiceReport(int memberId,String reportType){
        String query = "SELECT * FROM reportview1 WHERE ";
        if (reportType == "provider")
            query += "providerid = ?";
       else
            query += "memberid = ?";

        // Instantiate a list of type ServiceReport
        List<ServiceReport> services = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memberId);
            ResultSet results = preparedStatement.executeQuery();

            while(results.next()) {
                ServiceReport service = new ServiceReport();
                service.ServiceDate = results.getDate("provideddate");
                service.SerivceID = results.getInt("serviceid");
                service.ProviderName = results.getString("providername");
                service.ServiceName = results.getString("servicename");
                service.MemberId =  results.getInt("memberid");
                service.PatientName = results.getString("name");
                service.ProviderID =  results.getInt("providerid");
                service.Fee = results.getInt("fee");
                service.Comment = results.getString("comment");
                services.add(service);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    // Function to export members to a file along with their service reports
    public boolean exportMembersReportToFile() {

        List<Organization> memberRegistry = getOrganizationList("member");
        if (memberRegistry == null)                     // If the memberRegistry doesn't get set appropriately
            return false;

        try {
        String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        File dir = new File(System.getProperty("user.dir"),"Member Reports");
        if (!dir.exists()) // If the file doesn't exist in that path create it
                dir.mkdir();
        for (Organization org : memberRegistry) {          // For each member in the list perform these actions
            List<ServiceReport> memberReports = getServiceReport(org.Id,"member");       // Retrieve the services that member received
            if (!memberReports.isEmpty()){
                    String fileName = org.Name + "(" + date + ")";                      // Create the file name
                File file = new File(dir.getPath(), fileName + ".txt");  // Establish the path to the file

                if (!file.exists()) // If the file doesn't exist in that path create it
                    file.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(file)); // Allocate the BufferedWriter to write the information to the file
                // Information that only needs to be written to the file ONCE according to design requirements
                writer.write("Member name: " + org.Name);
                writer.newLine();
                writer.write("Member number: " + org.Id);
                writer.newLine();
                writer.write("Member address: " + org.Street + ", " + org.City + ", " + org.State + " " + org.ZipCode);
                writer.newLine();
                writer.newLine();
                writer.write("Services received: ");
                writer.newLine();
                // Information that needs to be written for each service report according to design requirements
                for (ServiceReport report : memberReports) {
                    writer.write("Service date: " + report.ServiceDate.toString());
                    writer.newLine();
                    writer.write("Provider name: " + report.ProviderName);
                    writer.newLine();
                    writer.write("Service name: " + report.ServiceName);
                    writer.newLine();
                    writer.write("--------------------------------------------------------");
                    writer.newLine();
                    writer.newLine();
                }
                writer.flush();
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    // Function to export providers to a file along with their service report information
    public boolean exportProviderServicesToFile() {

        List<Organization> providers = getOrganizationList("provider");
        if (providers == null)                     // If the providers doesn't get set appropriately
            return false;

        try {
        String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        File dir = new File(System.getProperty("user.dir"),"Provider Reports");
        if (!dir.exists()) // If the file doesn't exist in that path create it
                dir.mkdir();
        for (Organization org : providers) {          // For each member in the list perform these actions
            List<ServiceReport> providedServices = getServiceReport(org.Id,"provider");       // Retrieve the services that member received
            float totalFees = 0;
            for(ServiceReport service : providedServices) {
                totalFees += service.Fee;
            }
            if (!providedServices.isEmpty()){
                    String fileName = org.Name + "(" + date + ")";                      // Create the file name
                File file = new File(dir.getPath(), fileName + ".txt");  // Establish the path to the file

                if (!file.exists()) // If the file doesn't exist in that path create it
                    file.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(file)); // Allocate the BufferedWriter to write the information to the file
                // Information that only needs to be written to the file ONCE according to design requirements
                writer.write("Provider name: " + org.Name);
                writer.newLine();
                writer.write("Provider number: " + org.Id);
                writer.newLine();
                writer.write("Provider address: " + org.Street + ", " + org.City + ", " + org.State + " " + org.ZipCode);
                writer.newLine();
                writer.write("# of consultations: " + providedServices.size());
                writer.newLine();
                writer.write("Total fees for current week: " + "$"+totalFees);
                writer.newLine();
                writer.newLine();
                writer.write("Services Provided: ");
                writer.newLine();
                // Information that needs to be written for each service report according to design requirements
                for (ServiceReport service : providedServices) {
                    writer.write("Service date: " + service.ServiceDate.toString());
                    writer.newLine();
                    writer.write("Provider name: " + service.MemberId);
                    writer.newLine();
                    writer.write("Service code: " + service.SerivceID);
                    writer.newLine();
                    writer.write("Fee to pay: " + "$"+service.Fee);
                    writer.newLine();
                    writer.write("--------------------------------------------------------");
                    writer.newLine();
                    writer.newLine();
                }
                writer.flush();
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    // Function to remove all reports in the database related to a given memberID
    public boolean deleteAllMemberReport(int memberID) {
        String query = "DELETE FROM report WHERE memberID = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memberID); 
            preparedStatement.executeUpdate();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Function to remove all reports in the database related to a given providerID
    public boolean deleteAllProviderReport(int providerID) {
        String query = "DELETE FROM report WHERE providerID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, providerID); 
            preparedStatement.executeUpdate();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
