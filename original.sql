PGDMP     "                    w           support     11.4 (Ubuntu 11.4-1.pgdg18.04+1)    11.3     I           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                       false            J           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                       false            K           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                       false            L           1262    24904    support    DATABASE     y   CREATE DATABASE support WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';
    DROP DATABASE support;
             postgres    false            �            1259    24905    tickets    TABLE     �  CREATE TABLE public.tickets (
    "ID" bigint NOT NULL,
    "Created" date,
    "Completed" date,
    "Modified" date,
    "Name" text,
    "Assignee" character varying(70),
    "Assignee_Email" character varying(100),
    "Start_Date" date,
    "End_Date" date,
    "Tags" character varying(100),
    "Notes" text,
    "Projects" character varying(100),
    "Parent_Task" character varying(100),
    "Site" character varying(50),
    "Ticket_Time" character varying(50),
    "Topic" character varying(100),
    "Ticket_Input" character varying(100),
    "Start_DateTime" timestamp without time zone,
    "End_DateTime" timestamp without time zone
);
    DROP TABLE public.tickets;
       public         postgres    false            F          0    24905    tickets 
   TABLE DATA               	  COPY public.tickets ("ID", "Created", "Completed", "Modified", "Name", "Assignee", "Assignee_Email", "Start_Date", "End_Date", "Tags", "Notes", "Projects", "Parent_Task", "Site", "Ticket_Time", "Topic", "Ticket_Input", "Start_DateTime", "End_DateTime") FROM stdin;
    public       postgres    false    196   	       �
           2606    24912    tickets tickets_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_pkey PRIMARY KEY ("ID");
 >   ALTER TABLE ONLY public.tickets DROP CONSTRAINT tickets_pkey;
       public         postgres    false    196            F      x������ � �     