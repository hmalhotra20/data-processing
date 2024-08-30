package com.example.spark_example1;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class PurchaseOrder implements Serializable {

    String petrolPumpId, machineId, city, purchaseTime;
    int fuelType, qnty, amt, pType;

    @Override
    public String toString() {
        return "" +
                petrolPumpId + "|" + machineId + "|" + city + "|" + purchaseTime + "|" + fuelType +
                "|" + qnty + "|" + amt + "|" + pType;
    }
}

