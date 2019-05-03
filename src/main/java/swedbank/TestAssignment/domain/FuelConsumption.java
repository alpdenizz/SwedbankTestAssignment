package swedbank.TestAssignment.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 
 * @author denizalp@ut.ee
 * <p>Fuel consumption entity class</p>
 *
 */
@Entity
public class FuelConsumption {

	@Id
	@GeneratedValue
	private long id;
	
	@NotBlank(message="Fuel type must not be empty")
	private String fuelType;
	
	@Positive(message="Price per letter must be positive")
	@NotNull(message="Price per letter must not be null")
	private BigDecimal pricePerLitter;
	
	@Positive(message="Volume must be positive")
	@NotNull(message="Volume must not be null")
	private BigDecimal volume;
	
	@NotNull(message="Date must not be null")
	private LocalDateTime date;
	
	@NotBlank(message="Driver id must not be empty")
	private String driverID;
	
	/**
	 * It is computed by pricePerLitter * volume
	 */
	private BigDecimal totalPrice;
	
	/**
	 * Month of date field between 1 and 12
	 */
	private int month;
	
	public FuelConsumption(String fuelType, BigDecimal pricePerLitter,
			BigDecimal volume, LocalDateTime date, String driverID) {
		this.fuelType = fuelType;
		this.pricePerLitter = pricePerLitter;
		this.volume = volume;
		this.date = date;
		this.driverID = driverID;
		this.month = this.date.getMonthValue();
		this.totalPrice = this.pricePerLitter.multiply(this.volume);
	}
	
	public FuelConsumption() {}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public BigDecimal getPricePerLitter() {
		return pricePerLitter;
	}

	public void setPricePerLitter(BigDecimal pricePerLitter) {
		this.pricePerLitter = pricePerLitter;
		if(this.volume != null) setTotalPrice();
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
		if(this.pricePerLitter != null) setTotalPrice();
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
		this.month = this.date.getMonthValue();
	}

	public String getDriverID() {
		return driverID;
	}

	public void setDriverID(String driverID) {
		this.driverID = driverID;
	}
	
	public void setTotalPrice() {
		this.totalPrice = this.pricePerLitter.multiply(this.volume);
	}
	
	public BigDecimal getTotalPrice() {
		return this.totalPrice;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public int getMonth() {
		return this.month;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj == null) return false;
		else if(obj instanceof FuelConsumption) {
			FuelConsumption fc = (FuelConsumption) obj;
			return fc.getId() == this.getId();
		}
		return false;
	}
}
