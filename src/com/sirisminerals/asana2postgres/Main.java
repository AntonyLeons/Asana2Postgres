package com.sirisminerals.asana2postgres;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.*;
import java.time.*;
import com.asana.*;
import com.asana.models.*;
import com.asana.requests.CollectionRequest;
import com.google.api.client.util.DateTime;
import org.apache.commons.lang3.StringUtils;

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
            List<String> fields =new ArrayList<String>(Arrays.asList("id","created_at","due_on","completed","modified_at","name","notes","assignee","assignee.name","assignee.email","tags","custom_fields"));
            List<String> expand = new ArrayList<String>(Arrays.asList("id","created_at","due_on","completed","modified_at","name","notes","assignee","assignee.name","assignee.email","tags","custom_fields"));
            while(true) {
                CollectionRequest tasks =client.tasks.findByProject(project_id).option("limit", 100).option("page_size", 100).option("offset", offset).option("fields",fields).option("expand",expand);
                ResultBodyCollection<Task> result= tasks.executeRaw();
            for (Task i:result.data) {
               i= Cleaninputs(i);
//                Statement stmt= conn.createStatement();
//                Optional<DateTime> createdAt = Optional.ofNullable(i.createdAt);
//                Optional<DateTime> ModifiedaAt=Optional.ofNullable(i.modifiedAt);
//                        createdAt.isPresent();
                Iterator<CustomField> listIterator = i.customFields.iterator();
//                writer.write(i.id+','+i.createdAt+','+i.completed+','+i.modifiedAt+','+i.name+','+i.assignee.name+','+i.assignee.email+','+i.startOn+','+i.completedAt+','+i.tags+','+i.notes+','+i.projects+','+i.parent+','+i.customFields+"\n");
                writer.write(i.id+','+i.createdAt+','+i.dueOn+','+i.completed+','+i.modifiedAt+','+i.name+','+','+i.assignee+','+i.tags+','+listIterator.next().enumValue.enabled+','+listIterator.next().enumValue.name+','+listIterator.next().description+','+listIterator.next().name+"\n");
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
                logger.log(Level.INFO, "Next Page " + logger.getName());
                offset = result.nextPage.offset;
            } else {
                break;
            }

        }



        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to db" + logger.getName());
            e.printStackTrace();
        }

    }
    public static Task Cleaninputs(Task l)
    {
        if(l.name==null)
        {
            l.name="";
        }
        if(l.createdAt==null)
        {
            l.createdAt=DateTime.parseRfc3339("1999-01-01T12:00:00-00:00");
        }
        if(l.assignee.name==null)
        {
            l.assignee.name="";
        }
        

        return l;
    }
}
