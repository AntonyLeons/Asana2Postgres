package com.sirisminerals.asana2postgres;

import com.asana.Client;
import com.asana.models.CustomField;
import com.asana.models.ResultBodyCollection;
import com.asana.models.Task;
import com.asana.models.User;
import com.asana.requests.CollectionRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws IOException {
        final Logger logger = Logger.getLogger(Main.class.getName());
        //config
        String project_id = "2760706195514";
        String db_user = "postgres";
        String db_pass = "BodyRail%8";
        String Auth_key = "0/1fe378c15de839054e06c60f8e78563f";
        String CSV_Filepath="C:\\Users\\aleons\\OneDrive - Sirius Minerals PLC\\Documents\\GitHub\\Asana2Postgres\\data.csv";
        String Database_path="/media/sf_Asana2Postgres/data.csv";


        // Database connection
        String db_url = "jdbc:postgresql://172.16.132.193:5432/support";
        Properties props = new Properties();
        props.setProperty("user", db_user);
        props.setProperty("password", db_pass);
        props.setProperty("ssl", "false");


            logger.log(Level.INFO, "Connected " + logger.getName());
            String offset = null;
            final Client client = Client.accessToken(Auth_key);
            File file = new File(CSV_Filepath);
            FileWriter writer = new FileWriter(file, false);
            List<String> fields = new ArrayList<String>(Arrays.asList("id", "created_at", "due_on", "completed", "modified_at", "name", "notes", "assignee", "assignee.name", "assignee.email", "tags", "custom_fields", "custom_fields.enum_value"));
            List<String> expand = new ArrayList<String>(Arrays.asList("id", "created_at", "due_on", "completed", "modified_at", "name", "notes", "assignee", "assignee.name", "assignee.email", "tags", "custom_fields", "custom_fields.enum_value"));

            while (true) {
                CollectionRequest tasks = client.tasks.findByProject(project_id).option("limit", 100).option("page_size", 100).option("offset", offset).option("fields", fields).option("expand", expand);
                CollectionRequest assignee = client.users.findAll();
                ResultBodyCollection<User> userResult = assignee.executeRaw();
                ResultBodyCollection<Task> result = tasks.executeRaw();
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
                        created_at = i.createdAt.toString();
                    }
                    if (i.completedAt != null) {
                        completed_at = i.createdAt.toString();
                    }
                    if (i.modifiedAt != null) {
                        modified_at = i.createdAt.toString();
                    }
                    if (i.modifiedAt != null) {
                        due_on = i.modifiedAt.toString();
                    }
                    writer.write(i.id + '~' + created_at + '~' + completed_at + '~' + i.completed + '~' + modified_at + '~'  + "name"+ '~' + assignee_name + '~' + assignee_email + '~' + due_on + '~' +"notes"+ '~' + site + '~' + ticket_time + '~' + topic + '~' + input + "\n");

                }
                if (result.nextPage != null) {
                    logger.log(Level.INFO, "Next Page " + logger.getName());
                    offset = result.nextPage.offset;
                } else {
                    break;
                }

            }
            writer.close();
        try (Connection conn = DriverManager.getConnection(db_url, props)) {
            Statement statement = conn.createStatement();
            String sql = "COPY tickets FROM \'"+Database_path+"\' DELIMITERS \'~\' CSV encoding 'UTF-8'";

            System.out.println(statement.executeUpdate(sql));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to db" + logger.getName());
            e.printStackTrace();
        }

    }
}
