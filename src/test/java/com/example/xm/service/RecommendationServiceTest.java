package com.example.xm.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.xm.model.CryptoResults;
import com.example.xm.model.NormalizeRange;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RecommendationServiceTest {

  @Autowired
  RecommendationService recommendationService;

  @Test
  void calculateNormalizeRange() {
    List<NormalizeRange> result = recommendationService.calculateNormalizeRange(LocalDate.of(2000, 01, 01),
        LocalDate.of(2030, 01, 01));
    Assertions.assertEquals(5, result.size());
  }

  @Test
  void calculateNormalizeRange_fail() {
    Throwable exception = assertThrows(RuntimeException.class, () -> recommendationService.calculateNormalizeRange(LocalDate.of(2000, 01, 01),
        LocalDate.of(2000, 01, 01)));
    Assertions.assertEquals("There are no cryptos data between 2000-01-01 00:00:00.0 and 2000-01-01 23:59:59.999999999", exception.getMessage());
  }

  @Test
  void calculateBitCoinsResultsSpecificDate() {
    CryptoResults result = recommendationService.fetchCryptoResults("BTC");
    Assertions.assertEquals(BigDecimal.valueOf(47722.66), result.getMaxPrice());
    Assertions.assertEquals(BigDecimal.valueOf(33276.59), result.getMinPrice());
    Assertions.assertEquals(Timestamp.valueOf("2022-01-01 06:00:00.0"), result.getOldest());
    Assertions.assertEquals(Timestamp.valueOf("2022-01-31 22:00:00.0"), result.getNewest());
    Assertions.assertEquals("BTC", result.getSymbol());
  }

  @Test
  void calculateBitCoinsResultsSpecificDate_fail() {
    Throwable exception = assertThrows(RuntimeException.class, () -> recommendationService.fetchCryptoResults("AAA"));
    Assertions.assertEquals("There is no data for this crypto: AAA", exception.getMessage());
  }

  @Test
  void fetchHighestNormalizedCrypto() {
    NormalizeRange result = recommendationService.fetchHighestNormalizedRange(LocalDate.of(2022, 01, 03));
    Assertions.assertEquals("BTC", result.getSymbol());
    Assertions.assertEquals(BigDecimal.valueOf(0.03), result.getRange());
  }

  @Test
  void fetchHighestNormalizedCrypto_fail() {
    Throwable exception = assertThrows(RuntimeException.class, () -> recommendationService.fetchHighestNormalizedRange(LocalDate.of(2021, 01, 03)));
    Assertions.assertEquals("There are no cryptos data for: 2021-01-03", exception.getMessage());
  }
}