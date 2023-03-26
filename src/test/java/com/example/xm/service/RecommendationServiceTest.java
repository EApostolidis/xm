package com.example.xm.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.xm.model.BitcoinResults;
import com.example.xm.model.NormalizeRange;

@SpringBootTest
class RecommendationServiceTest {

  @Autowired
  RecommendationService recommendationService;

//  @Test
//  void fetchBitCoins() {
//    List<Bitcoin> result = recomendationService.fetchBitCoinsPeriod("ETH", LocalDate.of(2000, 01, 01), LocalDate.of(2030, 01, 01));
//    Assertions.assertNotNull(result);
//  }
//
//  @Test
//  void calculateBitCoinsResults() {
//    List<Bitcoin> bitcoins = recomendationService.fetchBitCoinsPeriod("DOGE", LocalDate.of(2000, 01, 01), LocalDate.of(2030, 01, 01));
//    BitcoinResults result = recomendationService.calculateBitCoinsResults(bitcoins);
//    Assertions.assertNotNull(result);
//  }

  @Test
  void calculateNormalizeRange() {
    List<NormalizeRange> result = recommendationService.calculateNormalizeRange(LocalDate.of(2000, 01, 01),
        LocalDate.of(2030, 01, 01));
    Assertions.assertNotNull(result);

  }

  @Test
  void calculateBitCoinsResultsSpecificDate() {
    BitcoinResults result = recommendationService.fetchBitcoinResults("BTC");
    Assertions.assertNotNull(result);
  }

  @Test
  void fetchHighestNormalizedCrypto() {
    NormalizeRange result = recommendationService.fetchHighestNormalizedRange(LocalDate.of(2022, 01, 03));
    Assertions.assertNotNull(result);
  }
}