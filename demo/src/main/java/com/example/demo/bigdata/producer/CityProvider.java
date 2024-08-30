package com.example.demo.bigdata.producer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class CityProvider {

	@Autowired
	Config config;
	
	String strCityIds;	
	List<String> cityList;
	
	@PostConstruct
	public void init()	{
		strCityIds = config.getCityNames();
		cityList = Arrays.stream(strCityIds.split(",")).map(s->s.trim()).collect(Collectors.toList());
	}
}
