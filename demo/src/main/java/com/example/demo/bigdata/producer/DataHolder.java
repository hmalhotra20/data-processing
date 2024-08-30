package com.example.demo.bigdata.producer;

import lombok.Data;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public enum DataHolder {

    INSTANCE;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public static void doSomething(){

    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }
}
