import com.asana.Client;
import com.asana.models.CustomField;
import com.asana.models.ResultBodyCollection;
import com.asana.models.Task;
import com.asana.requests.CollectionRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws IOException {
        final Logger logger = Logger.getLogger(Main.class.getName());
        //config
        String project_id = "2760706195514";
        String db_user = System.getenv("db_user");
        String db_pass = System.getenv("db_pass");
        String ip_address= System.getenv("db_address");
        String port=System.getenv("db_port"); // append with : at start
        String table ="/support"; // append with / at start
        String Auth_key = System.getenv("TOKEN");


        // Database connection
        String db_url = "jdbc:postgresql://" + ip_address + port + table;
        Properties props = new Properties();
        props.setProperty("user", db_user);
        props.setProperty("password", db_pass);
        props.setProperty("ssl", "false");


        String offset = null;
        final Client client = Client.accessToken(Auth_key);
        List<String> fields = new ArrayList<>(Arrays.asList("id", "created_at", "due_on", "completed_at", "completed", "modified_at", "name", "notes", "assignee", "assignee.name", "assignee.email", "tags", "custom_fields", "custom_fields.enum_value"));
        List<String> expand = new ArrayList<>(Arrays.asList("id", "created_at", "due_on", "completed_at", "completed", "modified_at", "name", "notes", "assignee", "assignee.name", "assignee.email", "tags", "custom_fields", "custom_fields.enum_value"));
        try (Connection conn = DriverManager.getConnection(db_url, props)) {
            logger.log(Level.INFO, "Connected " + logger.getName());
            Statement getModified = conn.createStatement();
            String getModifiedSQL = "SELECT MAX(\"Modified\") FROM public.tickets";
            ResultSet rs =getModified.executeQuery(getModifiedSQL);
            String max ="";
            while(rs.next()) {
                Timestamp maxModified =  rs.getTimestamp("max");
                max = new SimpleDateFormat("yyyy-MM-dd").format(maxModified);
            }
            while (true) {
               // CollectionRequest search =client.tasks.searchInWorkspace("2740660799089").option("projects.any","2760706195514").option("modified_on.after",max).option("limit", 100).option("page_size", 100).option("offset", offset).option("fields", fields).option("expand", expand);
                 CollectionRequest search =client.tasks.searchInWorkspace("2740660799089").query("modified_on.after",max).option("limit", 100).option("page_size", 100).option("offset", offset).option("fields", fields).option("expand", expand);
                ResultBodyCollection<Task> result = search.executeRaw();
                for (Task i : result.data) {
                    String assignee_id = "";
                    Timestamp created_at = null;
                    Timestamp completed_at = null;
                    Timestamp modified_at = null;
                    String name = "";
                    String notes = "";
                    Timestamp due_on = null;
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
                        created_at = Timestamp.from(Instant.ofEpochMilli(i.createdAt.getValue()));
                    }
                    if (i.completedAt != null) {
                        completed_at = Timestamp.from(Instant.ofEpochMilli(i.completedAt.getValue()));
                    }
                    if (i.modifiedAt != null) {
                        modified_at = Timestamp.from(Instant.ofEpochMilli(i.modifiedAt.getValue()));
                    }
                    if (i.dueOn != null) {
                        due_on = Timestamp.from(Instant.ofEpochMilli(i.dueOn.getValue()));
                    }
                    if (i.name != null) {
                        name = i.name;
                    }
                    if (i.notes != null) {
                        notes = i.notes;
                    }
                    String sql = "INSERT INTO public.tickets(\"ID\", \"Created_Date\", \"Completed_At\", \"Completed\", \"Modified\", \"Name\", \"Assignee\", \"Assignee_Email\", \"Due_On\", \"Notes\", \"Site\", \"Ticket_Time\", \"Topic\", \"Ticket_Input\")" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                            "ON CONFLICT (\"ID\") DO UPDATE SET " +
                            "\"Created_Date\" = excluded.\"Created_Date\", \"Completed_At\" = excluded.\"Completed_At\", \"Completed\" = excluded.\"Completed\", \"Modified\"=excluded.\"Modified\", \"Name\"=excluded.\"Name\", \"Assignee\"=excluded.\"Assignee\", \"Assignee_Email\"=excluded.\"Assignee_Email\", \"Due_On\"=excluded.\"Due_On\", \"Notes\"=excluded.\"Notes\", \"Site\"=excluded.\"Site\", \"Ticket_Time\"=excluded.\"Ticket_Time\",\"Topic\"=excluded.\"Topic\",\"Ticket_Input\"=excluded.\"Ticket_Input\"";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setBigDecimal(1, new BigDecimal(i.id));
                    ps.setTimestamp(2, created_at);
                    ps.setTimestamp(3, completed_at);
                    ps.setBoolean(4, i.completed);
                    ps.setTimestamp(5, modified_at);
                    ps.setString(6, name);
                    ps.setString(7, assignee_name);
                    ps.setString(8, assignee_email);
                    ps.setTimestamp(9, due_on);
                    ps.setString(10, notes);
                    ps.setString(11, site);
                    ps.setString(12, ticket_time);
                    ps.setString(13, topic);
                    ps.setString(14, input);
                    ps.execute();
                }
                if (result.nextPage != null) {
                    logger.log(Level.INFO, "Next Page " + logger.getName());
                    offset = result.nextPage.offset;
                } else {
                    break;
                }

            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to db " + logger.getName());
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
