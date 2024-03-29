package swedbank.TestAssignment.resource;

import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import swedbank.TestAssignment.domain.FuelConsumption;
import swedbank.TestAssignment.repository.StatByMonthAndFuelType;
import swedbank.TestAssignment.repository.TotalSpentMoneyByMonth;
import swedbank.TestAssignment.service.FuelConsumptionService;

/**
 * 
 * @author denizalp@ut.ee
 * <p>Rest Controller for Fuel Consumption Management</p>
 */
@RestController
@Validated
@RequestMapping("/api/consumptions")
public class FuelConsumptionResource {

	@Autowired
	private FuelConsumptionService service;
	
	/**
	 * POST request for registering a fuel consumption
	 * @param fuelConsumption Request Body
	 * @return JSON representation of registered fuel consumption
	 */
	@PostMapping
	public FuelConsumption registerConsumption(@RequestBody FuelConsumption fuelConsumption) {
		return service.addFuelConsumption(fuelConsumption);
	}
	
	/**
	 * POST request with csv file to register fuel consumptions from the file
	 * @param file RequestParam including csv file
	 * @return result message of the operation
	 * @throws Exception
	 */
	@PostMapping(path="/file")
	public String registerFromFile(@RequestParam("file") MultipartFile file) throws Exception {
		Scanner s = new Scanner(file.getInputStream());
		return service.addFuelConsumptionsFromCsvFile(s);
	}
	
	/**
	 * 
	 * @param driverID optional RequestParam to narrow the results for a driver
	 * @return list of TotalSpentMoneyByMonth
	 * @see FuelConsumptionRepository
	 */
	@GetMapping("/totalSpentMoneyByMonth")
	public List<TotalSpentMoneyByMonth> getTotalSpentMoneyByMonth(@RequestParam(value="driver",required=false) String driverID) {
		if(driverID != null) return service.findTotalPricesGroupedByMonthForSingleDriver(driverID);
		else return service.findTotalPricesGroupedByMonth();
	}
	
	/**
	 * 
	 * @param month optional RequestParam to narrow the results for a month
	 * @param driverID optional RequestParam to narrow the results for a driver
	 * @return list of FuelConsumption
	 * @see FuelConsumptionRepository
	 */
	@GetMapping
	public List<FuelConsumption> getConsumptionsForMonth(@RequestParam(value="month", required=false) Integer month, @RequestParam(value="driver",required=false) String driverID) {
		if(driverID != null && month != null) return service.findAllByMonthForSingleDriver(month, driverID);
		else if(month != null) return service.findAllByMonth(month);
		else return service.getAllFuelConsumptions();
	}
	
	/**
	 * 
	 * @param driverID optional RequestParam to narrow the results for a driver
	 * @return list of StatByMonthAndFuelType
	 * @see FuelConsumptionRepository
	 */
	@GetMapping("/statsByFuelType")
	public List<StatByMonthAndFuelType> getStatsByFuelType(@RequestParam(value="driver", required=false) String driverID) {
		if(driverID != null) return service.getStatisticsGroupedByFuelTypeForSingleDriver(driverID);
		else return service.getStatisticsGroupedByFuelType();
	}
	
}
