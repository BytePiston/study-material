DROP TABLE IF EXISTS DEAL;
CREATE TABLE DEAL (ID VARCHAR PRIMARY KEY, TITLE VARCHAR , DESCRIPTION VARCHAR, DEAL_PRICE_AMOUNT DOUBLE PRECISION, NORMAL_PRICE_AMOUNT DOUBLE PRECISION, REDEEMABLE_FROM DATE, REDEEMABLE_TO DATE, VERSION INTEGER);

INSERT INTO DEAL VALUES ('LEVIS_501', 'Levis 501', 'Traditional Jeans', 33.00, 70.00, '2016-12-24', CURRENT_DATE, 0);