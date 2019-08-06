import com.asana.Client;
import com.asana.models.CustomField;
import com.asana.models.ResultBodyCollection;
import com.asana.models.Task;
import com.asana.requests.CollectionRequest;
import com.google.api.client.util.DateTime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
//SYNC
    public static void main(String[] args) throws IOException {
        final Logger logger = Logger.getLogger(Main.class.getName());
        //config
        String project_id = "2760706195514";
        String db_user = System.getenv("db_user");
        String db_pass = System.getenv("db_pass");
        String Auth_key = System.getenv("TOKEN");
        String CSV_Filepath = System.getenv("Filepath");
        String Database_path = System.getenv("db_Filepath");


        // Database connection
        String db_url = "jdbc:postgresql://"+System.getenv("db_address")+"/support";
        Properties props = new Properties();
        props.setProperty("user", db_user);
        props.setProperty("password", db_pass);
        props.setProperty("ssl", "false");


        logger.log(Level.INFO, "Connected " + logger.getName());
        try (Connection conn = DriverManager.getConnection(db_url, props)) {
            Statement getModified = conn.createStatement();
            String getModifedSQL = "SELECT MAX(\"Modified\") FROM public.tickets";
            ResultSet rs =getModified.executeQuery(getModifedSQL);
            String max ="";
            while(rs.next()) {
              Timestamp maxModified =  rs.getTimestamp("Modified");
              max =maxModified.toString();
            }



        String offset = null;
        final Client client = Client.accessToken(Auth_key);

        List<String> fields = new ArrayList<>(Arrays.asList("id", "created_at", "due_on", "completed_at", "completed", "modified_at", "assignee", "assignee.name", "assignee.email", "tags", "custom_fields", "custom_fields.enum_value"));
        List<String> expand = new ArrayList<>(Arrays.asList("id", "created_at", "due_on", "completed_at", "completed", "modified_at", "assignee", "assignee.name", "assignee.email", "tags", "custom_fields", "custom_fields.enum_value"));
        List<String> fields2 = new ArrayList<>(Arrays.asList("id", "name", "notes"));
        List<String> expand2 = new ArrayList<>(Arrays.asList("id", "name", "notes"));
        List<String> search_op = new ArrayList<>(Arrays.asList("id", "name", "notes"));

        while (true) {
            CollectionRequest search =client.tasks.searchInWorkspace("2740660799089").option("projects.any","2760706195514").option("modified_on.after",max).option("limit", 100).option("page_size", 100).option("offset", offset).option("fields", fields).option("expand", expand);
            ResultBodyCollection<Task> result = search.executeRaw();
            for (Task i : result.data) {
                String assignee_id = "";
                String created_at = "";
                String completed_at = "";
                String modified_at = "";
                String due_on = "";
                String assignee_name = "";
                String assignee_email = "";
                String site = "";
                String ticket_time = "";
                String topic = "";
                String input = "";

                if (i.assignee != null) {
                    assignee_id = i.assignee.id;
                    assignee_name = i.assignee.name;
                    assignee_email = i.assignee.email;
                }
                Iterator<CustomField> listIterator = i.customFields.iterator();
                CustomField a = listIterator.next();

                if (a != null && a.enumValue != null) {
                    site = a.enumValue.name;
                }
                a = listIterator.next();
                if (a != null && a.enumValue != null) {
                    ticket_time = a.enumValue.name;
                }
                a = listIterator.next();
                if (a != null && a.enumValue != null) {
                    topic = a.enumValue.name;
                }
                a = listIterator.next();
                if (a != null && a.enumValue != null) {
                    input = a.enumValue.name;
                }
                if (i.createdAt != null) {
                    created_at = String.valueOf(i.createdAt);
                }
                if (i.completedAt != null) {
                    completed_at = String.valueOf(i.completedAt);
                }
                if (i.modifiedAt != null) {
                    modified_at = String.valueOf(i.modifiedAt);
                }
                if (i.dueOn != null) {
                    due_on = String.valueOf(i.dueOn);
                }
                Statement statement = conn.createStatement();
                String sql = "INSERT INTO public.tickets(" +
                        "\"ID\", \"Created_Date\", \"Completed_At\", \"Completed\", \"Modified\", \"Name\", \"Assignee\", \"Assignee_Email\", \"Due_On\", \"Notes\", \"Site\", \"Ticket_Time\", \"Topic\", \"Ticket_Input\")" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//                INSERT INTO tbl_Employee
//                VALUES (7,'Ramu')
//                ON CONFLICT (EmpID)
//                        DO UPDATE SET EmpName = Excluded.EmpName;
//                System.out.println(statement.executeUpdate(sql));
            }
            if (result.nextPage != null) {
                logger.log(Level.INFO, "Next Page " + logger.getName());
                offset = result.nextPage.offset;
            } else {
                break;
            }

        }

            Statement statement = conn.createStatement();
            String sql = "COPY tickets FROM \'" + Database_path + "\' DELIMITERS \'~\' CSV encoding 'UTF-8'";
            offset = null;
            System.out.println(statement.executeUpdate(sql));
            while (true) {
                CollectionRequest tasks = client.tasks.findByProject(project_id).option("limit", 100).option("page_size", 100).option("offset", offset).option("fields", fields2).option("expand", expand2);
                ResultBodyCollection<Task> result = tasks.executeRaw();
                for (Task i : result.data) {
                    String name = "";
                    String notes = "";

                    if (i.name != null) {
                        name = i.name;
                    }
                    if (i.notes != null) {
                        notes = i.notes;
                    }
                    String sql2 = "UPDATE public.tickets SET \"Name\"=?,\"Notes\"=? WHERE \"ID\"=" + i.id;
                    PreparedStatement ps = conn.prepareStatement(sql2);
                    ps.setString(1, name);
                    ps.setString(2, notes);
                    ps.execute();
                }
                if (result.nextPage != null) {
                    logger.log(Level.INFO, "UPDATE Next Page " + logger.getName());
                    offset = result.nextPage.offset;
                } else {
                    break;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to db" + logger.getName());
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
