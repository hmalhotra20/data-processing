package com.example.demo.bigdata.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.bigdata.producer.PetrolPump.Machine;

import lombok.Data;

@Component
@Data
public class PPInfoProvider {
	
	@Autowired
	Config config;
	
	public List<PetrolPump> loadPumps(int n)	{
		
		List<PetrolPump> pumps = new ArrayList<>(); 
		IntStream.rangeClosed(1, n).forEach(i-> {
			String uid = RandomStringUtils.randomAlphanumeric(8);			
			PetrolPump pump = new PetrolPump("pp" + i +"-" + uid, config.getPumpCapacity());
			pump.init();
			pumps.add(pump);
		});
		
		return pumps;
	}

}
