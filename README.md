# Asana2Postgres [![Build Status](https://travis-ci.com/AntonyLeons/Asana2Postgres.svg?token=iEHPmhnrfp4VatGpB9LT&branch=master)](https://travis-ci.com/AntonyLeons/Asana2Postgres) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/96c2e41e78b543b29558193bd883d111)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AntonyLeons/Asana2Postgres&amp;utm_campaign=Badge_Grade)
 
-  Create a database
- Ensure environment variables are set or coded

  This uses Metabase variables and an `ASANA_TOKEN` variable
  
- Run import then sync

## Compile with 

```
gradle jar
```
in respective folders.

## Run with

```
java -jar <NAME> <PROJECT_ID> <TABLE_NAME> <DATABASE>
```
`<PROJECT_ID> <TABLE_NAME> <DATABASE>` are optional and default is support tickets table, however these must be in order so a `<PROJECT_ID>` must be set to set a `<TABLE_NAME>`.
Database creation is NOT supported.
