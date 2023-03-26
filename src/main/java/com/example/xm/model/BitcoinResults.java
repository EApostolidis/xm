package com.example.xm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BitcoinResults {

  private Bitcoin oldest;
  private Bitcoin newest;
  private Bitcoin min;
  private Bitcoin max;

}
