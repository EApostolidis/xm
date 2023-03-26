package com.example.xm.controller;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.xm.model.BitcoinResults;
import com.example.xm.model.NormalizeRange;
import com.example.xm.service.RecommendationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

  @MockBean
  RecommendationService recommendationService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getNormalizedRange() throws Exception {
    when(recommendationService.calculateNormalizeRange(any(), any())).thenReturn(new ArrayList<>());
    mockMvc.perform(get("/bitcoins/normalized-range")
            .param("from", "2000-01-01")
            .param("to", "2000-01-01"))
        .andExpect(status().isOk());
  }

  @Test
  void getNormalizedRange_bad_request() throws Exception {
    when(recommendationService.calculateNormalizeRange(any(), any())).thenReturn(new ArrayList<>());
    mockMvc.perform(get("/bitcoins/normalized-range")
            .param("frommm", "2000-01-01")
            .param("to", "2000-01-01"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getNormalizedRange_not_found() throws Exception {
    when(recommendationService.calculateNormalizeRange(any(), any())).thenReturn(new ArrayList<>());
    mockMvc.perform(get("/bitcoins/normalized-ranges")
            .param("from", "2000-01-01")
            .param("to", "2000-01-01"))
        .andExpect(status().isNotFound());
  }
  @Test
  void getBitcoinResults() throws Exception {
    when(recommendationService.fetchBitcoinResults(any())).thenReturn(new BitcoinResults());
    mockMvc.perform(get("/bitcoins/results")
            .param("bitcoinName", "ETC"))
        .andExpect(status().isOk());
  }

  @Test
  void getBitcoinResults_bad_request() throws Exception {
    when(recommendationService.fetchBitcoinResults(any())).thenReturn(new BitcoinResults());
    mockMvc.perform(get("/bitcoins/results")
            .param("bitcoinNames", "ETC"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getBitcoinResults_not_found() throws Exception {
    when(recommendationService.fetchBitcoinResults(any())).thenReturn(new BitcoinResults());
    mockMvc.perform(get("/bitcoins/resultss")
            .param("bitcoinName", "ETC"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getHighestNormalizedRangeByDate() throws Exception {
    when(recommendationService.fetchHighestNormalizedRange(any())).thenReturn(new NormalizeRange());
    mockMvc.perform(get("/bitcoins/highest-normalized-range")
            .param("date", "2000-01-01"))
        .andExpect(status().isOk());
  }

  @Test
  void getHighestNormalizedRangeByDate_bad_request() throws Exception {
    when(recommendationService.fetchHighestNormalizedRange(any())).thenReturn(new NormalizeRange());
    mockMvc.perform(get("/bitcoins/highest-normalized-range")
            .param("date", "ETC"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getHighestNormalizedRangeByDate_not_found() throws Exception {
    when(recommendationService.fetchHighestNormalizedRange(any())).thenReturn(new NormalizeRange());
    mockMvc.perform(get("/bitcoins/highest-normalized-ranges")
            .param("date", "ETC"))
        .andExpect(status().isNotFound());
  }
}