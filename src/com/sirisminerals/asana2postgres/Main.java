package com.sirisminerals.asana2postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        String projectid="2760706195514";
        String db_user="postgres";
        String db_pass="BodyRail%8";
        String Auth_key="0/1fe378c15de839054e06c60f8e78563f";
        String Api_URL="https://app.asana.com/api/1.0";
        String Api_req="/projects/2760706195514/tasks?opt_fields=id,assignee,assignee_status,created_at,completed,completed_at,due_on,due_at,external,followers,hearted,hearts,modified_at,name,notes,num_hearts,projects,parent,workspace,memberships&limit=100";

        String db_url = "jdbc:postgresql://172.16.132.193:5432/support";
        Properties props = new Properties();
        props.setProperty("user",db_user);
        props.setProperty("password",db_pass);
        props.setProperty("ssl","false");
        try (Connection conn = DriverManager.getConnection(db_url, props)) {

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
