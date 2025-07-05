package main.java.com.library.dao;

import main.java.com.library.models.Loan;
import java.util.List;

public interface LoanDao {
    Loan addLoan(Loan loan);
    Loan updateLoan(Loan loan);
    List<Loan> findAllLoans();
    List<Loan> findLoansByUser(String userId);
    List<Loan> findActiveLoansByUser(String userId);
    List<Loan> findOverdueLoans();
}
