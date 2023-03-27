package com.example.xm.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CryptoResults {
  private Timestamp oldest;
  private Timestamp newest;
  private BigDecimal maxPrice;
  private BigDecimal minPrice;
  private String symbol;
}
