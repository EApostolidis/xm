package com.example.xm.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bitcoin {
  
  private Timestamp timestamp;
  private String symbol;
  private BigDecimal price;
}
