// Main.java

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Hospital hospital = new Hospital("St. Mary's Hospital", "123 Medical Center Dr");

        // Create departments
        Department cardiology = new Department("Cardiology", "D001", 20);
        Department neurology = new Department("Neurology", "D002", 15);
        Department emergency = new Department("Emergency", "D003", 30);

        hospital.addDepartment(cardiology);
        hospital.addDepartment(neurology);
        hospital.addDepartment(emergency);

        // Create doctors
        Doctor dr1 = new Doctor("Dr. Sarah Johnson", "DOC001", "Cardiology", 15);
        Doctor dr2 = new Doctor("Dr. Michael Chen", "DOC002", "Neurology", 10);
        Doctor dr3 = new Doctor("Dr. Emily Davis", "DOC003", "Emergency", 8);

        hospital.registerDoctor(dr1, cardiology);
        hospital.registerDoctor(dr2, neurology);
        hospital.registerDoctor(dr3, emergency);

        // Create patients
        Patient patient1 = new Patient("John Smith", "P001", 45, "O+");
        Patient patient2 = new Patient("Mary Williams", "P002", 62, "A+");
        Patient patient3 = new Patient("James Brown", "P003", 38, "B-");

        hospital.registerPatient(patient1);
        hospital.registerPatient(patient2);
        hospital.registerPatient(patient3);

        // Schedule appointments
        System.out.println("=== Hospital Management System ===");
        System.out.println("Hospital: " + hospital.getName());
        System.out.println();

        Appointment apt1 = hospital.scheduleAppointment(patient1, dr1, "Routine checkup");
        Appointment apt2 = hospital.scheduleAppointment(patient2, dr2, "Follow-up consultation");
        Appointment apt3 = hospital.scheduleAppointment(patient3, dr3, "Emergency visit");

        System.out.println("\n--- Scheduled Appointments ---");
        hospital.displayAppointments();

        System.out.println("\n--- Processing Appointments ---");
        hospital.processAppointment(apt1, "Patient shows stable heart rate. Prescribed medication.");
        hospital.processAppointment(apt2, "Brain scan results normal. Continue current treatment.");

        System.out.println("\n--- Doctor Workload ---");
        hospital.displayDoctorWorkload();

        System.out.println("\n--- Patient Medical History ---");
        patient1.displayMedicalHistory();

        //DeadCodeStart
        // Create billing system but never use it
        BillingSystem billing = new BillingSystem(hospital);
        double cost = billing.calculateAppointmentCost(apt1);
        Invoice invoice = billing.generateInvoice(patient1, apt1);
        //DeadCodeEnd

        //DeadCodeStart
        // Analytics initialization
        HospitalAnalytics analytics = new HospitalAnalytics(hospital);
        analytics.generateOccupancyReport();
        double avgPatientAge = analytics.calculateAveragePatientAge();
        //DeadCodeEnd
    }
}

// Department.java
class Department {
    private String name;
    private String deptId;
    private int capacity;
    private List<Doctor> doctors;
    private int currentPatients;

    public Department(String name, String deptId, int capacity) {
        this.name = name;
        this.deptId = deptId;
        this.capacity = capacity;
        this.doctors = new ArrayList<>();
        this.currentPatients = 0;
    }

    public String getName() {
        return name;
    }

    //DeadCodeStart
    public String getDeptId() {
        return deptId;
    }
    //DeadCodeEnd

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }

    //DeadCodeStart
    public List<Doctor> getDoctors() {
        return doctors;
    }

    public boolean hasCapacity() {
        return currentPatients < capacity;
    }

    public void incrementPatientCount() {
        currentPatients++;
    }

    public void decrementPatientCount() {
        if (currentPatients > 0) currentPatients--;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int newCapacity) {
        // Never called
        this.capacity = newCapacity;
        System.out.println("Capacity updated to: " + newCapacity);
    }

    public int getCurrentPatients() {
        return currentPatients;
    }

    public double getUtilizationRate() {
        // Unused calculation method
        if (capacity == 0) return 0.0;
        return (double) currentPatients / capacity * 100;
    }

    public List<Doctor> getAvailableDoctors() {
        // Complex dead code for finding available doctors
        List<Doctor> available = new ArrayList<>();
        for (Doctor doc : doctors) {
            if (doc.getAppointmentCount() < 10) {
                available.add(doc);
            }
        }
        return available;
    }

    private void reorganizeDoctors() {
        // Incomplete optimization logic
        System.out.println("Reorganizing department: " + name);
    }
    //DeadCodeEnd

    @Override
    public String toString() {
        return String.format("Department: %s [%s] - %d doctors, %d/%d patients",
                name, deptId, doctors.size(), currentPatients, capacity);
    }
}

