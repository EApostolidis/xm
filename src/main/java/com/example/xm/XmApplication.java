package com.example.xm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.xm.configuration.ConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = ConfigProperties.class)
public class XmApplication {

  public static void main(String[] args) {
    SpringApplication.run(XmApplication.class, args);
  }

}
