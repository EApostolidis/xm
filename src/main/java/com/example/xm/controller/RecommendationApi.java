package com.example.xm.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.xm.model.CryptoResults;
import com.example.xm.model.NormalizeRange;

public interface RecommendationApi {

  ResponseEntity<List<NormalizeRange>> getNormalizedRange(LocalDate from, LocalDate to);
  ResponseEntity<CryptoResults> getBitcoinResults(String bitcoinName);
  ResponseEntity<NormalizeRange> getHighestNormalizedRangeByDate(LocalDate from);
}
