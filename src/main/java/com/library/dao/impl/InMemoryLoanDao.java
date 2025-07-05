package main.java.com.library.dao.impl;

import main.java.com.library.dao.LoanDao;
import main.java.com.library.models.Loan;
import main.java.com.library.models.Loan.LoanStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryLoanDao implements LoanDao {
    private final Map<Long, Loan> loans = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Loan addLoan(Loan loan) {
        long id = idGenerator.getAndIncrement();
        loan.setId(id);
        loans.put(id, loan);
        return loan;
    }

    @Override
    public Loan updateLoan(Loan loan) {
        if (!loans.containsKey(loan.getId())) {
            return null;
        }
        loans.put(loan.getId(), loan);
        return loan;
    }

    @Override
    public List<Loan> findAllLoans() {
        return new ArrayList<>(loans.values());
    }

    @Override
    public List<Loan> findLoansByUser(String userId) {
        return loans.values().stream()
                .filter(loan -> loan.getUser().getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findActiveLoansByUser(String userId) {
        return loans.values().stream()
                .filter(loan -> loan.getUser().getUserId().equals(userId))
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findOverdueLoans() {
        LocalDate today = LocalDate.now();
        return loans.values().stream()
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE)
                .filter(loan -> loan.getDueDate().isBefore(today))
                .collect(Collectors.toList());
    }
}
