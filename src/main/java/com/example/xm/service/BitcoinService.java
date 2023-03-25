package com.example.xm.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.xm.model.Bitcoin;
import com.example.xm.model.BitcoinMonthResults;
import com.example.xm.model.NormalizeBitcoin;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BitcoinService {

  private static final BigDecimal MIN = BigDecimal.valueOf(99999999999999999.99);
  private static final BigDecimal MAX = BigDecimal.valueOf(0);
  private static final Timestamp OLDEST = Timestamp.valueOf("3000-01-01 00:00:00");
  private static final Timestamp NEWEST = Timestamp.valueOf("1900-01-01 00:00:00");
  private static String FILE_SOURCE = "src/test/resources/prices/";
  private static String FILE_SUFFIX = "_values.csv";
  private final CsvMapper csvMapper;
  private final CsvSchema schema;

  public BitcoinService() {
    this.csvMapper = new CsvMapper();
    this.schema = CsvSchema.emptySchema().withHeader();
  }

  public List<Bitcoin> fetchBitCoins(String bitcoinName) {
    ObjectReader oReader = csvMapper.reader(Bitcoin.class).with(schema);
    List<Bitcoin> bitcoins = new ArrayList<>();
    try (Reader reader = new FileReader(FILE_SOURCE.concat(bitcoinName).concat(FILE_SUFFIX))) {
      MappingIterator<Bitcoin> bitcoinMappingIterator = oReader.readValues(reader);
      while (bitcoinMappingIterator.hasNext()) {
        Bitcoin current = bitcoinMappingIterator.next();
        bitcoins.add(current);
        System.out.println(current);
      }
      return bitcoins;
    } catch (IOException e) {
      throw new RuntimeException("Could not retrieve data", e);
    }
  }

  public BitcoinMonthResults calculateBitCoinsResults(List<Bitcoin> bitcoins) {
    BigDecimal min = MIN;
    BigDecimal max = MAX;
    Timestamp oldest = OLDEST;
    Timestamp newest = NEWEST;
    BitcoinMonthResults result = new BitcoinMonthResults();
    for (Bitcoin bitcoin : bitcoins) {
      if(bitcoin.getPrice().compareTo(max) >= 0) {
        max = bitcoin.getPrice();
        result.setMax(bitcoin);
      }
      if(bitcoin.getPrice().compareTo(min) <= 0) {
        min = bitcoin.getPrice();
        result.setMin(bitcoin);
      }
      if(bitcoin.getTimestamp().before(oldest)) {
        oldest = bitcoin.getTimestamp();
        result.setOldest(bitcoin);
      }
      if(bitcoin.getTimestamp().after(newest)) {
        newest = bitcoin.getTimestamp();
        result.setNewest(bitcoin);
      }
    }
    return result;
  }

  public List<NormalizeBitcoin> calculateNormalizeRange(List<String> bitcoinNames) {
    return bitcoinNames.stream()
        .map(this::fetchBitCoins)
        .map(this::calculateBitCoinsResults)
        .map(BitcoinService::calculateNormalization)
        .sorted((a, b) -> b.getRange().compareTo(a.getRange()))
        .collect(Collectors.toList());
  }

  private static NormalizeBitcoin calculateNormalization(BitcoinMonthResults bitcoinMonthResults) {
    NormalizeBitcoin normalizeBitcoin = new NormalizeBitcoin();
    normalizeBitcoin.setSymbol(bitcoinMonthResults.getMin().getSymbol());
    normalizeBitcoin.setRange((bitcoinMonthResults.getMax().getPrice().subtract(bitcoinMonthResults.getMin().getPrice()))
        .divide(bitcoinMonthResults.getMin().getPrice(), 2, RoundingMode.HALF_UP));
    return normalizeBitcoin;
  }
}
