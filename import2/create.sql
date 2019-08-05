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