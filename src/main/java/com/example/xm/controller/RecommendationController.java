package com.example.xm.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.xm.model.CryptoResults;
import com.example.xm.model.NormalizeRange;
import com.example.xm.service.RecommendationService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Main controller which implements the Recommendation Api endpoints
 */
@RestController
@RequestMapping(value = "/crypto-recommendation", produces = APPLICATION_JSON_VALUE)
public class RecommendationController implements RecommendationApi {

  private final RecommendationService recommendationService;

  public RecommendationController(RecommendationService recommendationService) {
    this.recommendationService = recommendationService;
  }

  /**
   * Endpoint that returns a descending sorted list of all the cryptos,
   * comparing the normalized range (i.e. (max-min)/min) by giving the time range
   * @param from first date of calculations {@link LocalDate}
   * @param to last date of calculations {@link LocalDate}
   * @return the descended list with the normalized results {@link ResponseEntity<List<NormalizeRange>> }
   */
  @Override
  @GetMapping(value = "/normalized-range")
  public ResponseEntity<List<NormalizeRange>> getNormalizedRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    List<NormalizeRange> response = recommendationService.calculateNormalizeRange(from, to);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Endpoint that returns the oldest/newest/min/max values for a requested crypto
   * @param cryptoName the name of the crypt {@link String}
   * @return the expected results {@link ResponseEntity<CryptoResults>}
   */
  @Override
  @GetMapping(value = "/results")
  public ResponseEntity<CryptoResults> getBitcoinResults(@RequestParam String cryptoName) {
    CryptoResults response = recommendationService.fetchCryptoResults(cryptoName);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Endpoint that returns the crypto with the highest normalized range for a specific day
   * @param date the date to get the highest normalized range {@link LocalDate}
   * @return the highest normalized range for the specific date  {@link ResponseEntity<NormalizeRange>}
   */
  @Override
  @GetMapping(value = "/highest-normalized-range")
  public ResponseEntity<NormalizeRange> getHighestNormalizedRangeByDate(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    NormalizeRange response = recommendationService.fetchHighestNormalizedRange(date);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
