package com.example.xm.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.xm.model.Bitcoin;
import com.example.xm.model.BitcoinMonthResults;
import com.example.xm.model.NormalizeBitcoin;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BitcoinServiceTest {

  @Autowired
  BitcoinService bitcoinService;

  @Test
  void fetchBitCoins() {
    List<Bitcoin> result = bitcoinService.fetchBitCoins("ETH");
    Assertions.assertNotNull(result);
  }

  @Test
  void calculateBitCoinsResults() {
    List<Bitcoin> bitcoins = bitcoinService.fetchBitCoins("DOGE");
    BitcoinMonthResults result = bitcoinService.calculateBitCoinsResults(bitcoins);
    Assertions.assertNotNull(result);
  }

  @Test
  void calculateNormalizeRange() {
    List<NormalizeBitcoin> result = bitcoinService.calculateNormalizeRange(List.of("BTC", "DOGE", "ETH", "LTC", "XRP"));
    Assertions.assertNotNull(result);

  }
}