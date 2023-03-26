package com.example.xm.service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
      }
      return bitcoins;
    } catch (IOException e) {
      throw new RuntimeException("Could not retrieve data", e);
    }
  }

  public List<Bitcoin> fetchBitCoinsPeriod(String bitcoinName, LocalDate from, LocalDate to) {
    Timestamp fromTimestamp = Timestamp.valueOf(from.atStartOfDay());
    Timestamp toTimestamp = Timestamp.valueOf(to.atStartOfDay());
    return fetchBitCoins(bitcoinName).stream()
        .filter(bitcoin -> bitcoin.getTimestamp().after(fromTimestamp) && bitcoin.getTimestamp().before(toTimestamp))
        .collect(Collectors.toList());
  }

  public BitcoinMonthResults calculateBitCoinsResults(List<Bitcoin> bitcoins) {
    BigDecimal min = MIN;
    BigDecimal max = MAX;
    Timestamp oldest = OLDEST;
    Timestamp newest = NEWEST;
    BitcoinMonthResults result = new BitcoinMonthResults();
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

  public BitcoinMonthResults calculateBitCoinsResultsSpecificDate(LocalDate date, String bitcoinName) {
    List<Bitcoin> bitcoins = fetchBitCoins(bitcoinName);
    return calculateBitCoinsResults(bitcoins.stream()
        .filter(bitcoin -> bitcoin.getTimestamp().toLocalDateTime().toLocalDate().equals(date) && bitcoin.getSymbol().equals(bitcoinName))
        .collect(Collectors.toList()));
  }

  public List<NormalizeBitcoin> calculateNormalizeRange(List<String> bitcoinNames, LocalDate from, LocalDate to) {
    return bitcoinNames.stream()
        .map(bitcoinName -> fetchBitCoinsPeriod(bitcoinName, from, to))
        .map(this::calculateBitCoinsResults)
        .map(BitcoinService::calculateNormalization)
        .sorted((a, b) -> b.getRange().compareTo(a.getRange()))
        .collect(Collectors.toList());
  }

  public NormalizeBitcoin fetchHighestNormalizedCrypto(LocalDate date) {
    return Stream.of("BTC", "DOGE", "ETH", "LTC", "XRP")
        .map(bitcoinName -> fetchBitCoinsPeriod(bitcoinName, date.atStartOfDay().toLocalDate(), date.plusDays(1).atStartOfDay().toLocalDate()))
        .map(this::calculateBitCoinsResults)
        .map(BitcoinService::calculateNormalization)
        .sorted((a, b) -> b.getRange().compareTo(a.getRange()))
        .collect(Collectors.toList()).get(0);

  }

  private static NormalizeBitcoin calculateNormalization(BitcoinMonthResults bitcoinMonthResults) {
    NormalizeBitcoin normalizeBitcoin = new NormalizeBitcoin();
    normalizeBitcoin.setSymbol(bitcoinMonthResults.getMin().getSymbol());
    normalizeBitcoin.setRange((bitcoinMonthResults.getMax().getPrice().subtract(bitcoinMonthResults.getMin().getPrice()))
        .divide(bitcoinMonthResults.getMin().getPrice(), 2, RoundingMode.HALF_UP));
    return normalizeBitcoin;
  }
}
