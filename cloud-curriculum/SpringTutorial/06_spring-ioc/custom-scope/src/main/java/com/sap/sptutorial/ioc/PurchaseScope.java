package com.sap.sptutorial.ioc;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class PurchaseScope implements Scope {
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if (name.equals("purchase") || name.equals("customer")) {
            return objectFactory.getObject();
        }
        return null;
    }

    @Override
    public Object remove(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object resolveContextualObject(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getConversationId() {
        // TODO Auto-generated method stub
        return null;
    }

}