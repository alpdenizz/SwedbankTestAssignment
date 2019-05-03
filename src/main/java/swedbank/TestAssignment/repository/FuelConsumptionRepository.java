package swedbank.TestAssignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import swedbank.TestAssignment.domain.FuelConsumption;

/**
 * 
 * @author denizalp@ut.ee
 * <p>Repository methods to operate FuelConsumption table</p>
 */
public interface FuelConsumptionRepository extends JpaRepository<FuelConsumption, Long> {
	
	@Query("select fc from FuelConsumption fc where fc.month = ?1")
	List<FuelConsumption> findAllByMonth(int month);
	
	@Query("select fc from FuelConsumption fc where fc.month = ?1 and fc.driverID = ?2")
	List<FuelConsumption> findAllByMonthForSingleDriver(int month, String driverID);
	
	@Query("select new swedbank.TestAssignment.repository.TotalSpentMoneyByMonth(fc.month, sum(fc.totalPrice)) from FuelConsumption fc group by fc.month")
	List<TotalSpentMoneyByMonth> findTotalPricesGroupedByMonth();
	
	@Query("select new swedbank.TestAssignment.repository.TotalSpentMoneyByMonth(fc.month, sum(fc.totalPrice)) from FuelConsumption fc where fc.driverID = ?1 group by fc.month")
	List<TotalSpentMoneyByMonth> findTotalPricesGroupedByMonthForSingleDriver(String driverID);
	
	@Query("select new swedbank.TestAssignment.repository.StatByMonthAndFuelType(fc.fuelType, sum(fc.volume), avg(fc.pricePerLitter), sum(fc.totalPrice), fc.month) from FuelConsumption fc group by fc.month, fc.fuelType")
	List<StatByMonthAndFuelType> getStatisticsGroupedByFuelType();
	
	@Query("select new swedbank.TestAssignment.repository.StatByMonthAndFuelType(fc.fuelType, sum(fc.volume), avg(fc.pricePerLitter), sum(fc.totalPrice), fc.month) from FuelConsumption fc where fc.driverID = ?1 group by fc.month, fc.fuelType")
	List<StatByMonthAndFuelType> getStatisticsGroupedByFuelTypeForSingleDriver(String driverID);
	
}
