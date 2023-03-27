package com.example.xm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CryptoResults {

  private Crypto oldest;
  private Crypto newest;
  private Crypto min;
  private Crypto max;

}
