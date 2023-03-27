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
import com.example.xm.model.Crypto;
import com.example.xm.model.CryptoResults;
import com.example.xm.model.NormalizeRange;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * Main service class which holds all the business logic
 */
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

  /**
   * Calculates the oldest/newest/min/max values for a requested crypto
   */
  public CryptoResults fetchCryptoResults(String cryptoName) {
    return calculateCryptosResults().stream()
        .filter(result -> result.getSymbol().equals(cryptoName))
        .findFirst().orElseThrow(() -> new RuntimeException("There is no data for this crypto: " + cryptoName));
  }

  /**
   * Given the time range calculates the normalized range for the cryptos
   */
  public List<NormalizeRange> calculateNormalizeRange(LocalDate from, LocalDate to) {
    return configProperties.getCryptoNames().stream()
        .map(bitcoinName -> fetchCryptosPeriod(bitcoinName, from, to))
        .map(this::calculateCryptoResults)
        .map(RecommendationService::calculateNormalizationRange)
        .sorted((a, b) -> b.getRange().compareTo(a.getRange()))
        .collect(Collectors.toList());
  }

  /**
   * Retrieves the crypto with the maxim normalized range for a specific date
   */
  public NormalizeRange fetchHighestNormalizedRange(LocalDate date) {
    return Optional.of(configProperties.getCryptoNames().stream()
            .map(cryptoName -> fetchCryptosDate(cryptoName, date))
            .map(this::calculateCryptoResults)
            .map(RecommendationService::calculateNormalizationRange)
            .sorted((a, b) -> b.getRange().compareTo(a.getRange()))
            .collect(Collectors.toList())).map(bitcoins -> bitcoins.get(0))
        .orElseThrow(() -> new RuntimeException("No crypto found for that day"));
  }

  /**
   * Calculates the results for all the supported results
   */
  private List<CryptoResults> calculateCryptosResults() {
    return configProperties.getCryptoNames().stream()
        .map(this::fetchCryptos)
        .map(this::calculateCryptoResults)
        .collect(Collectors.toList());
  }

  /**
   * Calculates the oldest/newest/min/max values from a list of cryptos
   */
  private CryptoResults calculateCryptoResults(List<Crypto> cryptos) {
    BigDecimal min = MIN;
    BigDecimal max = MAX;
    Timestamp oldest = OLDEST;
    Timestamp newest = NEWEST;
    CryptoResults result = new CryptoResults();
    for (Crypto crypto : cryptos) {
      result.setSymbol(crypto.getSymbol());
      if (crypto.getPrice().compareTo(max) >= 0) {
        max = crypto.getPrice();
        result.setMaxPrice(crypto.getPrice());
      }
      if (crypto.getPrice().compareTo(min) <= 0) {
        min = crypto.getPrice();
        result.setMinPrice(crypto.getPrice());
      }
      if (crypto.getTimestamp().before(oldest)) {
        oldest = crypto.getTimestamp();
        result.setOldest(crypto.getTimestamp());
      }
      if (crypto.getTimestamp().after(newest)) {
        newest = crypto.getTimestamp();
        result.setNewest(crypto.getTimestamp());
      }
    }
    return result;
  }

  /**
   * Retrieves the cryptos for a specific date and specific crypto
   */
  private List<Crypto> fetchCryptosDate(String bitcoinName, LocalDate date) {
    Timestamp fromTimestamp = Timestamp.valueOf(date.atStartOfDay());
    Timestamp toTimestamp = Timestamp.valueOf(date.atTime(LocalTime.MAX));
    List<Crypto> cryptosPeriod = filterCryptosPeriod(bitcoinName, fromTimestamp, toTimestamp);
    if (cryptosPeriod.isEmpty()) {
      throw new RuntimeException("There are no cryptos data for: " + date);
    }
    return cryptosPeriod;
  }

  /**
   * Retrieves the cryptos for a range of time and specific crypto
   */
  private List<Crypto> fetchCryptosPeriod(String bitcoinName, LocalDate from, LocalDate to) {
    Timestamp fromTimestamp = Timestamp.valueOf(from.atStartOfDay());
    Timestamp toTimestamp = Timestamp.valueOf(to.atTime(LocalTime.MAX));
    List<Crypto> cryptosPeriod = filterCryptosPeriod(bitcoinName, fromTimestamp, toTimestamp);
    if (cryptosPeriod.isEmpty()) {
      throw new RuntimeException("There are no cryptos data between " + fromTimestamp + " and " + toTimestamp);
    }
    return cryptosPeriod;
  }

  /**
   * Filters the cryptos for the given crypto and time period
   */
  private List<Crypto> filterCryptosPeriod(String cryptoName, Timestamp fromTimestamp, Timestamp toTimestamp) {
    return fetchCryptos(cryptoName).stream()
        .filter(crypto -> crypto.getTimestamp().after(fromTimestamp) && crypto.getTimestamp().before(toTimestamp))
        .collect(Collectors.toList());
  }

  /**
   * Fetches the cryptos for a specific crypto name using {@link CsvMapper} and {@link CsvSchema} to transform data from csv files to {@link Crypto}
   */
  private List<Crypto> fetchCryptos(String cryptoName) {
    checkIfCryptoExists(cryptoName);
    ObjectReader oReader = csvMapper.reader(Crypto.class).with(schema);
    List<Crypto> cryptos = new ArrayList<>();
    try (Reader reader = new FileReader(FILE_SOURCE.concat(cryptoName).concat(FILE_SUFFIX))) {
      MappingIterator<Crypto> cryptoMappingIterator = oReader.readValues(reader);
      while (cryptoMappingIterator.hasNext()) {
        Crypto current = cryptoMappingIterator.next();
        cryptos.add(current);
      }
      return cryptos;
    } catch (IOException e) {
      throw new RuntimeException("Could not retrieve data", e);
    }
  }

  /**
   * Calculates the normal range a crypto.
   */
  private static NormalizeRange calculateNormalizationRange(CryptoResults cryptoResults) {
    NormalizeRange normalizeRange = new NormalizeRange();
    normalizeRange.setSymbol(cryptoResults.getSymbol());
    normalizeRange.setRange((cryptoResults.getMaxPrice().subtract(cryptoResults.getMinPrice()))
        .divide(cryptoResults.getMinPrice(), 2, RoundingMode.HALF_UP));
    return normalizeRange;
  }

  /**
   * Checks if crypto exists in the supported cryptos.
   */
  private void checkIfCryptoExists(String cryptoName) {
    if (!configProperties.getCryptoNames().contains(cryptoName)) {
      throw new RuntimeException("There is no data for this crypto: " + cryptoName);
    }
  }
}
