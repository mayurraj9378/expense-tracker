package com.expensetracker.backend.repository;

import com.expensetracker.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdOrderByDateDesc(Long userId);
    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    List<Expense> findByUserIdAndCategoryIdOrderByDateDesc(Long userId, Long categoryId);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal sumExpensesByDateRange(@Param("userId") Long userId, 
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT FUNCTION('MONTH', e.date), FUNCTION('YEAR', e.date), SUM(e.amount) " +
           "FROM Expense e WHERE e.user.id = :userId " +
           "GROUP BY FUNCTION('YEAR', e.date), FUNCTION('MONTH', e.date) " +
           "ORDER BY FUNCTION('YEAR', e.date) DESC, FUNCTION('MONTH', e.date) DESC")
    List<Object[]> getMonthlyExpenseSummary(@Param("userId") Long userId);
}