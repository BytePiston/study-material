-- org units
INSERT INTO ORGUNIT VALUES (1, 'Accounting')

-- employees
INSERT INTO EMPLOYEE SET ID=1, LAST_NAME='Smith', FIRST_NAME='Claus', EMAIL='c.smith@demo.biz'

-- Stored procedures
DROP ALIAS IF EXISTS JAVA_RANDOM
CREATE ALIAS JAVA_RANDOM FOR "java.lang.Math.random"

DROP ALIAS IF EXISTS JAVA_POWER
CREATE ALIAS JAVA_POWER FOR "java.lang.Math.pow"
