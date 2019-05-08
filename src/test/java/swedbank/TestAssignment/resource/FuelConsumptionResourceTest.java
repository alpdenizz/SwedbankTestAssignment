package swedbank.TestAssignment.resource;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import swedbank.TestAssignment.domain.FuelConsumption;
import swedbank.TestAssignment.repository.FuelConsumptionRepository;
import swedbank.TestAssignment.repository.StatByMonthAndFuelType;
import swedbank.TestAssignment.repository.TotalSpentMoneyByMonth;
import swedbank.TestAssignment.service.FuelConsumptionService;

/**
 * 
 * Integration tests of Fuel Consumption Resource
 * @author denizalp@ut.ee
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FuelConsumptionResourceTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private FuelConsumptionService service;
	
	@Autowired
	private FuelConsumptionRepository repository;
	
	@Autowired
	private ObjectMapper om;
	
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
		assertThat(mvc).isNotNull();
		assertThat(service).isNotNull();
		assertThat(repository).isNotNull();
		assertThat(om).isNotNull();
	}
	
	/**
	 * Check POST request with valid fuel consumption request body succeeded
	 * <ul>
	 * <li>Response status must be OK(200)</li>
	 * <li>Response JSON must have mapping "fuelType": "Diesel"</li>
	 * <li>Response JSON must have mapping "driverID": "driver001"</li>
	 * <li>Table size must increase by 1</li>
	 * </ul>
	 * @throws Exception
	 * @see FuelConsumptionResource#registerConsumption(FuelConsumption)
	 */
	@Test
	public void test_insertionSuccessful() throws Exception {
		int before = service.getAllFuelConsumptions().size();
		
		FuelConsumption fc = new FuelConsumption();
		fc.setDate(LocalDateTime.now().minusDays(1));
		fc.setDriverID("driver001");
		fc.setFuelType("Diesel");
		fc.setPricePerLitter(BigDecimal.ONE);
		fc.setVolume(BigDecimal.TEN);
		
		mvc.perform(post("/api/consumptions")
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(fc)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.fuelType",is("Diesel")))
			.andExpect(jsonPath("$.driverID",is("driver001")));
		
		int after = service.getAllFuelConsumptions().size();
		assertThat(after-before).isEqualTo(1);
	}
	
	/**
	 * Check POST request with csv file succeeded
	 * <ul>
	 * <li>Response status must be OK(200)</li>
	 * <li>Table size must increase by 2</li>
	 * </ul>
	 * @throws Exception
	 * @see FuelConsumptionResource#registerFromFile(org.springframework.web.multipart.MultipartFile)
	 */
	@Test
	public void test_insertionFromFileSuccessful() throws Exception{
		int before = service.getAllFuelConsumptions().size();
		StringBuilder sb = new StringBuilder();
		sb.append("FuelType;PricePerLitter;Volume;Date;DriverID\n");
		sb.append("Diesel;1.5;100;2019-04-01T11:00:00;driver001\n");
		sb.append("98;2;50;2019-04-01T13:30:00;driver002\n");
		
		MockMultipartFile file = new MockMultipartFile("file","fgfmglkfmgkl.csv","text/csv",sb.toString().getBytes());
		mvc.perform(multipart("/api/consumptions/file")
				.file(file))
				.andExpect(status().isOk());
		
		int after = service.getAllFuelConsumptions().size();
		assertThat(after-before).isEqualTo(2);
	}
	
	/**
	 * Check GET request to retrieve total spent money by month succeeded both with
	 * driverID and without<br>
	 * Response JSON must include the true query results
	 * @throws Exception
	 */
	@Test
	public void test_totalSpentMoneyByMonth() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("FuelType;PricePerLitter;Volume;Date;DriverID\n");
		sb.append("Diesel;1.5;100;2019-04-01T11:00:00;driver001\n");
		sb.append("98;2;50;2019-04-01T13:30:00;driver002\n");
		sb.append("98;2;50;2019-05-01T13:30:00;driver001\n");
		sb.append("98;2;50;2019-05-02T13:30:00;driver002\n");
		
		MockMultipartFile file = new MockMultipartFile("file","fgfmglkfmgkl.csv","text/csv",sb.toString().getBytes());
		mvc.perform(multipart("/api/consumptions/file")
				.file(file))
				.andExpect(status().isOk());
		
		TotalSpentMoneyByMonth o1 = new TotalSpentMoneyByMonth(4,new BigDecimal(250).setScale(2));
		TotalSpentMoneyByMonth o2 = new TotalSpentMoneyByMonth(5,new BigDecimal(200).setScale(2));
		mvc.perform(get("/api/consumptions/totalSpentMoneyByMonth"))
			.andExpect(status().isOk())
			.andExpect(content().json("["+om.writeValueAsString(o1)+","+om.writeValueAsString(o2)+"]"));
	
		o1.setTotalMoneySpent(new BigDecimal(150).setScale(2));
		o2.setTotalMoneySpent(new BigDecimal(100).setScale(2));
		mvc.perform(get("/api/consumptions/totalSpentMoneyByMonth?driver=driver001"))
			.andExpect(status().isOk())
			.andExpect(content().json("["+om.writeValueAsString(o1)+","+om.writeValueAsString(o2)+"]"));
	
	}
	
	/**
	 * Check GET request to retrieve consumptions by month succeeded both with
	 * driverID and without<br>
	 * Response JSON must include the true query results
	 * @throws Exception
	 */
	@Test
	public void test_getConsumptionsForMonth() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("FuelType;PricePerLitter;Volume;Date;DriverID\n");
		sb.append("Diesel;1.5;100;2019-04-01T11:00:00;driver001\n");
		sb.append("98;2;50;2019-04-01T13:30:00;driver002\n");
		sb.append("98;2;50;2019-05-01T13:30:00;driver001\n");
		sb.append("98;2;50;2019-05-02T14:30:00;driver002\n");
		
		MockMultipartFile file = new MockMultipartFile("file","fgfmglkfmgkl.csv","text/csv",sb.toString().getBytes());
		mvc.perform(multipart("/api/consumptions/file")
				.file(file))
				.andExpect(status().isOk());
		
		FuelConsumption fc1 = new FuelConsumption();
		fc1.setDate(LocalDateTime.parse("2019-04-01T11:00:00", DateTimeFormatter.ISO_DATE_TIME));
		fc1.setDriverID("driver001");
		fc1.setFuelType("Diesel");
		fc1.setPricePerLitter(new BigDecimal(1.5));
		fc1.setVolume(new BigDecimal(100));
		fc1.setId(1l);
		
		FuelConsumption fc2 = new FuelConsumption();
		fc2.setDate(LocalDateTime.parse("2019-04-01T13:30:00", DateTimeFormatter.ISO_DATE_TIME));
		fc2.setDriverID("driver002");
		fc2.setFuelType("98");
		fc2.setPricePerLitter(new BigDecimal(2));
		fc2.setVolume(new BigDecimal(50));
		fc2.setId(2l);
		
		mvc.perform(get("/api/consumptions?month=4"))
		.andExpect(status().isOk())
		.andExpect(content().json("["+om.writeValueAsString(fc1)+","+om.writeValueAsString(fc2)+"]"));
	
		mvc.perform(get("/api/consumptions?month=4&driver=driver001"))
		.andExpect(status().isOk())
		.andExpect(content().json("["+om.writeValueAsString(fc1)+"]"));
		
		//FuelConsumption fc1 = new FuelConsumption();
		fc1.setDate(LocalDateTime.parse("2019-05-01T13:30:00", DateTimeFormatter.ISO_DATE_TIME));
		fc1.setDriverID("driver001");
		fc1.setFuelType("98");
		fc1.setPricePerLitter(new BigDecimal(2));
		fc1.setVolume(new BigDecimal(50));
		fc1.setId(3l);
		
		//FuelConsumption fc2 = new FuelConsumption();
		fc2.setDate(LocalDateTime.parse("2019-05-02T14:30:00", DateTimeFormatter.ISO_DATE_TIME));
		fc2.setDriverID("driver002");
		fc2.setFuelType("98");
		fc2.setPricePerLitter(new BigDecimal(2));
		fc2.setVolume(new BigDecimal(50));
		fc2.setId(4l);
		
		mvc.perform(get("/api/consumptions?month=5"))
		.andExpect(status().isOk())
		.andExpect(content().json("["+om.writeValueAsString(fc1)+","+om.writeValueAsString(fc2)+"]"));
	
		mvc.perform(get("/api/consumptions?month=5&driver=driver002"))
		.andExpect(status().isOk())
		.andExpect(content().json("["+om.writeValueAsString(fc2)+"]"));
	}
	
	/**
	 * Check GET request to retrieve statistics grouped by fuel type
	 * for each month succeeded both with
	 * driverID and without<br>
	 * Response JSON must include the true query results
	 * @throws Exception
	 */
	@Test
	public void test_statsByFuelType() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("FuelType;PricePerLitter;Volume;Date;DriverID\n");
		sb.append("Diesel;1.5;100;2019-04-01T11:00:00;driver001\n");
		sb.append("Diesel;2.5;80;2019-04-01T11:30:00;driver002\n");
		sb.append("98;2;50;2019-04-01T13:30:00;driver001\n");
		sb.append("98;2;50;2019-05-01T13:30:00;driver002\n");
		sb.append("95;2;50;2019-05-02T14:30:00;driver001\n");
		sb.append("95;1;30;2019-05-02T15:30:00;driver002\n");
		
		MockMultipartFile file = new MockMultipartFile("file","fgfmglkfmgkl.csv","text/csv",sb.toString().getBytes());
		mvc.perform(multipart("/api/consumptions/file")
				.file(file))
				.andExpect(status().isOk());
		
		StatByMonthAndFuelType s1 = new StatByMonthAndFuelType();
		s1.setAveragePricePerLitter(2);
		s1.setFuelType("Diesel");
		s1.setMonth(4);
		s1.setTotalVolume(new BigDecimal(180));
		s1.setTotalPrice(new BigDecimal(350));
		
		StatByMonthAndFuelType s2 = new StatByMonthAndFuelType();
		s2.setAveragePricePerLitter(2);
		s2.setFuelType("98");
		s2.setMonth(4);
		s2.setTotalVolume(new BigDecimal(50));
		s2.setTotalPrice(new BigDecimal(100));
		
		StatByMonthAndFuelType s3 = new StatByMonthAndFuelType();
		s3.setAveragePricePerLitter(2);
		s3.setFuelType("98");
		s3.setMonth(5);
		s3.setTotalVolume(new BigDecimal(50));
		s3.setTotalPrice(new BigDecimal(100));
		
		StatByMonthAndFuelType s4 = new StatByMonthAndFuelType();
		s4.setAveragePricePerLitter(1.5);
		s4.setFuelType("95");
		s4.setMonth(5);
		s4.setTotalVolume(new BigDecimal(80));
		s4.setTotalPrice(new BigDecimal(130));
		
		mvc.perform(get("/api/consumptions/statsByFuelType"))
		.andExpect(status().isOk())
		.andExpect(content().json("["+om.writeValueAsString(s1)+","+om.writeValueAsString(s2)
				+","+om.writeValueAsString(s3)+","+om.writeValueAsString(s4)+"]"));
		
		s1.setAveragePricePerLitter(1.5);
		s1.setFuelType("Diesel");
		s1.setMonth(4);
		s1.setTotalVolume(new BigDecimal(100));
		s1.setTotalPrice(new BigDecimal(150));
		
		s2.setAveragePricePerLitter(2);
		s2.setFuelType("98");
		s2.setMonth(4);
		s2.setTotalVolume(new BigDecimal(50));
		s2.setTotalPrice(new BigDecimal(100));
		
		s3.setAveragePricePerLitter(2);
		s3.setFuelType("95");
		s3.setMonth(5);
		s3.setTotalVolume(new BigDecimal(50));
		s3.setTotalPrice(new BigDecimal(100));
		
		mvc.perform(get("/api/consumptions/statsByFuelType?driver=driver001"))
		.andExpect(status().isOk())
		.andExpect(content().json("["+om.writeValueAsString(s1)+","+om.writeValueAsString(s2)
				+","+om.writeValueAsString(s3)+"]"));
		
		s1.setAveragePricePerLitter(2.5);
		s1.setFuelType("Diesel");
		s1.setMonth(4);
		s1.setTotalVolume(new BigDecimal(80));
		s1.setTotalPrice(new BigDecimal(200));
		
		s2.setAveragePricePerLitter(2);
		s2.setFuelType("98");
		s2.setMonth(5);
		s2.setTotalVolume(new BigDecimal(50));
		s2.setTotalPrice(new BigDecimal(100));
		
		s3.setAveragePricePerLitter(1);
		s3.setFuelType("95");
		s3.setMonth(5);
		s3.setTotalVolume(new BigDecimal(30));
		s3.setTotalPrice(new BigDecimal(30));
		
		mvc.perform(get("/api/consumptions/statsByFuelType?driver=driver002"))
		.andExpect(status().isOk())
		.andExpect(content().json("["+om.writeValueAsString(s1)+","+om.writeValueAsString(s2)
				+","+om.writeValueAsString(s3)+"]"));
	}

}