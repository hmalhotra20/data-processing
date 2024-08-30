package com.example.demo.bigdata.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/*
 * Assumption: each petrol pump has four machines - 2 petrol, 2 deisel
 */
public class PetrolPump {
	
	String id;
	Integer capacity;
	List<Machine> machines;
	
	public PetrolPump(String id, Integer capacity)	{
		this.id = id;
		this.capacity = capacity;
	}
	
	public void init()	{
		this.machines = new ArrayList<>();
		IntStream.rangeClosed(1, 4).forEach(i-> {
			String mcName = id + "-" + "mc-" + RandomStringUtils.randomAlphanumeric(8);
			machines.add(Machine.builder().type(RandomUtils.nextInt(1,3)).name(mcName).build());
		});
	}
	
	@Data
	@Builder
	static class Machine	{
		String name; // mc-p1-uuid
		int type; // 1-p, 2-d 
	}
}
