package com.expensetracker.backend.repository;

import com.expensetracker.backend.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserIdAndMonthYearOrderByCategoryNameAsc(Long userId, LocalDate monthYear);
    Optional<Budget> findByUserIdAndCategoryIdAndMonthYear(Long userId, Long categoryId, LocalDate monthYear);
    
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.monthYear = :monthYear AND b.spentAmount > b.amount")
    List<Budget> findOverBudget(@Param("userId") Long userId, @Param("monthYear") LocalDate monthYear);
}