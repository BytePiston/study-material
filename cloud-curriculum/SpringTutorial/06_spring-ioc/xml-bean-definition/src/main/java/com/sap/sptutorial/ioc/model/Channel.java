package com.sap.sptutorial.ioc.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Channel {
    public enum Type {DIRECT, ONLINE};
    
    private Type type;
}
