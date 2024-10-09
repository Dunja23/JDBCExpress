package student;
import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;


public class StudentMain {

    public static void main(String[] args) {
        CityOperations cityOperations = new City(); // Change this to your implementation.
        DistrictOperations districtOperations = new District(); // Do it for all classes.
        CourierOperations courierOperations = new Courier(); // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new CourierRequest();
        GeneralOperations generalOperations = new General();
        UserOperations userOperations = new User();
        VehicleOperations vehicleOperations = new Vehicle();
        PackageOperations packageOperations = new Package();

        TestHandler.createInstance(
                cityOperations,
                courierOperations,
                courierRequestOperation,
                districtOperations,
                generalOperations,
                userOperations,
                vehicleOperations,
                packageOperations);

        TestRunner.runTests();
    }
}
