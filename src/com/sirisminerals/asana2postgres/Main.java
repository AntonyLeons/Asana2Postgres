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
try {
    String id=  check(i.id);
    String createdAt=check(String.valueOf(i.createdAt));
    String completed=check(String.valueOf(i.completed));
    String modifiedAt=  check(String.valueOf(i.modifiedAt));
    String name =check(i.name);
    String assignee_name=check(i.assignee.name);
    String assignee_Email=check(i.assignee.email);
        String Start_Date=check(String.valueOf(i.startOn));
        String End_Date=check(String.valueOf(i.completedAt));
        String tags =check(String.valueOf(i.tags));
        String notes=check(i.notes);
        String projects=check(String.valueOf(i.projects));
        String parent_name=check(i.parent.name);
        String custom_field=check(String.valueOf(i.customFields));
        String sql = "INSERT INTO public.tickets(\"ID\", \"Created\", \"Completed\", \"Modified\", \"Name\", \"Assignee\", \"Assignee_Email\", \"Start_Date\", \"End_Date\", \"Tags\", \"Notes\", \"Projects\", \"Parent_Task\", \"Site\", \"Ticket_Time\", \"Topic\", \"Ticket_Input\", \"Start_DateTime\", \"End_DateTime\") " +
            "VALUES (\'" + check(i.id) + "\', \'" + check(String.valueOf(i.createdAt)) + "\', \'" + check(String.valueOf(i.completed)) + "\',\'" + check(String.valueOf(i.modifiedAt)) + "\',\'" + check(i.name) + "\',\'" + check(i.assignee.name) + "\',\'" + check(i.assignee.email) + "\',\'" + check(String.valueOf(i.startOn)) + "\',\'" + check(String.valueOf(i.completedAt)) + "\',\'" + check(String.valueOf(i.tags)) + "\',\'" + check(i.notes) + "\',\'" + check(String.valueOf(i.projects)) + "\',\'" + check(i.parent.name) + "\',\'" + check(String.valueOf(i.customFields)) + "\',\'" + check(String.valueOf(i.customFields)) + "\',\'" + check(String.valueOf(i.customFields)) + "\',\'" + check(String.valueOf(i.customFields)) + "\',\'" + check(String.valueOf(i.customFields)) + "\',\'" + check(String.valueOf(i.customFields)) + "\');";
    stmt.executeUpdate(sql);
}
catch(Exception E)
                {

                }
            }
            if (result.nextPage != null) {
                logger.log(Level.INFO, "Next Page" + logger.getName());
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
    public static String check(String str)
    {
        if(str != null && !str.isEmpty())
        {
            return str;
        }
        else
            return "null";

    }
}