// Doctor.java

class Doctor {
    private String name;
    private String doctorId;
    private String specialization;
    private int yearsOfExperience;
    private List<Appointment> appointments;
    private int appointmentCount;
    private Department assignedDepartment;

    public Doctor(String name, String doctorId, String specialization, int yearsOfExperience) {
        this.name = name;
        this.doctorId = doctorId;
        this.specialization = specialization;
        this.yearsOfExperience = yearsOfExperience;
        this.appointments = new ArrayList<>();
        this.appointmentCount = 0;
    }

    public String getName() {
        return name;
    }

    //DeadCodeStart
    public String getDoctorId() {
        return doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }
    //DeadCodeEnd

    public void setDepartment(Department dept) {
        this.assignedDepartment = dept;
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointmentCount++;
    }

    //DeadCodeStart
    public int getAppointmentCount() {
        return appointmentCount;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public Department getAssignedDepartment() {
        return assignedDepartment;
    }

    public void updateSpecialization(String newSpec) {
        // Never used
        this.specialization = newSpec;
        System.out.println("Specialization updated for " + name);
    }

    public boolean isSeniorDoctor() {
        // Unused classification method
        return yearsOfExperience >= 10;
    }

    public double calculateSalary() {
        // Dead code for salary calculation
        double baseSalary = 100000;
        double experienceBonus = yearsOfExperience * 5000;
        return baseSalary + experienceBonus;
    }

    public List<Patient> getPatientList() {
        // Dead code that extracts patients from appointments
        List<Patient> patients = new ArrayList<>();
        for (Appointment apt : appointments) {
            patients.add(apt.getPatient());
        }
        return patients;
    }

    private void sendReminders() {
        // Incomplete notification system
        System.out.println("Sending appointment reminders...");
    }
    //DeadCodeEnd

    @Override
    public String toString() {
        return String.format("%s (%s) - %s specialist, %d appointments",
                name, doctorId, specialization, appointmentCount);
    }
}

// Patient.java
class Patient {
    private String name;
    private String patientId;
    private int age;
    private String bloodType;
    private List<String> medicalHistory;
    private List<Appointment> appointments;
    private String currentStatus;

    public Patient(String name, String patientId, int age, String bloodType) {
        this.name = name;
        this.patientId = patientId;
        this.age = age;
        this.bloodType = bloodType;
        this.medicalHistory = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.currentStatus = "Registered";
    }

    public String getName() {
        return name;
    }

    //DeadCodeStart
    public String getPatientId() {
        return patientId;
    }

    public int getAge() {
        return age;
    }
    //DeadCodeEnd

