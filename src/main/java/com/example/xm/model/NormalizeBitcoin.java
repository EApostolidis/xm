package com.example.xm.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NormalizeBitcoin {

  private String symbol;
  private BigDecimal range;
}
