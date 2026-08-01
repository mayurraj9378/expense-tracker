package com.expensetracker.backend.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
@EntityListeners(AuditingEntityListener.class)
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "spent_amount", precision = 10, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Column(name = "month_year", nullable = false)
    private LocalDate monthYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }
    
    public LocalDate getMonthYear() { return monthYear; }
    public void setMonthYear(LocalDate monthYear) { this.monthYear = monthYear; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder Pattern
    public static BudgetBuilder builder() {
        return new BudgetBuilder();
    }

    public static class BudgetBuilder {
        private Budget budget = new Budget();

        public BudgetBuilder id(Long id) { budget.id = id; return this; }
        public BudgetBuilder amount(BigDecimal amount) { budget.amount = amount; return this; }
        public BudgetBuilder spentAmount(BigDecimal spentAmount) { budget.spentAmount = spentAmount; return this; }
        public BudgetBuilder monthYear(LocalDate monthYear) { budget.monthYear = monthYear; return this; }
        public BudgetBuilder category(Category category) { budget.category = category; return this; }
        public BudgetBuilder user(User user) { budget.user = user; return this; }

        public Budget build() { return budget; }
    }
}