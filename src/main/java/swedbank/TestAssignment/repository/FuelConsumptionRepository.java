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
	
	/**
	 * 
	 * @param month from 1 to 12 representing months
	 * @return list of fuel consumptions that have been registered at param month
	 */
	@Query("select fc from FuelConsumption fc where fc.month = ?1")
	List<FuelConsumption> findAllByMonth(int month);
	
	/**
	 * 
	 * @param month from 1 to 12 representing months
	 * @param driverID driver identifier
	 * @return list of fuel consumptions that have been registered at param month
	 * for param driverID
	 */
	@Query("select fc from FuelConsumption fc where fc.month = ?1 and fc.driverID = ?2")
	List<FuelConsumption> findAllByMonthForSingleDriver(int month, String driverID);
	
	/**
	 * @return list of TotalSpentMoneyByMonth<br>
	 * TotalSpentMoneyByMonth is a class with fields month and total price spent on that month<br>
	 * This class is used because it is easier to operate the result with this than an Object[]
	 */
	@Query("select new swedbank.TestAssignment.repository.TotalSpentMoneyByMonth(fc.month, sum(fc.totalPrice)) from FuelConsumption fc group by fc.month")
	List<TotalSpentMoneyByMonth> findTotalPricesGroupedByMonth();
	
	/**
	 * @param driverID driver identifier
	 * @return list of TotalSpentMoneyByMonth for param driver<br>
	 * TotalSpentMoneyByMonth is a class with fields month and total price spent on that month<br>
	 * This class is used because it is easier to operate the result with this than an Object[]
	 */
	@Query("select new swedbank.TestAssignment.repository.TotalSpentMoneyByMonth(fc.month, sum(fc.totalPrice)) from FuelConsumption fc where fc.driverID = ?1 group by fc.month")
	List<TotalSpentMoneyByMonth> findTotalPricesGroupedByMonthForSingleDriver(String driverID);
	
	/**
	 * 
	 * @return list of StatByMonthAndFuelType<br>
	 * StatByMonthAndFuelType is a class with fields fuel type, total volume for this fuel type, average price per litter
	 * for this fuel type, total price spent on this fuel type and month for the calculations<br>
	 * This class is used because it is easier to operate the result with this than an Object[]
	 */
	@Query("select new swedbank.TestAssignment.repository.StatByMonthAndFuelType(fc.fuelType, sum(fc.volume), avg(fc.pricePerLitter), sum(fc.totalPrice), fc.month) from FuelConsumption fc group by fc.month, fc.fuelType")
	List<StatByMonthAndFuelType> getStatisticsGroupedByFuelType();
	
	/**
	 * 
	 * @param driverID driver identifier
	 * @return list of StatByMonthAndFuelType for param driver<br>
	 * StatByMonthAndFuelType is a class with fields fuel type, total volume for this fuel type, average price per litter
	 * for this fuel type, total price spent on this fuel type and month for the calculations<br>
	 * This class is used because it is easier to operate the result with this than an Object[]
	 *
	 */
	@Query("select new swedbank.TestAssignment.repository.StatByMonthAndFuelType(fc.fuelType, sum(fc.volume), avg(fc.pricePerLitter), sum(fc.totalPrice), fc.month) from FuelConsumption fc where fc.driverID = ?1 group by fc.month, fc.fuelType")
	List<StatByMonthAndFuelType> getStatisticsGroupedByFuelTypeForSingleDriver(String driverID);
	
}
