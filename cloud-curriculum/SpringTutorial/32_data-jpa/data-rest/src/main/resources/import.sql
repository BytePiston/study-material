-- org units
INSERT INTO ORGUNIT (ID, NAME) VALUES (1, 'Accounting')
INSERT INTO ORGUNIT (ID, NAME) VALUES (2, 'Controlling')
INSERT INTO ORGUNIT (ID, NAME) VALUES (3, 'Sales')
INSERT INTO ORGUNIT (ID, NAME) VALUES (4, 'Development')

-- employees
INSERT INTO EMPLOYEE (ID, FIRSTNAME, LASTNAME, EMAIL, ORGUNIT_ID) VALUES(1, 'Claus', 'Smith', 'c.smith@demo.biz', 1)
INSERT INTO EMPLOYEE (ID, FIRSTNAME, LASTNAME, EMAIL, ORGUNIT_ID) VALUES(2, 'Horst', 'Jefferson', 'h.j@demo.biz', 2)