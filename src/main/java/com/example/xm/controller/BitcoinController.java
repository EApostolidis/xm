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

import com.example.xm.model.NormalizeBitcoin;
import com.example.xm.service.BitcoinService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/bitcoins", produces = APPLICATION_JSON_VALUE)
public class BitcoinController implements BitcoinApi {

  private final BitcoinService bitcoinService;

  public BitcoinController(BitcoinService bitcoinService) {
    this.bitcoinService = bitcoinService;
  }

  @Override
  @GetMapping
  public ResponseEntity<List<NormalizeBitcoin>> createAppointment(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    List<NormalizeBitcoin> response = bitcoinService.calculateNormalizeRange(from, to);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
