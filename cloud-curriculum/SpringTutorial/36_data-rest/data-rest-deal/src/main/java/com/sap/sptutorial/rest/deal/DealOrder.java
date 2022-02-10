package com.sap.sptutorial.rest.deal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@EqualsAndHashCode
@JsonSerialize(using=DealOrderSerializer.class)
public class DealOrder {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "s_deal_order")
    @SequenceGenerator(name = "s_deal_order", sequenceName = "S_DEAL_ORDER")
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    private Deal deal;

    @Getter
    @Setter
    private Long customerId;
}