    public void addMedicalRecord(String record) {
        medicalHistory.add(record);
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    //DeadCodeStart
    public String getStatus() {
        return currentStatus;
    }

    public void setStatus(String status) {
        this.currentStatus = status;
    }
    //DeadCodeEnd

    public void displayMedicalHistory() {
        System.out.println("Medical history for " + name + ":");
        if (medicalHistory.isEmpty()) {
            System.out.println("  No records yet");
        } else {
            for (String record : medicalHistory) {
                System.out.println("  - " + record);
            }
        }
    }

    //DeadCodeStart
    public String getBloodType() {
        return bloodType;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void updateAge(int newAge) {
        // Never called
        this.age = newAge;
    }

    public void updateBloodType(String newBloodType) {
        // Dead code for updating blood type
        this.bloodType = newBloodType;
        System.out.println("Blood type updated for " + name);
    }

    public boolean isHighRiskPatient() {
        // Unused risk assessment
        return age > 65 || medicalHistory.size() > 5;
    }

    public List<String> getAllergies() {
        // Returns empty list - never properly implemented
        return new ArrayList<>();
    }

    public String getInsuranceInfo() {
        // Dead code returning placeholder
        return "Insurance ID: INS-" + patientId;
    }

    private void notifyEmergencyContact() {
        // Incomplete notification system
        System.out.println("Notifying emergency contact for " + name);
    }
    //DeadCodeEnd

    @Override
    public String toString() {
        return String.format("Patient: %s [%s] - Age: %d, Blood Type: %s, Status: %s",
                name, patientId, age, bloodType, currentStatus);
    }
}

// Appointment.java

class Appointment {
    private static int nextId = 1;
    private String appointmentId;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime scheduledTime;
    private String reason;
    private String status;
    private String diagnosis;

    public Appointment(Patient patient, Doctor doctor, String reason) {
        this.appointmentId = "APT" + String.format("%04d", nextId++);
        this.patient = patient;
        this.doctor = doctor;
        this.scheduledTime = LocalDateTime.now();
        this.reason = reason;
        this.status = "Scheduled";
        this.diagnosis = "";
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    //DeadCodeStart
    public Doctor getDoctor() {
        return doctor;
    }
    //DeadCodeEnd

    public String getStatus() {
        return status;
    }

    //DeadCodeStart
    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiagnosis() {
        return diagnosis;
    }
    //DeadCodeEnd

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        this.status = "Completed";
    }

    //DeadCodeStart
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public String getReason() {
        return reason;
    }

    public void reschedule(LocalDateTime newTime) {
        // Never called rescheduling method
        this.scheduledTime = newTime;
        System.out.println("Appointment rescheduled to: " +
                newTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public void cancel() {
        // Dead code for cancellation
        this.status = "Cancelled";
        System.out.println("Appointment " + appointmentId + " cancelled");
    }

    public int getDurationMinutes() {
        // Returns fixed duration - never used
        return 30;
    }

    private boolean isUrgent() {
        // Unused priority check
        return reason.toLowerCase().contains("emergency");
    }
    //DeadCodeEnd

    @Override
    public String toString() {
        return String.format("[%s] %s with %s - %s (%s)",
                appointmentId, patient.getName(), doctor.getName(), reason, status);
    }
}

// Hospital.java

class Hospital {
    private String name;
    private String address;
    private List<Department> departments;
    private List<Doctor> doctors;
    private List<Patient> patients;
    private List<Appointment> appointments;

    public Hospital(String name, String address) {
        this.name = name;
        this.address = address;
        this.departments = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addDepartment(Department dept) {
        departments.add(dept);
        System.out.println("Added department: " + dept.getName());
    }

    public void registerDoctor(Doctor doctor, Department dept) {
        doctors.add(doctor);
        doctor.setDepartment(dept);
        dept.addDoctor(doctor);
        System.out.println("Registered doctor: " + doctor.getName() + " to " + dept.getName());
    }

    public void registerPatient(Patient patient) {
        patients.add(patient);
        System.out.println("Registered patient: " + patient.getName());
    }

    public Appointment scheduleAppointment(Patient patient, Doctor doctor, String reason) {
        Appointment apt = new Appointment(patient, doctor, reason);
        appointments.add(apt);
        patient.addAppointment(apt);
        doctor.addAppointment(apt);
        System.out.println("Scheduled: " + apt);
        return apt;
    }

    public void processAppointment(Appointment apt, String diagnosis) {
        apt.setDiagnosis(diagnosis);
        apt.getPatient().addMedicalRecord(diagnosis);
        System.out.println("Processed appointment " + apt.getAppointmentId() +
                " - Status: " + apt.getStatus());
    }

    public void displayAppointments() {
        for (Appointment apt : appointments) {
            System.out.println("  " + apt);
        }
    }

    public void displayDoctorWorkload() {
        for (Doctor doc : doctors) {
            System.out.println("  " + doc);
        }
    }

    //DeadCodeStart
    public List<Patient> getPatients() {
        return patients;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }
    
    public List<Department> getDepartments() {
        return departments;
    }

    public String getAddress() {
        return address;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public Department findDepartmentById(String deptId) {
        // Never used search method
        for (Department dept : departments) {
            if (dept.getDeptId().equals(deptId)) {
                return dept;
            }
        }
        return null;
    }

    public Doctor findDoctorById(String doctorId) {
        // Dead code for finding doctors
        for (Doctor doc : doctors) {
            if (doc.getDoctorId().equals(doctorId)) {
                return doc;
            }
        }
        return null;
    }

    public Patient findPatientById(String patientId) {
        // Dead code for finding patients
        for (Patient p : patients) {
            if (p.getPatientId().equals(patientId)) {
                return p;
            }
        }
        return null;
    }

    public List<Appointment> getAppointmentsByStatus(String status) {
        // Complex filtering that's never used
        List<Appointment> filtered = new ArrayList<>();
        for (Appointment apt : appointments) {
            if (apt.getStatus().equals(status)) {
                filtered.add(apt);
            }
        }
        return filtered;
    }

    private void generateDailyReport() {
        // Incomplete reporting logic
        System.out.println("Generating daily report for " + name);
        System.out.println("Total appointments: " + appointments.size());
        System.out.println("Total patients: " + patients.size());
    }

    public void transferPatient(Patient patient, Department from, Department to) {
        // Never called transfer logic
        if (to.hasCapacity()) {
            from.decrementPatientCount();
            to.incrementPatientCount();
            System.out.println("Transferred " + patient.getName() +
                    " from " + from.getName() + " to " + to.getName());
        }
    }
    //DeadCodeEnd
}

// BillingSystem.java
//DeadCodeStart
class BillingSystem {
    private static final double BASE_CONSULTATION_FEE = 150.0;
    private Hospital hospital;

    public BillingSystem(Hospital hospital) {
        this.hospital = hospital;
    }

    public double calculateAppointmentCost(Appointment apt) {
        // Entire class is dead code
        double cost = BASE_CONSULTATION_FEE;
        Doctor doc = apt.getDoctor();
        if (doc.getYearsOfExperience() > 10) {
            cost *= 1.5;
        }
        return cost;
    }

    public Invoice generateInvoice(Patient patient, Appointment apt) {
        double amount = calculateAppointmentCost(apt);
        return new Invoice(patient, apt, amount);
    }

    public void processPayment(Invoice invoice) {
        System.out.println("Processing payment for invoice: " + invoice.getInvoiceId());
    }
}

class Invoice {
    private static int nextId = 1;
    private String invoiceId;
    private Patient patient;
    private Appointment appointment;
    private double amount;
    private boolean paid;

    public Invoice(Patient patient, Appointment appointment, double amount) {
        this.invoiceId = "INV" + String.format("%05d", nextId++);
        this.patient = patient;
        this.appointment = appointment;
        this.amount = amount;
        this.paid = false;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void markAsPaid() {
        this.paid = true;
    }
}
//DeadCodeEnd

// HospitalAnalytics.java
//DeadCodeStart
class HospitalAnalytics {
    private Hospital hospital;

    public HospitalAnalytics(Hospital hospital) {
        this.hospital = hospital;
    }

    public void generateOccupancyReport() {
        // Never actually used - dead analytics code
        System.out.println("=== Occupancy Report ===");
        for (Department dept : hospital.getDepartments()) {
            System.out.println(dept.getName() + ": " +
                    dept.getCurrentPatients() + "/" + dept.getCapacity());
        }
    }

    public double calculateAveragePatientAge() {
        // Dead calculation method
        List<Patient> patients = hospital.getPatients();
        if (patients.isEmpty()) return 0.0;

        int sum = 0;
        for (Patient p : patients) {
            sum += p.getAge();
        }
        return (double) sum / patients.size();
    }

    public List<Doctor> getTopDoctors(int count) {
        // Complex sorting logic that's never used
        List<Doctor> sorted = new ArrayList<>(hospital.getDoctors());
        // Incomplete sorting implementation
        return sorted.subList(0, Math.min(count, sorted.size()));
    }

    private void analyzeTrends() {
        // Incomplete trend analysis
        System.out.println("Analyzing hospital trends...");
    }
}
//DeadCodeEnd
