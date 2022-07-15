package com.icebear.stay.model;


import javax.persistence.*;
import java.io.Serializable;


/*
primary key: stay_id and date
foreign key: stay
 */

@Entity
@Table(name = "stay_reserved_date")
public class StayReservedDate implements Serializable {
    private static final long serialVersionUID = 1L;
    //    composite primary key:
    //    based on stay_id and date
    @EmbeddedId
    private StayReservedDateKey id;

    // @MapsId: provides mapping for an attribute within an EmbeddedId primary key
    // value("") specifies the attribute within a composite key.
    // let Stay and StayReservedDate share the same primary key: stay_id
    @MapsId("stay_id")
    @ManyToOne
    private Stay stay;

    public StayReservedDate() {}

    public StayReservedDate(StayReservedDateKey id, Stay stay) {
        this.id = id;
        this.stay = stay;
    }

    public StayReservedDateKey getId() {
        return id;
    }

    public Stay getStay() {
        return stay;
    }
    // no setter for StayReservedDate
    // id and stay will get from database

}
