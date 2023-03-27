package com.example.xm.service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.xm.configuration.ConfigProperties;
import com.example.xm.model.Bitcoin;
import com.example.xm.model.BitcoinResults;
import com.example.xm.model.NormalizeRange;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Service
public class RecommendationService {

  private static final BigDecimal MIN = BigDecimal.valueOf(99999999999999999.99);
  private static final BigDecimal MAX = BigDecimal.valueOf(0);
  private static final Timestamp OLDEST = Timestamp.valueOf("3000-01-01 00:00:00");
  private static final Timestamp NEWEST = Timestamp.valueOf("1900-01-01 00:00:00");
  private static String FILE_SOURCE = "src/main/resources/prices/";
  private static String FILE_SUFFIX = "_values.csv";
  private final ConfigProperties configProperties;
  private final CsvMapper csvMapper;
  private final CsvSchema schema;

  public RecommendationService(ConfigProperties configProperties) {
    this.configProperties = configProperties;
    this.csvMapper = new CsvMapper();
    this.schema = CsvSchema.emptySchema().withHeader();
  }

  public BitcoinResults fetchBitcoinResults(String bitcoinName) {
    return calculateBitCoinsResults(fetchBitCoins(bitcoinName));
  }

  public List<NormalizeRange> calculateNormalizeRange(LocalDate from, LocalDate to) {
    return configProperties.getBitcoinNames().stream()
        .map(bitcoinName -> fetchBitCoinsPeriod(bitcoinName, from, to))
        .map(this::calculateBitCoinsResults)
        .map(RecommendationService::calculateNormalizationRange)
        .sorted((a, b) -> b.getRange().compareTo(a.getRange()))
        .collect(Collectors.toList());
  }

  public NormalizeRange fetchHighestNormalizedRange(LocalDate date) {
    return Optional.of(configProperties.getBitcoinNames().stream()
            .map(bitcoinName -> fetchBitCoinsDate(bitcoinName, date))
            .map(this::calculateBitCoinsResults)
            .map(RecommendationService::calculateNormalizationRange)
            .sorted((a, b) -> b.getRange().compareTo(a.getRange()))
            .collect(Collectors.toList())).map(bitcoins -> bitcoins.get(0))
        .orElseThrow(() -> new RuntimeException("No bitcoins found for that day"));
  }

  private BitcoinResults calculateBitCoinsResults(List<Bitcoin> bitcoins) {
    BigDecimal min = MIN;
    BigDecimal max = MAX;
    Timestamp oldest = OLDEST;
    Timestamp newest = NEWEST;
    BitcoinResults result = new BitcoinResults();
    for (Bitcoin bitcoin : bitcoins) {
      if (bitcoin.getPrice().compareTo(max) >= 0) {
        max = bitcoin.getPrice();
        result.setMax(bitcoin);
      }
      if (bitcoin.getPrice().compareTo(min) <= 0) {
        min = bitcoin.getPrice();
        result.setMin(bitcoin);
      }
      if (bitcoin.getTimestamp().before(oldest)) {
        oldest = bitcoin.getTimestamp();
        result.setOldest(bitcoin);
      }
      if (bitcoin.getTimestamp().after(newest)) {
        newest = bitcoin.getTimestamp();
        result.setNewest(bitcoin);
      }
    }
    return result;
  }

  private List<Bitcoin> fetchBitCoinsDate(String bitcoinName, LocalDate date) {
    Timestamp fromTimestamp = Timestamp.valueOf(date.atStartOfDay());
    Timestamp toTimestamp = Timestamp.valueOf(date.atTime(LocalTime.MAX));
    List<Bitcoin> bitcoinsPeriod = filterBitcoinsPeriod(bitcoinName, fromTimestamp, toTimestamp);
    if (bitcoinsPeriod.isEmpty()) {
      throw new RuntimeException("There are no bitcoins data for: " + date);
    }
    return bitcoinsPeriod;
  }

  private List<Bitcoin> fetchBitCoinsPeriod(String bitcoinName, LocalDate from, LocalDate to) {
    Timestamp fromTimestamp = Timestamp.valueOf(from.atStartOfDay());
    Timestamp toTimestamp = Timestamp.valueOf(to.atTime(LocalTime.MAX));
    List<Bitcoin> bitcoinsPeriod = filterBitcoinsPeriod(bitcoinName, fromTimestamp, toTimestamp);
    if (bitcoinsPeriod.isEmpty()) {
      throw new RuntimeException("There are no bitcoins data between " + fromTimestamp + " and " + toTimestamp);
    }
    return bitcoinsPeriod;
  }


  private List<Bitcoin> filterBitcoinsPeriod(String bitcoinName, Timestamp fromTimestamp, Timestamp toTimestamp) {
    return fetchBitCoins(bitcoinName).stream()
        .filter(bitcoin -> bitcoin.getTimestamp().after(fromTimestamp) && bitcoin.getTimestamp().before(toTimestamp))
        .collect(Collectors.toList());
  }

  private List<Bitcoin> fetchBitCoins(String bitcoinName) {
    checkIfBitcoinExists(bitcoinName);
    ObjectReader oReader = csvMapper.reader(Bitcoin.class).with(schema);
    List<Bitcoin> bitcoins = new ArrayList<>();
    try (Reader reader = new FileReader(FILE_SOURCE.concat(bitcoinName).concat(FILE_SUFFIX))) {
      MappingIterator<Bitcoin> bitcoinMappingIterator = oReader.readValues(reader);
      while (bitcoinMappingIterator.hasNext()) {
        Bitcoin current = bitcoinMappingIterator.next();
        bitcoins.add(current);
      }
      return bitcoins;
    } catch (IOException e) {
      throw new RuntimeException("Could not retrieve data", e);
    }
  }

  private static NormalizeRange calculateNormalizationRange(BitcoinResults bitcoinResults) {
    NormalizeRange normalizeRange = new NormalizeRange();
    normalizeRange.setSymbol(bitcoinResults.getMin().getSymbol());
    normalizeRange.setRange((bitcoinResults.getMax().getPrice().subtract(bitcoinResults.getMin().getPrice()))
        .divide(bitcoinResults.getMin().getPrice(), 2, RoundingMode.HALF_UP));
    return normalizeRange;
  }

  private void checkIfBitcoinExists(String bitcoinName) {
    if (!configProperties.getBitcoinNames().contains(bitcoinName)) {
      throw new RuntimeException("There is no data for this bitcoin: " + bitcoinName);
    }
  }
}
