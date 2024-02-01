-- create new user
CREATE USER brandedcompany IDENTIFIED BY brandedcompany;

-- grant priviledges
GRANT CONNECT, RESOURCE, DBA TO brandedcompany;