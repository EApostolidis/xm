package com.example.xm.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Application configurations
 */
@Configuration
@ConfigurationProperties(prefix = "xm")
@Data
public class ConfigProperties {

  /**
   * Supported cryptos
   */
  public List<String> cryptoNames = new ArrayList<>();
}
