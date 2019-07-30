package com.sirisminerals.asana2postgres;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.*;

import com.asana.*;
import com.asana.models.*;
import com.google.api.client.util.DateTime;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        final Logger logger = Logger.getLogger(Main.class.getName());
        //config
        String project_id="2760706195514";
        String db_user="postgres";
        String db_pass="BodyRail%8";
        String Auth_key="0/1fe378c15de839054e06c60f8e78563f";

        // Database connection
        String db_url = "jdbc:postgresql://172.16.132.193:5432/support";
        Properties props = new Properties();
        props.setProperty("user",db_user);
        props.setProperty("password",db_pass);
        props.setProperty("ssl","false");
        try (Connection conn = DriverManager.getConnection(db_url, props)) {
            logger.log(Level.INFO, "Connected " + logger.getName());
        String offset =null;
        final Client client = Client.accessToken(Auth_key);
        while(true) {
            ResultBodyCollection<Task> result=client.tasks.findByProject(project_id).option("limit", 100).option("page_size", 100).option("offset", offset).executeRaw();
            for (Task i:result.data) {
                Statement stmt= conn.createStatement();

                String sql = "INSERT INTO public.tickets(\"ID\", \"Created\", \"Completed\", \"Modified\", \"Name\", \"Assignee\", \"Assignee_Email\", \"Start_Date\", \"End_Date\", \"Tags\", \"Notes\", \"Projects\", \"Parent_Task\", \"Site\", \"Ticket_Time\", \"Topic\", \"Ticket_Input\", \"Start_DateTime\", \"End_DateTime\") " +
                        "VALUES (\'"+i.id+"\', \'"+i.createdAt+"\', \'"+i.completed+"\',\'"+i.modifiedAt+"\',\'"+i.name+"\',\'"+i.assignee+"\',\'"+i.assigneeStatus+"\',\'"+i.startOn+"\',\'"+i.completedAt+"\',\'"+i.tags+"\',\'"+i.notes+"\',\'"+i.projects+"\',\'"+i.parent+"\',\'"+i.customFields+"\',\'"+i.customFields+"\',\'"+i.customFields+"\',\'"+i.customFields+"\',\'"+i.customFields+"\',\'"+i.customFields+"\');";
                stmt.executeUpdate(sql);
            }
            if (result.nextPage != null) {
                offset = result.nextPage.offset;
            } else {
                break;
            }
        }



        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed " + logger.getName());
            e.printStackTrace();
        }
    }
}
