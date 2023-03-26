package com.example.xm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.xm.configuration.ConfigProperties;
import com.example.xm.service.BitcoinService;

@SpringBootApplication
@EnableConfigurationProperties(value = ConfigProperties.class)
public class XmApplication implements ApplicationRunner  {

  @Autowired
  BitcoinService bitcoinService;
  public static void main(String[] args) {
    SpringApplication.run(XmApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    var result = bitcoinService.fetchBitCoins("BTC");
  }
}
