package com.example.xm.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.xm.model.Bitcoin;
import com.example.xm.model.BitcoinMonthResults;
import com.example.xm.model.NormalizeBitcoin;

@SpringBootTest
class BitcoinServiceTest {

  @Autowired
  BitcoinService bitcoinService;

  @Test
  void fetchBitCoins() {
    List<Bitcoin> result = bitcoinService.fetchBitCoins("ETH", LocalDate.of(2000, 01, 01), LocalDate.of(2030, 01, 01));
    Assertions.assertNotNull(result);
  }

  @Test
  void calculateBitCoinsResults() {
    List<Bitcoin> bitcoins = bitcoinService.fetchBitCoins("DOGE", LocalDate.of(2000, 01, 01), LocalDate.of(2030, 01, 01));
    BitcoinMonthResults result = bitcoinService.calculateBitCoinsResults(bitcoins);
    Assertions.assertNotNull(result);
  }

  @Test
  void calculateNormalizeRange() {
    List<NormalizeBitcoin> result = bitcoinService.calculateNormalizeRange(List.of("BTC", "DOGE", "ETH", "LTC", "XRP"), LocalDate.of(2000, 01, 01),
        LocalDate.of(2030, 01, 01));
    Assertions.assertNotNull(result);

  }

  @Test
  void calculateBitCoinsResultsSpecificDate() {
    BitcoinMonthResults result = bitcoinService.calculateBitCoinsResultsSpecificDate(LocalDate.of(2022, 01, 01), "BTC");
    Assertions.assertNotNull(result);
  }

  @Test
  void fetchHighestNormalizedCrypto() {
    NormalizeBitcoin result = bitcoinService.fetchHighestNormalizedCrypto(LocalDate.of(2022, 01, 03));
    Assertions.assertNotNull(result);
  }
}