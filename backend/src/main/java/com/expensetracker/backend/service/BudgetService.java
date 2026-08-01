package com.expensetracker.backend.service;

import com.expensetracker.backend.entity.Budget;
import com.expensetracker.backend.entity.Category;
import com.expensetracker.backend.entity.User;
import com.expensetracker.backend.repository.BudgetRepository;
import com.expensetracker.backend.repository.CategoryRepository;
import com.expensetracker.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {
    
    private static final Logger log = LoggerFactory.getLogger(BudgetService.class);
    
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BudgetService(BudgetRepository budgetRepository, 
                         UserRepository userRepository,
                         CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }
    
    @Transactional
    public Budget setBudget(Long userId, Long categoryId, BigDecimal amount, LocalDate monthYear) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        Budget budget = budgetRepository
            .findByUserIdAndCategoryIdAndMonthYear(userId, categoryId, monthYear)
            .orElse(Budget.builder()
                .user(user)
                .category(category)
                .monthYear(monthYear)
                .spentAmount(BigDecimal.ZERO)
                .build());
        
        budget.setAmount(amount);
        
        Budget savedBudget = budgetRepository.save(budget);
        log.info("💰 Budget set for category: {} - Amount: {}", category.getName(), amount);
        
        return savedBudget;
    }
    
    public List<Budget> getUserBudgetsForMonth(Long userId, LocalDate monthYear) {
        return budgetRepository.findByUserIdAndMonthYearOrderByCategoryNameAsc(userId, monthYear);
    }
    
    public List<Budget> getOverBudget(Long userId, LocalDate monthYear) {
        return budgetRepository.findOverBudget(userId, monthYear);
    }
    
    @Transactional
    public void updateSpentAmount(Long budgetId, BigDecimal spentAmount) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        budget.setSpentAmount(spentAmount);
        budgetRepository.save(budget);
        
        log.info("💰 Budget spent amount updated: {}", budgetId);
    }
}