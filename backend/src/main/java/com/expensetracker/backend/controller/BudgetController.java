package com.expensetracker.backend.controller;

import com.expensetracker.backend.entity.Budget;
import com.expensetracker.backend.service.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {
    
    private static final Logger log = LoggerFactory.getLogger(BudgetController.class);
    
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return 1L;
    }
    
    @PostMapping
    public ResponseEntity<?> setBudget(
            @RequestParam Long categoryId,
            @RequestParam BigDecimal amount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate monthYear) {
        
        Long userId = getCurrentUserId();
        log.info("💰 Setting budget for user ID: {}, category: {}, amount: {}", userId, categoryId, amount);
        
        Budget budget = budgetService.setBudget(userId, categoryId, amount, monthYear);
        return ResponseEntity.ok(budget);
    }
    
    @GetMapping
    public ResponseEntity<?> getBudgetsForMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate monthYear) {
        
        Long userId = getCurrentUserId();
        log.info("💰 Fetching budgets for user ID: {}, month: {}", userId, monthYear);
        
        List<Budget> budgets = budgetService.getUserBudgetsForMonth(userId, monthYear);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/over-budget")
    public ResponseEntity<?> getOverBudget(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate monthYear) {
        
        Long userId = getCurrentUserId();
        log.info("💰 Fetching over-budget items for user ID: {}, month: {}", userId, monthYear);
        
        List<Budget> overBudget = budgetService.getOverBudget(userId, monthYear);
        return ResponseEntity.ok(overBudget);
    }
}