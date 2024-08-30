package com.example.demo.bigdata.producer;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class PurchaseOrder implements Serializable {

    String petrolPumpId, machineId, city, purchaseTime;
    int fuelType, qnty, amt, pType;

    @Override
    public String toString() {
        return "" +
                petrolPumpId + "|" + machineId + "|" + city + "|" + purchaseTime + "|" + fuelType +
                "|" + qnty + "|" + amt + "|" + pType;

        //Date dt = new Date(Long.parseLong(purchaseTime));
        /*
        return "" +
                "ppId='" + petrolPumpId + '\'' +
                "|mcId='" + machineId + '\'' +
                "|city='" + city + '\'' +
                "|purchaseTime='" + purchaseTime + '\'' +
                "|fuelType=" + fuelType +
                "|qnty=" + qnty +
                "|amt=" + amt +
                "|pType=" + pType + "";
                //'}';
        */
    }
}
