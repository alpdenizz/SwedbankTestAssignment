package swedbank.TestAssignment.repository;

import java.math.BigDecimal;

public class TotalSpentMoneyByMonth {
	
	private int month;
	private BigDecimal totalMoneySpent;
	
	public TotalSpentMoneyByMonth(int month, BigDecimal totalMoneySpent) {
		this.month = month;
		this.totalMoneySpent = totalMoneySpent;
	}
	
	public TotalSpentMoneyByMonth() {}
	
	public void setMonth(int month) {
		this.month = month;
	}
	public void setTotalMoneySpent(BigDecimal totalMoneySpent) {
		this.totalMoneySpent = totalMoneySpent;
	}
	public int getMonth() {
		return month;
	}
	public BigDecimal getTotalMoneySpent() {
		return totalMoneySpent;
	}
}
