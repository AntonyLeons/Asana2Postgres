import com.asana.Client;
import com.asana.models.CustomField;
import com.asana.models.ResultBodyCollection;
import com.asana.models.Task;
import com.asana.requests.CollectionRequest;

import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static List<String> customColumn = new ArrayList<>();

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
        client.headers.put("Asana-Enable", "string_ids"); // remove after 2020-02-11
        List<String> fields = new ArrayList<>(Arrays.asList("gid", "created_at", "due_on", "completed_at", "completed", "modified_at", "name", "notes", "assignee", "assignee.name", "assignee.email", "tags", "custom_fields", "custom_fields.enum_value", "custom_fields.resource_subtype", "custom_fields.text_value", "custom_fields.number_value", "custom_fields.name"));
        List<String> expand = new ArrayList<>(Arrays.asList("gid", "created_at", "due_on", "completed_at", "completed", "modified_at", "name", "notes", "assignee", "assignee.name", "assignee.email", "tags", "custom_fields", "custom_fields.enum_value", "custom_fields.resource_subtype", "custom_fields.text_value", "custom_fields.number_value", "custom_fields.name"));

        try (Connection conn = DriverManager.getConnection(db_url, props)) {
            logger.log(Level.INFO, "Connected " + logger.getName());
            createTable(conn, client, project_id, table, fields, expand);
            Statement delete = conn.createStatement();
            String deleteSQL = "TRUNCATE " + table;
            delete.execute(deleteSQL);
            while (true) {
                SortedMap<String, String> customfields = new TreeMap<>();
                CollectionRequest tasks = client.tasks.findByProject(project_id).option("limit", 100).option("page_size", 100).option("offset", offset).option("fields", fields).option("expand", expand);
                ResultBodyCollection<Task> result = tasks.executeRaw();
                for (Task i : result.data) {
//                    String assignee_id = "";
                    Timestamp created_at = null;
                    Timestamp completed_at = null;
                    Timestamp modified_at = null;
                    String name = "";
                    String notes = "";
                    Timestamp due_on = null;
                    String assignee_name = "";
                    String assignee_email = "";
                    String tags = "";
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
                    String sql = "INSERT INTO " + table + "(\"ID\", \"Created_Date\", \"Completed_At\", \"Completed\", \"Modified\", \"Name\", \"Assignee\", \"Assignee_Email\", \"Due_On\", \"Tags\", \"Notes\")" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);";
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
                        if (customColumn.contains(entry.getKey())) {
                            String sql2 = "UPDATE " + table + " SET \"" + entry.getKey() + "\"=? WHERE \"ID\"=?";
                            PreparedStatement ps2 = conn.prepareStatement(sql2);
                            ps2.setString(1, entry.getValue());
                            ps2.setString(2, i.gid);
                            ps2.execute();
                        }
                    }
                    counter++;
                }
                if (result.nextPage != null) {
                    logger.log(Level.INFO, "Next Page " + logger.getName());
                    offset = result.nextPage.offset;
                } else {
                    System.out.println(counter + " tasks added");
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

    private static void createTable(Connection conn, Client client, String project_id, String table, List<String> fields, List<String> expand) throws IOException, SQLException {
        String sql = "DROP TABLE IF EXISTS " + table + ";CREATE TABLE " + table +
                "(" +
                "    \"ID\" character varying(31) PRIMARY KEY  NOT NULL," +
                "    \"Created_Date\" timestamp(4) with time zone," +
                "    \"Completed_At\" timestamp(4) with time zone," +
                "    \"Completed\" boolean," +
                "    \"Modified\" timestamp(4) with time zone," +
                "    \"Name\" text ," +
                "    \"Assignee\" character varying(70) ," +
                "    \"Assignee_Email\" character varying(100) ," +
                "    \"Due_On\" date," +
                "    \"Tags\" character varying," +
                "    \"Notes\" text )";
        Statement create = conn.createStatement();
        create.execute(sql);
        CollectionRequest tasks = client.tasks.findByProject(project_id).option("limit", 1).option("page_size", 1).option("fields", fields).option("expand", expand);
        ResultBodyCollection<Task> result = tasks.executeRaw();
        Task i = result.data.get(0);
        Iterator<CustomField> listIterator = i.customFields.iterator();
        while (listIterator.hasNext()) {
            CustomField a = listIterator.next();
            String columnName = a.name.replace(' ', '_');
            customColumn.add(columnName);
            String sql2 = "ALTER TABLE " + table +
                    " ADD COLUMN \"" + columnName + "\" VARCHAR";
            create.execute(sql2);
        }
    }
}
