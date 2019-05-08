package swedbank.TestAssignment.repository;

import java.math.BigDecimal;

/**
 * 
 * @author denizalp@ut.ee
 * @see FuelConsumptionRepository
 */
public class StatByMonthAndFuelType {
	
	/**
	 * Fuel type in FuelConsumption entity
	 */
	private String fuelType;
	
	private BigDecimal totalVolume;
	
	private double averagePricePerLitter;
	
	private BigDecimal totalPrice;
	
	private int month;
	
	public StatByMonthAndFuelType(String fuelType, BigDecimal totalVolume,
			double averagePricePerLitter, BigDecimal totalPrice, int month) {
		this.fuelType = fuelType;
		this.totalVolume = totalVolume;
		this.averagePricePerLitter = averagePricePerLitter;
		this.totalPrice = totalPrice;
		this.month = month;
	}
	
	public StatByMonthAndFuelType() {}
	
	public void setAveragePricePerLitter(double averagePricePerLitter) {
		this.averagePricePerLitter = averagePricePerLitter;
	}
	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public void setTotalVolume(BigDecimal totalVolume) {
		this.totalVolume = totalVolume;
	}
	public double getAveragePricePerLitter() {
		return averagePricePerLitter;
	}
	public String getFuelType() {
		return fuelType;
	}
	public int getMonth() {
		return month;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public BigDecimal getTotalVolume() {
		return totalVolume;
	}

}
