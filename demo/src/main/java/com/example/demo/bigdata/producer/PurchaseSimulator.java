package com.example.demo.bigdata.producer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class PurchaseSimulator {

	@Autowired
	PPInfoProvider ppProvider;

	@Autowired
    CityProvider cityProvider;

	@Autowired
	Config config;

	@Autowired
    KafkaTemplate kafkaTemplate;

	List<PetrolPump> pumps;
	List<String> cities;
	
	int iterationCount=1;
	double petrolPrice = 72, deiselPrice = 60;

	Path output;
    Logger logger = LoggerFactory.getLogger(PurchaseSimulator.class.getName());

	@PostConstruct
	public void init() {
		pumps = ppProvider.loadPumps(config.getNoPumps());
		cities = cityProvider.getCityList();
        output = Paths.get(config.getFilePath());
    }

	@Scheduled(fixedDelayString="#{config.tickTime}",initialDelay=100)
	public void simulatePurchases() {

        int allowedHitsPerTick = Math.toIntExact(config.getAllowedHitsPerTick());
        long finalMillisToAdd;
        long toMillis = TimeUnit.MINUTES.toMillis(config.getIncrementTimeEveryTick());
        long initialMillis = LocalDate.parse(config.getInitialDate()).atStartOfDay().toInstant(
                ZoneOffset.ofHoursMinutes(5,30)).toEpochMilli();
        long millisToAdd = 0;
        DataHolder dataHolder = DataHolder.INSTANCE;
        List<PurchaseOrder> purchaseOrderList = new ArrayList<>();

        millisToAdd = toMillis*iterationCount;
        if(config.getWithInitialDateOrCurrent())
            finalMillisToAdd = initialMillis + millisToAdd;
        else
            finalMillisToAdd = System.currentTimeMillis() + millisToAdd;

        IntStream.rangeClosed(1, Math.toIntExact(allowedHitsPerTick)).forEach(i->{
            int nextPp = RandomUtils.nextInt(1,pumps.size());
            PetrolPump petrolPump = pumps.get(nextPp-1);
            PetrolPump.Machine machine = petrolPump.getMachines().get(RandomUtils.nextInt(1,4));

            String purchaseTime = new Timestamp( finalMillisToAdd ).toString();
            int fuelType = RandomUtils.nextInt(1, 3);
            int qty = RandomUtils.nextInt(1,10);

            PurchaseOrder order = PurchaseOrder.builder()
                    .petrolPumpId(petrolPump.getId())
                    .machineId(machine.getName())
                    .city(cities.get(RandomUtils.nextInt(1, cities.size())))
                    .purchaseTime(purchaseTime)
                    .fuelType(fuelType)
                    .qnty(qty)
                    .amt((int) evalAmount(fuelType, qty))
                    .pType(RandomUtils.nextInt(1, 4)).build();
            if(getConfig().isWriteToQueueOrFile()==true)    {
                try {
                    dataHolder.getQueue().put(order.toString());
                    logger.info("Entry: " + order.toString());
                } catch (InterruptedException e) {  }
            }else   {
                purchaseOrderList.add(order);
            }
        });
        //log.log(Level.INFO, "Generated: "+ Math.toIntExact(allowedHitsPerTick) + " entries for date: "
        //        + new Timestamp(finalMillisToAdd).toLocalDateTime());

        try {
            FileUtils.writeLines(output.toFile(),purchaseOrderList,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        iterationCount++;
	}

    private double evalAmount(int fuelType, int qty) {
	    double fuelAmt = 0;
        if(fuelType==1) // petrol
            fuelAmt = qty * petrolPrice;
        else
            fuelAmt = qty * deiselPrice;
        return fuelAmt;
    }

    @Scheduled(fixedDelayString="#{config.tickTime}",initialDelay=50)
    public void sendToKafka() {
        boolean writeToQueueOrFile = getConfig().isWriteToQueueOrFile();
        if(writeToQueueOrFile==true)    {
            BlockingQueue<String> queue = DataHolder.INSTANCE.getQueue();
            queue.stream().forEach(s->{
                kafkaTemplate.send("test1", 1, s);
            });
            queue.clear();
        }
    }

}
