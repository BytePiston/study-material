insert into CUSTOMER (ID, DISPLAY_NAME, DATE_OF_BIRTH, PHONE_NUMBER) values (1, 'Fritz', parsedatetime('1971', 'yyyy'), '+49-6227-74123')
insert into CUSTOMER (ID, DISPLAY_NAME, DATE_OF_BIRTH, PHONE_NUMBER) values (2, 'Heinz', parsedatetime('1961', 'yyyy'), '+49-6227-74124')

insert into SALES_ORDER(ID, TITLE, CUSTOMER_ID) values (1, 'Bushmills', 1)
insert into SALES_ORDER(ID, TITLE, CUSTOMER_ID) values (2, 'Talisker', 2)