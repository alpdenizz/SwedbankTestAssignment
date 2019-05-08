package swedbank.TestAssignment.service;


import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

import swedbank.TestAssignment.domain.FuelConsumption;
import swedbank.TestAssignment.repository.FuelConsumptionRepository;
import swedbank.TestAssignment.repository.StatByMonthAndFuelType;
import swedbank.TestAssignment.repository.TotalSpentMoneyByMonth;

/**
 * 
 * Unit tests of Fuel Consumption Service
 * @author denizalp@ut.ee
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FuelConsumptionServiceTest {

	@Autowired
	private FuelConsumptionService service;
	
	@Autowired
	private FuelConsumptionRepository repository;
	
	private FuelConsumption fc1;
	
	private FuelConsumption fc2;
	
	private FuelConsumption fc3;
	
	private FuelConsumption fc4;
	
	/**
	 * Initialize some fuel consumptions for each test case
	 */
	@Before
	public void insertData() {
		fc1 = new FuelConsumption("Diesel",new BigDecimal(2),BigDecimal.TEN,
				LocalDateTime.parse("2019-04-01T11:00:00",DateTimeFormatter.ISO_DATE_TIME),"driver001");
		
		fc2 = new FuelConsumption("Diesel",BigDecimal.ONE,BigDecimal.TEN,
				LocalDateTime.parse("2019-04-02T11:00:00",DateTimeFormatter.ISO_DATE_TIME),"driver002");
		
		fc3 = new FuelConsumption("Diesel",BigDecimal.ONE,BigDecimal.TEN,
				LocalDateTime.parse("2019-05-03T11:00:00",DateTimeFormatter.ISO_DATE_TIME),"driver003");
		
		fc4 = new FuelConsumption("Diesel2",BigDecimal.ONE,BigDecimal.TEN,
				LocalDateTime.parse("2019-05-04T11:00:00",DateTimeFormatter.ISO_DATE_TIME),"driver001");
	}
	
	/**
	 * DB is emptied after a test is completed.
	 */
	@After
	public void clearDB() {
		repository.deleteAll();
	}
	
	/**
	 * Check if autowired beans are loaded successfully
	 */
	@Test
	public void test_beansNotNull() {
		assertThat(service).isNotNull();
		assertThat(repository).isNotNull();
	}
	
	/**
	 * Tested fuel consumption is not valid because driverID field is blank
	 * @see FuelConsumptionService#isValidObject(FuelConsumption)
	 */
	@Test
	public void test_validatorSuccessful() {
		FuelConsumption fc = new FuelConsumption();
		fc.setDate(LocalDateTime.now());
		fc.setDriverID(" \n");
		fc.setFuelType("Diesel");
		fc.setPricePerLitter(BigDecimal.ONE);
		fc.setVolume(BigDecimal.TEN);
		boolean isValid = false;
		try {
			isValid = service.isValidObject(fc);	
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		assertThat(isValid).isFalse();
	}
	
	/**
	 * Check FuelConsumption fc1 and fc2 are registered successfully
	 * <ul>
	 * <li>Table size must increase by 2</li>
	 * <li>Registered fuel consumptions must not be null and 
	 * 	must have a valid identifier</li>
	 * </ul>
	 * @see FuelConsumptionService#addFuelConsumption(FuelConsumption)
	 */
	@Test
	public void test_InsertionSuccessful() {
		int before = service.getAllFuelConsumptions().size();
		FuelConsumption inserted1 = service.addFuelConsumption(fc1);
		FuelConsumption inserted2 = service.addFuelConsumption(fc2);
		int after = service.getAllFuelConsumptions().size();
		
		assertThat(after-before).isEqualTo(2);
		assertThat(inserted1).isNotNull();
		assertThat(inserted2).isNotNull();
		assertThat(inserted1.getId()).isNotNull();
		assertThat(inserted2.getId()).isNotNull();
	}
	
	/**
	 * Check Fuel consumption fc with empty fields is not inserted in the table
	 * <ul>
	 * <li>Call must throw an exception</li>
	 * <li>That exception must be catched successfully and error flag must change</li>
	 * </ul>
	 * @see FuelConsumptionService#addFuelConsumption(FuelConsumption)
	 */
	@Test
	public void test_insertionNotSuccessful() {
		FuelConsumption fc = new FuelConsumption();
		boolean error = false;
		try {
			service.addFuelConsumption(fc);
		}
		catch(Exception e) {
			e.printStackTrace();
			assertThat(e).isNotNull();
			error = true;
		}
		
		assertThat(error).isTrue();
	}
	
	/**
	 * Check fuel consumptions from "example.csv" are inserted successfully
	 * <ul>
	 * <li>Table size must increase by 2</li>
	 * <li>Insertion must be according to the order in "example.csv"
	 * </ul>
	 * @throws Exception
	 * @see FuelConsumptionService#addFuelConsumptionsFromCsvFile(Scanner)
	 */
	@Test
	public void test_insertFromFileSuccessful() throws Exception {
		int before = service.getAllFuelConsumptions().size();
		
		Scanner s = new Scanner(new File("example.csv"));
		String response = service.addFuelConsumptionsFromCsvFile(s);
		assertThat(response).isEqualTo("Successful");
		
		List<FuelConsumption> result = service.getAllFuelConsumptions();
		int after = result.size();
		assertThat(after-before).isEqualTo(2);
		assertThat(result.get(0).getFuelType()).isEqualTo("Diesel");
		assertThat(result.get(1).getFuelType()).isEqualTo("98");
		
	}
	
	/**
	 * Check insertion from "bad1.csv" is not successful because
	 * in second row driverID column is empty
	 * <ul>
	 * <li>Result message must state the expected error</li>
	 * <li>There must be no change in FuelConsumption table</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test
	public void test_insertFromFileNotSuccessful() throws Exception {
		int before = service.getAllFuelConsumptions().size();
		
		Scanner s = new Scanner(new File("bad1.csv"));
		String response = service.addFuelConsumptionsFromCsvFile(s);
		assertThat(response).contains("Driver ID must not be empty");
		
		List<FuelConsumption> result = service.getAllFuelConsumptions();
		int after = result.size();
		assertThat(after-before).isEqualTo(0);
		
	}
	
	/**
	 * Check if fuel consumptions are retrieved successfully by month<br>
	 * Initially three fuel consumptions are registered where two of them
	 * has month value 4 and one has month value 5
	 * <ul>
	 * <li>Initial loading must be successful</li>
	 * <li>Query result must include elements with month 4 only</li>
	 * </ul>
	 * 
	 * @see FuelConsumptionService#findAllByMonth(int)
	 * @see FuelConsumptionServiceTest#insertData()
	 */
	@Test
	public void test_FindByMonthSuccessful() {
		int before = service.getAllFuelConsumptions().size();
		FuelConsumption inserted1 = service.addFuelConsumption(fc1);
		FuelConsumption inserted2 = service.addFuelConsumption(fc2);
		FuelConsumption inserted3 = service.addFuelConsumption(fc3);
		int after = service.getAllFuelConsumptions().size();
		
		assertThat(after-before).isEqualTo(3);
		
		List<FuelConsumption> result = service.findAllByMonth(4);
		assertThat(result.size()).isEqualTo(2);
		assertThat(result).contains(inserted1,inserted2);
		assertThat(result).doesNotContain(inserted3);
	}
	
	/**
	 * Check if fuel consumptions are retrieved successfully by month and driverID<br>
	 * Initially three fuel consumptions are registered with different driverID
	 * <ul>
	 * <li>Initial loading must be successful</li>
	 * <li>Query result must include elements with month 4 and driverID "driver001" only</li>
	 * </ul>
	 * 
	 * @see FuelConsumptionService#findAllByMonthForSingleDriver(int, String)
	 * @see FuelConsumptionServiceTest#insertData()
	 */
	@Test
	public void test_FindByMonthForSingleDriverSuccessful() {
		int before = service.getAllFuelConsumptions().size();
		FuelConsumption inserted1 = service.addFuelConsumption(fc1);
		FuelConsumption inserted2 = service.addFuelConsumption(fc2);
		FuelConsumption inserted3 = service.addFuelConsumption(fc3);
		int after = service.getAllFuelConsumptions().size();
		
		assertThat(after-before).isEqualTo(3);
		
		List<FuelConsumption> result = service.findAllByMonthForSingleDriver(4,"driver001");
		assertThat(result.size()).isEqualTo(1);
		assertThat(result).contains(inserted1);
		assertThat(result).doesNotContain(inserted2,inserted3);
	}
	
	/**
	 * Check if total prices grouped by month are retrieved successfully
	 * Initially three fuel consumptions are registered
	 * <ul>
	 * <li>Initial loading must be successful</li>
	 * <li>Query result must include two elements: one with month 4 and one with month 5</li>
	 * <li>Query result must also include the true total price for each element</li>
	 * </ul>
	 * 
	 * @see FuelConsumptionService#findTotalPricesGroupedByMonth()
	 * @see FuelConsumptionServiceTest#insertData()
	 */
	@Test
	public void test_findTotalPricesGroupedByMonthSuccessful() {
		int before = service.getAllFuelConsumptions().size();
		FuelConsumption inserted1 = service.addFuelConsumption(fc1);
		FuelConsumption inserted2 = service.addFuelConsumption(fc2);
		FuelConsumption inserted3 = service.addFuelConsumption(fc3);
		int after = service.getAllFuelConsumptions().size();
		
		assertThat(after-before).isEqualTo(3);
		List<TotalSpentMoneyByMonth> result = service.findTotalPricesGroupedByMonth();
		assertThat(result).isNotNull();
		
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getMonth()).isEqualTo(4);
		assertThat(result.get(0).getTotalMoneySpent()).isEqualTo(inserted1.getTotalPrice().add(inserted2.getTotalPrice()).setScale(2));
		assertThat(result.get(1).getMonth()).isEqualTo(5);
		assertThat(result.get(1).getTotalMoneySpent()).isEqualTo(inserted3.getTotalPrice().setScale(2));
		
	}
	
	/**
	 * Check if total prices grouped by month are retrieved successfully for driverID<br>
	 * Initially three fuel consumptions are registered
	 * <ul>
	 * <li>Initial loading must be successful</li>
	 * <li>Query result must include one element with month 4 and true total price</li>
	 * </ul>
	 * 
	 * @see FuelConsumptionService#findTotalPricesGroupedByMonthForSingleDriver(String)
	 * @see FuelConsumptionServiceTest#insertData()
	 */
	@Test
	public void test_findTotalPricesGroupedByMonthForSingleDriverSuccessful() {
		int before = service.getAllFuelConsumptions().size();
		FuelConsumption inserted1 = service.addFuelConsumption(fc1);
		service.addFuelConsumption(fc2);
		service.addFuelConsumption(fc3);
		int after = service.getAllFuelConsumptions().size();
		
		assertThat(after-before).isEqualTo(3);
		List<TotalSpentMoneyByMonth> result = service.findTotalPricesGroupedByMonthForSingleDriver("driver001");
		assertThat(result).isNotNull();
		
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getMonth()).isEqualTo(4);
		assertThat(result.get(0).getTotalMoneySpent()).isEqualTo(inserted1.getTotalPrice().setScale(2));
		
	}
	
	/**
	 * Check if statistics grouped by fuel type for each month is retrieved successfully<br>
	 * Initially four fuel consumptions are registered
	 * <ul>
	 * <li>Initial loading must be successful</li>
	 * <li>Query result must include three elements: one with month 4 and two with month 5</li>
	 * <li>Result element with month 4 must include true average of price per litter</li>
	 * <li>Result elements with month 5 must be one with fuel type "Diesel" and one with fuel type "Diesel2"</li>
	 * </ul>
	 * 
	 * @see FuelConsumptionService#getStatisticsGroupedByFuelType()
	 * @see FuelConsumptionServiceTest#insertData()
	 */
	@Test
	public void test_getStatisticsGroupedByFuelTypeSuccessful() {
		int before = service.getAllFuelConsumptions().size();
		service.addFuelConsumption(fc1);
		service.addFuelConsumption(fc2);
		service.addFuelConsumption(fc3);
		service.addFuelConsumption(fc4);
		int after = service.getAllFuelConsumptions().size();
		
		assertThat(after-before).isEqualTo(4);
		List<StatByMonthAndFuelType> result = service.getStatisticsGroupedByFuelType();
		assertThat(result).isNotEmpty();
		
		assertThat(result).hasSize(3);
		assertThat(result.get(0).getAveragePricePerLitter()).isEqualTo(1.5);
		assertThat(result.get(0).getMonth()).isEqualTo(4);
		assertThat(result.get(1).getFuelType()).isEqualTo("Diesel");
		assertThat(result.get(2).getFuelType()).isEqualTo("Diesel2");
	}
	
	/**
	 * Check if statistics grouped by fuel type for each month is retrieved successfully for driverID<br>
	 * Initially four fuel consumptions are registered
	 * <ul>
	 * <li>Initial loading must be successful</li>
	 * <li>Query result must include two elements: one with month 4 and one with month 5</li>
	 * <li>Result element with month 4 must include true average of price per litter</li>
	 * <li>Result element with month 5 must be with fuel type "Diesel2"</li>
	 * </ul>
	 * 
	 * @see FuelConsumptionService#getStatisticsGroupedByFuelTypeForSingleDriver(String)
	 * @see FuelConsumptionServiceTest#insertData()
	 */
	@Test
	public void test_getStatisticsGroupedByFuelTypeForSingleDriverSuccessful() {
		int before = service.getAllFuelConsumptions().size();
		service.addFuelConsumption(fc1);
		service.addFuelConsumption(fc2);
		service.addFuelConsumption(fc3);
		service.addFuelConsumption(fc4);
		int after = service.getAllFuelConsumptions().size();
		
		assertThat(after-before).isEqualTo(4);
		List<StatByMonthAndFuelType> result = service.getStatisticsGroupedByFuelTypeForSingleDriver("driver001");
		assertThat(result).isNotEmpty();
		
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getAveragePricePerLitter()).isEqualTo(2.0);
		assertThat(result.get(0).getMonth()).isEqualTo(4);
		assertThat(result.get(1).getAveragePricePerLitter()).isEqualTo(1.0);
		assertThat(result.get(1).getMonth()).isEqualTo(5);
		assertThat(result.get(1).getFuelType()).isEqualTo("Diesel2");
	}
	
}
