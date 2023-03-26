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

import com.example.xm.model.BitcoinResults;
import com.example.xm.model.NormalizeRange;
import com.example.xm.service.RecommendationService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/bitcoins", produces = APPLICATION_JSON_VALUE)
public class RecommendationController implements RecommendationApi {

  private final RecommendationService recommendationService;

  public RecommendationController(RecommendationService recommendationService) {
    this.recommendationService = recommendationService;
  }

  @Override
  @GetMapping(value = "/normalized-range")
  public ResponseEntity<List<NormalizeRange>> getNormalizedRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    List<NormalizeRange> response = recommendationService.calculateNormalizeRange(from, to);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Override
  @GetMapping(value = "/results")
  public ResponseEntity<BitcoinResults> getBitcoinResults(@RequestParam String bitcoinName) {
    BitcoinResults response = recommendationService.fetchBitcoinResults(bitcoinName);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Override
  @GetMapping(value = "/highest-normalized-range")
  public ResponseEntity<NormalizeRange> getHighestNormalizedRangeByDate(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    NormalizeRange response = recommendationService.fetchHighestNormalizedRange(date);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
