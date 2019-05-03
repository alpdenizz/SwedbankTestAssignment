package swedbank.TestAssignment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import swedbank.TestAssignment.domain.FuelConsumption;
import swedbank.TestAssignment.repository.FuelConsumptionRepository;
import swedbank.TestAssignment.repository.StatByMonthAndFuelType;
import swedbank.TestAssignment.repository.TotalSpentMoneyByMonth;

/**
 * 
 * @author denizalp@ut.ee
 * <p>Defined Fuel Consumption Management operations with database</p>
 *
 */
@Service
@Validated
public class FuelConsumptionService {
	
	@Autowired
	private FuelConsumptionRepository repo;
	
	public FuelConsumption addFuelConsumption(@Valid FuelConsumption fc) {
		//System.out.println("Trying to add fc");
		return repo.save(fc);
	}
	
	public List<FuelConsumption> addFuelConsumptionList(List<FuelConsumption> list) {
		return repo.saveAll(list);
	}
	
	public boolean isValidObject(FuelConsumption fc) throws Exception {
		if(fc.getDate() == null) throw new Exception("Date must not be null");
		if(fc.getDriverID() == null || fc.getDriverID().trim().isEmpty()) throw new Exception("Driver ID must not be empty");
		if(fc.getFuelType() == null || fc.getFuelType().trim().isEmpty()) throw new Exception("FuelType must not be empty");
		if(fc.getPricePerLitter() == null || fc.getPricePerLitter().compareTo(BigDecimal.ZERO) <= 0) throw new Exception("PricePerLitter must be positive");
		if(fc.getVolume() == null || fc.getVolume().compareTo(BigDecimal.ZERO) <= 0) throw new Exception("Volume must be positive");
		return true;
	}
	
	public String addFuelConsumptionsFromCsvFile(Scanner s) {
		String[] columns = null;
		int row = 0;
		if(s.hasNextLine()) {
			row++;
			columns = s.nextLine().split(";");
		}
		List<FuelConsumption> list = new ArrayList<FuelConsumption>();
		while(s.hasNextLine()) {
			row++;
			String line = s.nextLine();
			//System.out.println(line);
			String[] values = line.split(";");
			if(columns.length != values.length) {
				return "Value column size must be equal to Attribute column size";
			}
			else {
				FuelConsumption fc = new FuelConsumption();
				int column = 0;
				for(int i=0; i<columns.length; i++) {
					column = i+1;
					if(columns[i].equalsIgnoreCase("FuelType")) {
						fc.setFuelType(values[i]);
					}
					if(columns[i].equalsIgnoreCase("PricePerLitter")) {
						fc.setPricePerLitter(new BigDecimal(values[i]));
					}
					if(columns[i].equalsIgnoreCase("Volume")) {
						fc.setVolume(new BigDecimal(values[i]));
					}
					if(columns[i].equalsIgnoreCase("Date")) {
						DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME; 
						LocalDateTime dateTime = LocalDateTime.parse(values[i], formatter);
						fc.setDate(dateTime);
					}
					if(columns[i].equalsIgnoreCase("DriverID")) {
						fc.setDriverID(values[i]);
					}
				}
				boolean isValidObject = false;
				try {
					isValidObject = isValidObject(fc);
				}
				catch(Exception e) {
					return "In row: "+row+" column: "+column+" error: "+e.getMessage();
				}
				//ystem.out.println("Driver: "+fc.getDriverID());
				if(isValidObject) list.add(fc);
			}
		}
		s.close();
		addFuelConsumptionList(list);
		return "Successful";
	}
	
	public List<FuelConsumption> findAllByMonth(int month) {
		return repo.findAllByMonth(month);
	}
	
	public List<FuelConsumption> findAllByMonthForSingleDriver(int month, String driverID) {
		return repo.findAllByMonthForSingleDriver(month,driverID);
	}
	
	public List<TotalSpentMoneyByMonth> findTotalPricesGroupedByMonth() {
		return repo.findTotalPricesGroupedByMonth();
	}
	
	public List<TotalSpentMoneyByMonth> findTotalPricesGroupedByMonthForSingleDriver(String driverID) {
		return repo.findTotalPricesGroupedByMonthForSingleDriver(driverID);
	}
	
	public List<StatByMonthAndFuelType> getStatisticsGroupedByFuelType() {
		return repo.getStatisticsGroupedByFuelType();
	}
	
	public List<StatByMonthAndFuelType> getStatisticsGroupedByFuelTypeForSingleDriver(String driverID) {
		return repo.getStatisticsGroupedByFuelTypeForSingleDriver(driverID);
	}
	
	public List<FuelConsumption> getAllFuelConsumptions() {
		return repo.findAll();
	}

}
