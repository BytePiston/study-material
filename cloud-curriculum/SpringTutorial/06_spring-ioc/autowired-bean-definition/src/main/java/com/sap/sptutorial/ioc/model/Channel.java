package com.sap.sptutorial.ioc.model;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
public class Channel {
    public enum Type {DIRECT, ONLINE};
    
    private Type type;
}
