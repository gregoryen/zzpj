package com.example.zzpj.ranking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rate")
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long fkUserId;

    private long fkSquadId;

    private double rateValue;

    @Transient
    public final static double NO_RATE = -1.0;
}