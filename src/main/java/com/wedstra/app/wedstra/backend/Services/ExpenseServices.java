package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.Expense;
import com.wedstra.app.wedstra.backend.Repo.ExpenseRepository;
import com.wedstra.app.wedstra.backend.config.AmazonS3Config.bucket.fileStore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ExpenseServices {

    @Autowired
    private ExpenseRepository expenseRepository;


    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void deleteExpense(String id) {
        expenseRepository.deleteById(id);
    }

    public List<Expense> getUserExpenses(String userId) {
        return expenseRepository.findByUserId(userId);
    }

}
