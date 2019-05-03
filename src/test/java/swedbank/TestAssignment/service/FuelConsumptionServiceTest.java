package swedbank.TestAssignment.service;


import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import javax.validation.Validator;

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
	
	@Before
	public void insertData() {
		fc1 = new FuelConsumption("Diesel",new BigDecimal(2),BigDecimal.TEN,
				LocalDateTime.now().minusDays(6),"driver001");
		
		fc2 = new FuelConsumption("Diesel",BigDecimal.ONE,BigDecimal.TEN,
				LocalDateTime.now().minusDays(8),"driver002");
		
		fc3 = new FuelConsumption("Diesel",BigDecimal.ONE,BigDecimal.TEN,
				LocalDateTime.now(),"driver003");
		
		fc4 = new FuelConsumption("Diesel2",BigDecimal.ONE,BigDecimal.TEN,
				LocalDateTime.now(),"driver001");
	}
	
	/**
	 * DB is emptied after a test is completed.
	 */
	@After
	public void clearDB() {
		repository.deleteAll();
	}
	
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
		assertThat(result.get(1).getMonth()).isEqualTo(5);
		
	}
	
	@Test
	public void test_findTotalPricesGroupedByMonthForSingleDriverSuccessful() {
		int before = service.getAllFuelConsumptions().size();
		FuelConsumption inserted1 = service.addFuelConsumption(fc1);
		FuelConsumption inserted2 = service.addFuelConsumption(fc2);
		FuelConsumption inserted3 = service.addFuelConsumption(fc3);
		int after = service.getAllFuelConsumptions().size();
		
		assertThat(after-before).isEqualTo(3);
		List<TotalSpentMoneyByMonth> result = service.findTotalPricesGroupedByMonthForSingleDriver("driver001");
		assertThat(result).isNotNull();
		
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getMonth()).isEqualTo(4);
		
	}
	
	@Test
	public void test_getStatisticsGroupedByFuelTypeSuccessful() {
		int before = service.getAllFuelConsumptions().size();
		FuelConsumption inserted1 = service.addFuelConsumption(fc1);
		FuelConsumption inserted2 = service.addFuelConsumption(fc2);
		FuelConsumption inserted3 = service.addFuelConsumption(fc3);
		FuelConsumption inserted4 = service.addFuelConsumption(fc4);
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
	
	@Test
	public void test_getStatisticsGroupedByFuelTypeForSingleDriverSuccessful() {
		int before = service.getAllFuelConsumptions().size();
		FuelConsumption inserted1 = service.addFuelConsumption(fc1);
		FuelConsumption inserted2 = service.addFuelConsumption(fc2);
		FuelConsumption inserted3 = service.addFuelConsumption(fc3);
		FuelConsumption inserted4 = service.addFuelConsumption(fc4);
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
