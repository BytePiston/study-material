package com.sap.sptutorial.odata2;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType.Type;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.sap.icd.odatav2.spring.messages.Message;
import com.sap.icd.odatav2.spring.messages.MessageBuffer;
import com.sap.sptutorial.odata2.model.SalesOrderProjection;

@Component
public class SalesOrderOperations implements ApplicationContextAware {

    private static int id = -1;

    private static int getId() {
        return id;
    }

    private static int incrementId() {
        return ++id;
    }

    private static ApplicationContext applicationContext;

    private static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private static void setStaticApplicationContext(
            ApplicationContext applicationContext) {
        if (SalesOrderOperations.applicationContext == null) {
            SalesOrderOperations.applicationContext = applicationContext;
        }
    }

    private static final String JPA_QUERY = "select o.ID, o.TITLE, c.DISPLAY_NAME "
            + "from SALES_ORDER o, CUSTOMER c where c.ID = ?1 and c.ID = o.CUSTOMER_ID";
    private static final String TEMPLATE_QUERY = "select o.ID as ORDER_ID, o.TITLE as ORDER_TITLE, "
            + "c.DISPLAY_NAME as CUSTOMER_NAME "
            + "from SALES_ORDER o, CUSTOMER c where c.ID = ? and c.ID = o.CUSTOMER_ID";

    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public void setEntityManagerFactory(
            EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private MessageBuffer messageBuffer;

    @Autowired
    public void setMessageBuffer(MessageBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    public SalesOrderOperations() {
        SalesOrderOperations.incrementId();
        System.out.println("SalesOrderOperations Instance: " + id);
        ApplicationContext applicationContext = SalesOrderOperations
                .getApplicationContext();
        if (applicationContext != null) {
            applicationContext.getAutowireCapableBeanFactory()
                    .autowireBean(this);
        }
    }

    @EdmFunctionImport(name = "SalesOrderProjectionJPA", returnType = @ReturnType(type = Type.COMPLEX, isCollection = true))
    public List<SalesOrderProjection> getSalesOrderProjectionJPA(
            @EdmFunctionImportParameter(name = "CustomerId") final Long customerId)
            throws ODataException {
        EntityManager em = entityManagerFactory.createEntityManager();
        messageBuffer
                .addMessage(Message.builder().code("salesorderops.findcustomer")
                        .args(new Object[] { customerId })
                        .defaultMessage("Find customer for id " + customerId)
                        .severity(Message.Severity.INFO).build());
        try {
            Query query = em.createNativeQuery(JPA_QUERY);
            query.setParameter(1, customerId);
            @SuppressWarnings("unchecked")
            List<Object[]> result = (List<Object[]>) query.getResultList();
            List<SalesOrderProjection> sops = result.stream()
                    .map(row -> new SalesOrderProjection(
                            ((BigInteger) row[0]).longValue(), (String) row[1],
                            (String) row[2]))
                    .collect(Collectors.toList());
            if (sops == null || sops.isEmpty()) {
                messageBuffer.addMessage(Message.builder()
                        .code("salesorderops.nocustomer")
                        .args(new Object[] { customerId })
                        .defaultMessage("No customer for id " + customerId)
                        .severity(Message.Severity.WARNING).build());
            }
            return sops;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ODataApplicationException("Internal error",
                    Locale.ENGLISH, HttpStatusCodes.INTERNAL_SERVER_ERROR);
        } finally {
            em.close();
        }
    }

    @EdmFunctionImport(name = "SalesOrderProjection", returnType = @ReturnType(type = Type.COMPLEX, isCollection = true))
    public List<SalesOrderProjection> getSalesOrderProjectionJdbcTemplate(
            @EdmFunctionImportParameter(name = "CustomerId") final Long customerId)
            throws ODataException {
        messageBuffer
                .addMessage(Message.builder().code("salesorderops.findcustomer")
                        .args(new Object[] { customerId })
                        .defaultMessage("Find customer for id " + customerId)
                        .severity(Message.Severity.INFO).build());
        List<SalesOrderProjection> sops = jdbcTemplate.query(TEMPLATE_QUERY,
                new Object[] { customerId },
                new RowMapper<SalesOrderProjection>() {
                    @Override
                    public SalesOrderProjection mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        return new SalesOrderProjection(rs.getLong("ORDER_ID"),
                                rs.getString("ORDER_TITLE"),
                                rs.getString("CUSTOMER_NAME"));
                    }
                });

        if (sops == null || sops.isEmpty()) {
            messageBuffer.addMessage(
                    Message.builder().code("salesorderops.nocustomer")
                            .args(new Object[] { customerId })
                            .defaultMessage("No customer for id " + customerId)
                            .severity(Message.Severity.WARNING).build());
        }

        return sops;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        SalesOrderOperations.setStaticApplicationContext(applicationContext);
        System.out.println(
                "setApplicationContext for SalesOrderOperations Instance: "
                        + SalesOrderOperations.getId());
    }
}