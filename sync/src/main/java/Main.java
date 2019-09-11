import com.asana.Client;
import com.asana.models.CustomField;
import com.asana.models.ResultBodyCollection;
import com.asana.models.Task;
import com.asana.requests.CollectionRequest;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws IOException {
        final Logger logger = Logger.getLogger(Main.class.getName());
        //config
        String project_id = "2760706195514"; //can retrieve this from url
        String db_user = System.getenv("MB_DB_USER");
        String db_pass = System.getenv("MB_DB_PASS");
        String ip_address = System.getenv("MB_DB_HOST");
        String port = ':' + System.getenv("MB_DB_PORT");
        String db = '/' + System.getenv("MB_DB_DBNAME");
        String table = "asana.tickets"; //Include schema
        String Auth_key = System.getenv("ASANA_TOKEN");

        if (args.length == 1) {
            project_id = args[0].strip();
            table = "asana.T" + project_id;
        } else if (args.length == 2) {
            project_id = args[0].strip();
            table = "asana." + args[1].strip();
        } else if (args.length == 3) {
            project_id = args[0].strip();
            table = "asana." + args[1].strip();
            db = args[2].strip();
        }

        // Database connection
        String db_url = "jdbc:postgresql://" + ip_address + port + db;
        Properties props = new Properties();
        props.setProperty("user", db_user);
        props.setProperty("password", db_pass);
        props.setProperty("ssl", "false");

        int counter = 0;
        String offset = null;
        final Client client = Client.accessToken(Auth_key);
        client.headers.put("Asana-Enable", "string_ids,new_sections"); // remove after 2020-02-11
        List<String> fields = new ArrayList<>(Arrays.asList("gid", "created_at", "due_on", "completed_at", "completed", "modified_at", "name", "notes", "assignee", "assignee.name", "assignee.email", "tags", "custom_fields", "custom_fields.enum_value", "custom_fields.resource_subtype", "custom_fields.text_value", "custom_fields.number_value", "custom_fields.name"));
        List<String> expand = new ArrayList<>(Arrays.asList("gid", "created_at", "due_on", "completed_at", "completed", "modified_at", "name", "notes", "assignee", "assignee.name", "assignee.email", "tags", "custom_fields", "custom_fields.enum_value", "custom_fields.resource_subtype", "custom_fields.text_value", "custom_fields.number_value", "custom_fields.name"));

        try (Connection conn = DriverManager.getConnection(db_url, props)) {
            logger.log(Level.INFO, "Connected " + logger.getName());

            Statement getModified = conn.createStatement();
            String getModifiedSQL = "SELECT MAX(\"Modified\") FROM " + table;
            ResultSet rs = getModified.executeQuery(getModifiedSQL);
            String max = "";
            while (rs.next()) {
                Timestamp maxModified = rs.getTimestamp("max");
                max = new SimpleDateFormat("yyyy-MM-dd").format(maxModified);
            }
            
            while (true) {
                SortedMap<String, String> customfields = new TreeMap<>();
                CollectionRequest search = client.tasks.searchInWorkspace("2740660799089").query("modified_on.after", max).query("projects.any", project_id).option("limit", 100).option("page_size", 100).option("offset", offset).option("fields", fields).option("expand", expand);
                ResultBodyCollection<Task> result = search.executeRaw();
                for (Task i : result.data) {
//                    String assignee_id = "";
                    Timestamp created_at = null;
                    Timestamp completed_at = null;
                    Timestamp modified_at = null;
                    String name = "";
                    String tags = "";
                    String notes = "";
                    Timestamp due_on = null;
                    String assignee_name = "";
                    String assignee_email = "";
                    if (i.assignee != null) {
//                        assignee_id = i.assignee.id;
                        assignee_name = i.assignee.name;
                        assignee_email = i.assignee.email;
                    }
                    for (CustomField a : i.customFields) {
                        customfields.put(a.name.replace(' ', '_'), getCustom(a));
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
                    if (i.tags != null) {
                        tags = i.tags.toString();
                    }
                    if (i.notes != null) {
                        notes = i.notes;
                    }
                    String sql = "INSERT INTO " + table + "(\"ID\", \"Created_Date\", \"Completed_At\", \"Completed\", \"Modified\", \"Name\", \"Assignee\", \"Assignee_Email\", \"Due_On\",\"Tags\", \"Notes\")" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?) " +
                            "ON CONFLICT (\"ID\") DO UPDATE SET " +
                            "\"Created_Date\" = excluded.\"Created_Date\", \"Completed_At\" = excluded.\"Completed_At\", \"Completed\" = excluded.\"Completed\", \"Modified\"=excluded.\"Modified\", \"Name\"=excluded.\"Name\", \"Assignee\"=excluded.\"Assignee\", \"Assignee_Email\"=excluded.\"Assignee_Email\", \"Due_On\"=excluded.\"Due_On\",\"Tags\"=excluded.\"Tags\", \"Notes\"=excluded.\"Notes\"";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, i.gid);
                    ps.setTimestamp(2, created_at);
                    ps.setTimestamp(3, completed_at);
                    ps.setBoolean(4, i.completed);
                    ps.setTimestamp(5, modified_at);
                    ps.setString(6, name);
                    ps.setString(7, assignee_name);
                    ps.setString(8, assignee_email);
                    ps.setTimestamp(9, due_on);
                    ps.setString(10, tags);
                    ps.setString(11, notes);
                    ps.execute();

                    for (Map.Entry<String, String> entry : customfields.entrySet()) {
                        String sql2 = "UPDATE " + table + " SET \"" + entry.getKey() + "\"=? WHERE \"ID\"=?";
                        PreparedStatement ps2 = conn.prepareStatement(sql2);
                        ps2.setString(1, entry.getValue());
                        ps2.setString(2, i.gid);
                        ps2.execute();
                    }
                    counter++;
                }
                if (result.nextPage != null) {
                    logger.log(Level.INFO, "Next Page " + logger.getName());
                    offset = result.nextPage.offset;
                } else {
                    System.out.println(counter + " tasks added or updated");
                    break;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to db " + logger.getName());
            e.printStackTrace();
            System.exit(-1);
        }
    }


    private static String getCustom(CustomField a) {
        if (a == null) {
            return "";
        } else if ("enum".equals(a.resourceSubtype) && a.enumValue != null) {
            return a.enumValue.name;
        } else if ("text".equals(a.resourceSubtype) && a.textValue != null) {
            return a.textValue;
        } else if ("number".equals(a.resourceSubtype) && a.numberValue != null) {
            return a.numberValue;
        } else return "";
    }
}
