package com.example.demo.bigdata.producer;

public enum Constants {
	
	PETROL_PRICE(67), DEISEL_PRICE(52);
	
	private int numVal;

	Constants(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
