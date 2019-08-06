# Asana2Postgres [![Build Status](https://travis-ci.com/AntonyLeons/Asana2Postgres.svg?token=iEHPmhnrfp4VatGpB9LT&branch=master)](https://travis-ci.com/AntonyLeons/Asana2Postgres)
 
0/1fe378c15de839054e06c60f8e78563f
project ID 2760706195514
https://app.asana.com/api/1.0/tasks?project=2760706195514&limit=100&opt_fields=completed_at,due_on,name,notes,projects,created_at,modified_at,assignee,parent

/projects/2760706195514/tasks?opt_fields=id,assignee,assignee_status,created_at,completed,completed_at,due_on,due_at,external,followers,hearted,hearts,modified_at,name,notes,num_hearts,projects,parent,workspace,memberships&limit=100

 String url = "jdbc:postgresql://172.16.132.193:5432/support";
        Properties props = new Properties();
        props.setProperty("user","postgres");
        props.setProperty("password","BodyRail%8");
        props.setProperty("ssl","false");
        try (Connection conn = DriverManager.getConnection(url, props)) {
        }

CREATE TABLE public.tickets
(
    "ID" bigint NOT NULL,
    "Created_Date" timestamp(4) with time zone,
    "Completed_At" timestamp(4) with time zone,
    "Completed" boolean,
    "Modified" timestamp(4) with time zone,
    "Name" text COLLATE pg_catalog."default",
    "Assignee" character varying(70) COLLATE pg_catalog."default",
    "Assignee_Email" character varying(100) COLLATE pg_catalog."default",
    "Due_On" date,
    "Notes" text COLLATE pg_catalog."default",
    "Site" character varying(50) COLLATE pg_catalog."default",
    "Ticket_Time" character varying(50) COLLATE pg_catalog."default",
    "Topic" character varying(100) COLLATE pg_catalog."default",
    "Ticket_Input" character varying(100) COLLATE pg_catalog."default",
    CONSTRAINT support_pkey PRIMARY KEY ("ID")
)