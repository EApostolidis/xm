package com.example.xm.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.xm.model.NormalizeBitcoin;

public interface BitcoinApi {

  ResponseEntity<List<NormalizeBitcoin>> createAppointment(LocalDate from, LocalDate to);

}
