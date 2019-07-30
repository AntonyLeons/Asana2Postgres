package com.sirisminerals.asana2postgres;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
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
        String db_url = "jdbc:postgresql://172.16.138.39:5432/support";
        Properties props = new Properties();
        props.setProperty("user",db_user);
        props.setProperty("password",db_pass);
        props.setProperty("ssl","false");
        try (Connection conn = DriverManager.getConnection(db_url, props)) {
            logger.log(Level.INFO, "Connected " + logger.getName());
        String offset =null;
        final Client client = Client.accessToken(Auth_key);
            File file = new File("C:\\Users\\aleons\\OneDrive - Sirius Minerals PLC\\Documents\\GitHub\\Asana2Postgres\\data.csv");
            FileWriter writer = new FileWriter(file, true);
        while(true) {
            ResultBodyCollection<Task> result=client.tasks.findByProject(project_id).option("limit", 100).option("page_size", 100).option("offset", offset).executeRaw();
            for (Task i:result.data) {
//                Statement stmt= conn.createStatement();
                writer.write(i.id+','+i.createdAt+','+i.completed+','+i.modifiedAt+','+i.name+','+i.assignee.name+','+i.assignee.email+','+i.startOn+','+i.completedAt+','+i.tags.toString()+','+i.notes+','+i.projects.toString()+','+i.parent.toString()+i.customFields.toString());
//                String sql = "INSERT INTO public.tickets(\"ID\", \"Created\", \"Completed\", \"Modified\", \"Name\", \"Assignee\", \"Assignee_Email\", \"Start_Date\", \"End_Date\", \"Tags\", \"Notes\", \"Projects\", \"Parent_Task\", \"Site\", \"Ticket_Time\", \"Topic\", \"Ticket_Input\", \"Start_DateTime\", \"End_DateTime\") " +
//                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
//                PreparedStatement ps =conn.prepareStatement(sql);
//                ps.setString(1,i.id);
//                ps.setString(2,i.createdAt.toString());
//                ps.setString(3,String.valueOf(i.completed));
//
//                ps.executeUpdate(sql);
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
