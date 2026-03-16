// Main.java

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        University university = new University("Tech State University", "456 University Ave");

        // PLAGIARIZED: Same structure as Hospital departments
        Department computerScience = new Department("Computer Science", "CS001", 50);
        Department mathematics = new Department("Mathematics", "MATH001", 40);
        Department physics = new Department("Physics", "PHYS001", 35);

        university.addDepartment(computerScience);
        university.addDepartment(mathematics);
        university.addDepartment(physics);

        // PLAGIARIZED: Similar to Hospital doctors
        Professor prof1 = new Professor("Dr. Robert Anderson", "PROF001", "Computer Science", 12);
        Professor prof2 = new Professor("Dr. Lisa Martinez", "PROF002", "Mathematics", 8);
        Professor prof3 = new Professor("Dr. David Wilson", "PROF003", "Physics", 15);

        university.registerProfessor(prof1, computerScience);
        university.registerProfessor(prof2, mathematics);
        university.registerProfessor(prof3, physics);

        // PLAGIARIZED: Similar to Hospital patients
        Student student1 = new Student("Alex Thompson", "S001", 20, "Computer Science");
        Student student2 = new Student("Emma Davis", "S002", 21, "Mathematics");
        Student student3 = new Student("Ryan Miller", "S003", 19, "Physics");

        university.registerStudent(student1);
        university.registerStudent(student2);
        university.registerStudent(student3);

        // PLAGIARIZED: Similar appointment scheduling pattern
        System.out.println("=== University Management System ===");
        System.out.println("University: " + university.getName());
        System.out.println();

        Enrollment enr1 = university.scheduleEnrollment(student1, prof1, "Data Structures");
        Enrollment enr2 = university.scheduleEnrollment(student2, prof2, "Linear Algebra");
        Enrollment enr3 = university.scheduleEnrollment(student3, prof3, "Quantum Mechanics");

        System.out.println("\n--- Scheduled Enrollments ---");
        university.displayEnrollments();

        System.out.println("\n--- Processing Enrollments ---");
        university.processEnrollment(enr1, "Student performing well. Midterm grade: A-");
        university.processEnrollment(enr2, "Needs additional tutoring. Current grade: B+");

        System.out.println("\n--- Professor Workload ---");
        university.displayProfessorWorkload();

        System.out.println("\n--- Student Academic History ---");
        student1.displayAcademicHistory();

        //DeadCodeStart
        // PLAGIARIZED: Same pattern as Hospital billing
        TuitionSystem tuition = new TuitionSystem(university);
        double cost = tuition.calculateCourseCost(enr1);
        Receipt receipt = tuition.generateReceipt(student1, enr1);
        //DeadCodeEnd

        //DeadCodeStart
        // PLAGIARIZED: Same pattern as Hospital analytics
        UniversityAnalytics analytics = new UniversityAnalytics(university);
        analytics.generateEnrollmentReport();
        double avgStudentAge = analytics.calculateAverageStudentAge();
        //DeadCodeEnd
    }
}

// Department.java - HEAVILY PLAGIARIZED from Hospital's Department

class Department {
    private String name;
    private String deptId;
    private int capacity;
    private List<Professor> professors;  // Changed from doctors to professors
    private int currentStudents;  // Changed from currentPatients

    // PLAGIARIZED: Exact same constructor pattern
    public Department(String name, String deptId, int capacity) {
        this.name = name;
        this.deptId = deptId;
        this.capacity = capacity;
        this.professors = new ArrayList<>();
        this.currentStudents = 0;
    }

    public String getName() {
        return name;
    }

    //DeadCodeStart
    public String getDeptId() {
        return deptId;
    }
    //DeadCodeEnd

    // PLAGIARIZED: Renamed from addDoctor
    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    //DeadCodeStart
    public List<Professor> getProfessors() {
        return professors;
    }

    // PLAGIARIZED: Exact same logic as Hospital
    public boolean hasCapacity() {
        return currentStudents < capacity;
    }

    // PLAGIARIZED: Same increment pattern
    public void incrementStudentCount() {
        currentStudents++;
    }

    // PLAGIARIZED: Same decrement pattern
    public void decrementStudentCount() {
        if (currentStudents > 0) currentStudents--;
    }

    public int getCapacity() {
        return capacity;
    }

    // PLAGIARIZED from Hospital.Department.setCapacity() - in dead code
    public void setCapacity(int newCapacity) {
        // Never called
        this.capacity = newCapacity;
        System.out.println("Capacity updated to: " + newCapacity);
    }

    public int getCurrentStudents() {
        return currentStudents;
    }

    // PLAGIARIZED: Exact same calculation as getUtilizationRate()
    public double getEnrollmentRate() {
        // Unused calculation method
        if (capacity == 0) return 0.0;
        return (double) currentStudents / capacity * 100;
    }

    // PLAGIARIZED from getAvailableDoctors()
    public List<Professor> getAvailableProfessors() {
        // Complex dead code for finding available professors
        List<Professor> available = new ArrayList<>();
        for (Professor prof : professors) {
            if (prof.getCourseCount() < 10) {
                available.add(prof);
            }
        }
        return available;
    }

    // PLAGIARIZED from reorganizeDoctors()
    private void reorganizeProfessors() {
        // Incomplete optimization logic
        System.out.println("Reorganizing department: " + name);
    }
    //DeadCodeEnd

    // PLAGIARIZED: Same toString format as Hospital
    @Override
    public String toString() {
        return String.format("Department: %s [%s] - %d professors, %d/%d students",
                name, deptId, professors.size(), currentStudents, capacity);
    }
}

// Professor.java - PLAGIARIZED from Doctor.java

class Professor {
    private String name;
    private String professorId;
    private String specialization;
    private int yearsOfExperience;
    private List<Enrollment> enrollments;  // Changed from appointments
    private int courseCount;  // Changed from appointmentCount
    private Department assignedDepartment;

    // PLAGIARIZED: Exact same constructor as Doctor
    public Professor(String name, String professorId, String specialization, int yearsOfExperience) {
        this.name = name;
        this.professorId = professorId;
        this.specialization = specialization;
        this.yearsOfExperience = yearsOfExperience;
        this.enrollments = new ArrayList<>();
        this.courseCount = 0;
    }

    public String getName() {
        return name;
    }

    //DeadCodeStart
    public String getProfessorId() {
        return professorId;
    }

    public String getSpecialization() {
        return specialization;
    }
    //DeadCodeEnd

    // PLAGIARIZED: Same as Doctor.setDepartment()
    public void setDepartment(Department dept) {
        this.assignedDepartment = dept;
    }

    // PLAGIARIZED from Doctor.addAppointment()
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        courseCount++;
    }

    //DeadCodeStart
    public int getCourseCount() {
        return courseCount;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public Department getAssignedDepartment() {
        return assignedDepartment;
    }

    // PLAGIARIZED from Doctor.updateSpecialization() - hidden in dead code
    public void updateSpecialization(String newSpec) {
        // Never used
        this.specialization = newSpec;
        System.out.println("Specialization updated for " + name);
    }

    // PLAGIARIZED from Doctor.isSeniorDoctor()
    public boolean isTenuredProfessor() {
        // Unused classification method - same logic
        return yearsOfExperience >= 10;
    }

    // PLAGIARIZED from Doctor.calculateSalary()
    public double calculateSalary() {
        // Dead code for salary calculation - exact same formula
        double baseSalary = 100000;
        double experienceBonus = yearsOfExperience * 5000;
        return baseSalary + experienceBonus;
    }

    // PLAGIARIZED from Doctor.getPatientList()
    public List<Student> getStudentList() {
        // Dead code that extracts students from enrollments
        List<Student> students = new ArrayList<>();
        for (Enrollment enr : enrollments) {
            students.add(enr.getStudent());
        }
        return students;
    }

    // PLAGIARIZED from Doctor.sendReminders()
    private void sendNotifications() {
        // Incomplete notification system
        System.out.println("Sending course notifications...");
    }

    public void assignGrade(Enrollment enrollment, String grade) {
        // Additional dead code
        System.out.println("Assigning grade " + grade + " to " + enrollment.getStudent().getName());
    }
    //DeadCodeEnd

    // PLAGIARIZED: Same toString format as Doctor
    @Override
    public String toString() {
        return String.format("%s (%s) - %s specialist, %d courses",
                name, professorId, specialization, courseCount);
    }
}

// Student.java - PLAGIARIZED from Patient.java

class Student {
    private String name;
    private String studentId;
    private int age;
    private String major;  // Changed from bloodType
    private List<String> academicHistory;  // Changed from medicalHistory
    private List<Enrollment> enrollments;  // Changed from appointments
    private String currentStatus;

    // PLAGIARIZED: Exact same constructor pattern as Patient
    public Student(String name, String studentId, int age, String major) {
        this.name = name;
        this.studentId = studentId;
        this.age = age;
        this.major = major;
        this.academicHistory = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.currentStatus = "Enrolled";
    }

    public String getName() {
        return name;
    }

    //DeadCodeStart
    public String getStudentId() {
        return studentId;
    }

    public int getAge() {
        return age;
    }
    //DeadCodeEnd

    // PLAGIARIZED from Patient.addMedicalRecord()
    public void addAcademicRecord(String record) {
        academicHistory.add(record);
    }

    // PLAGIARIZED from Patient.addAppointment()
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    //DeadCodeStart
    public String getStatus() {
        return currentStatus;
    }

    public void setStatus(String status) {
        this.currentStatus = status;
    }
    //DeadCodeEnd

    // PLAGIARIZED: Exact same display pattern as Patient.displayMedicalHistory()
    public void displayAcademicHistory() {
        System.out.println("Academic history for " + name + ":");
        if (academicHistory.isEmpty()) {
            System.out.println("  No records yet");
        } else {
            for (String record : academicHistory) {
                System.out.println("  - " + record);
            }
        }
    }

    //DeadCodeStart
    public String getMajor() {
        return major;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    // PLAGIARIZED from Patient.updateAge()
    public void updateAge(int newAge) {
        // Never called
        this.age = newAge;
    }

    // PLAGIARIZED from Patient.updateBloodType()
    public void updateMajor(String newMajor) {
        // Dead code for updating major
        this.major = newMajor;
        System.out.println("Major updated for " + name);
    }

    // PLAGIARIZED from Patient.isHighRiskPatient()
    public boolean isAtRiskStudent() {
        // Unused risk assessment - same logic pattern
        return age > 65 || academicHistory.size() > 5;
    }

    // PLAGIARIZED from Patient.getAllergies()
    public List<String> getExtracurriculars() {
        // Returns empty list - never properly implemented
        return new ArrayList<>();
    }

    // PLAGIARIZED from Patient.getInsuranceInfo()
    public String getScholarshipInfo() {
        // Dead code returning placeholder
        return "Scholarship ID: SCH-" + studentId;
    }

    // PLAGIARIZED from Patient.notifyEmergencyContact()
    private void notifyAdvisor() {
        // Incomplete notification system
        System.out.println("Notifying academic advisor for " + name);
    }

    public double calculateGPA() {
        // Additional dead code
        return 3.5;  // Hardcoded placeholder
    }
    //DeadCodeEnd

    // PLAGIARIZED: Same toString format as Patient
    @Override
    public String toString() {
        return String.format("Student: %s [%s] - Age: %d, Major: %s, Status: %s",
                name, studentId, age, major, currentStatus);
    }
}

// Enrollment.java - PLAGIARIZED from Appointment.java

class Enrollment {
    private static int nextId = 1;
    private String enrollmentId;
    private Student student;  // Changed from patient
    private Professor professor;  // Changed from doctor
    private LocalDateTime enrollmentTime;  // Changed from scheduledTime
    private String courseName;  // Changed from reason
    private String status;
    private String feedback;  // Changed from diagnosis

    // PLAGIARIZED: Exact same constructor pattern as Appointment
    public Enrollment(Student student, Professor professor, String courseName) {
        this.enrollmentId = "ENR" + String.format("%04d", nextId++);
        this.student = student;
        this.professor = professor;
        this.enrollmentTime = LocalDateTime.now();
        this.courseName = courseName;
        this.status = "Active";
        this.feedback = "";
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public Student getStudent() {
        return student;
    }

    //DeadCodeStart
    public Professor getProfessor() {
        return professor;
    }
    //DeadCodeEnd

    public String getStatus() {
        return status;
    }

    //DeadCodeStart
    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }
    //DeadCodeEnd

    // PLAGIARIZED from Appointment.setDiagnosis()
    public void setFeedback(String feedback) {
        this.feedback = feedback;
        this.status = "Graded";
    }

    //DeadCodeStart
    public LocalDateTime getEnrollmentTime() {
        return enrollmentTime;
    }

    public String getCourseName() {
        return courseName;
    }

    // PLAGIARIZED from Appointment.reschedule()
    public void changeSection(LocalDateTime newTime) {
        // Never called rescheduling method
        this.enrollmentTime = newTime;
        System.out.println("Section changed to: " +
                newTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    // PLAGIARIZED from Appointment.cancel()
    public void drop() {
        // Dead code for dropping course
        this.status = "Dropped";
        System.out.println("Enrollment " + enrollmentId + " dropped");
    }

    // PLAGIARIZED from Appointment.getDurationMinutes()
    public int getCreditHours() {
        // Returns fixed value - never used
        return 3;
    }

    // PLAGIARIZED from Appointment.isUrgent()
    private boolean isHonorsCourse() {
        // Unused priority check
        return courseName.toLowerCase().contains("honors");
    }

    public String calculateLetterGrade() {
        // Additional dead code
        return "A";  // Placeholder
    }
    //DeadCodeEnd

    // PLAGIARIZED: Same toString format as Appointment
    @Override
    public String toString() {
        return String.format("[%s] %s with %s - %s (%s)",
                enrollmentId, student.getName(), professor.getName(), courseName, status);
    }
}

// University.java - HEAVILY PLAGIARIZED from Hospital.java

class University {
    private String name;
    private String address;
    private List<Department> departments;
    private List<Professor> professors;  // Changed from doctors
    private List<Student> students;  // Changed from patients
    private List<Enrollment> enrollments;  // Changed from appointments

    // PLAGIARIZED: Exact same constructor as Hospital
    public University(String name, String address) {
        this.name = name;
        this.address = address;
        this.departments = new ArrayList<>();
        this.professors = new ArrayList<>();
        this.students = new ArrayList<>();
        this.enrollments = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    // PLAGIARIZED: Exact copy of Hospital.addDepartment()
    public void addDepartment(Department dept) {
        departments.add(dept);
        System.out.println("Added department: " + dept.getName());
    }

    // PLAGIARIZED from Hospital.registerDoctor()
    public void registerProfessor(Professor professor, Department dept) {
        professors.add(professor);
        professor.setDepartment(dept);
        dept.addProfessor(professor);
        System.out.println("Registered professor: " + professor.getName() + " to " + dept.getName());
    }

    // PLAGIARIZED from Hospital.registerPatient()
    public void registerStudent(Student student) {
        students.add(student);
        System.out.println("Registered student: " + student.getName());
    }

    // PLAGIARIZED: Same logic as Hospital.scheduleAppointment()
    public Enrollment scheduleEnrollment(Student student, Professor professor, String courseName) {
        Enrollment enr = new Enrollment(student, professor, courseName);
        enrollments.add(enr);
        student.addEnrollment(enr);
        professor.addEnrollment(enr);
        System.out.println("Scheduled: " + enr);
        return enr;
    }

    // PLAGIARIZED from Hospital.processAppointment()
    public void processEnrollment(Enrollment enr, String feedback) {
        enr.setFeedback(feedback);
        enr.getStudent().addAcademicRecord(feedback);
        System.out.println("Processed enrollment " + enr.getEnrollmentId() +
                " - Status: " + enr.getStatus());
    }

    // PLAGIARIZED from Hospital.displayAppointments()
    public void displayEnrollments() {
        for (Enrollment enr : enrollments) {
            System.out.println("  " + enr);
        }
    }

    // PLAGIARIZED from Hospital.displayDoctorWorkload()
    public void displayProfessorWorkload() {
        for (Professor prof : professors) {
            System.out.println("  " + prof);
        }
    }

    //DeadCodeStart
    public List<Student> getStudents() {
        return students;
    }

    public List<Professor> getProfessors() {
        return professors;
    }
    
    public List<Department> getDepartments() {
        return departments;
    }

    public String getAddress() {
        return address;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    // PLAGIARIZED from Hospital.findDepartmentById() - in dead code
    public Department findDepartmentById(String deptId) {
        // Never used search method
        for (Department dept : departments) {
            if (dept.getDeptId().equals(deptId)) {
                return dept;
            }
        }
        return null;
    }

    // PLAGIARIZED from Hospital.findDoctorById()
    public Professor findProfessorById(String professorId) {
        // Dead code for finding professors
        for (Professor prof : professors) {
            if (prof.getProfessorId().equals(professorId)) {
                return prof;
            }
        }
        return null;
    }

    // PLAGIARIZED from Hospital.findPatientById()
    public Student findStudentById(String studentId) {
        // Dead code for finding students
        for (Student s : students) {
            if (s.getStudentId().equals(studentId)) {
                return s;
            }
        }
        return null;
    }

    // PLAGIARIZED from Hospital.getAppointmentsByStatus()
    public List<Enrollment> getEnrollmentsByStatus(String status) {
        // Complex filtering that's never used
        List<Enrollment> filtered = new ArrayList<>();
        for (Enrollment enr : enrollments) {
            if (enr.getStatus().equals(status)) {
                filtered.add(enr);
            }
        }
        return filtered;
    }

    // PLAGIARIZED from Hospital.generateDailyReport()
    private void generateSemesterReport() {
        // Incomplete reporting logic
        System.out.println("Generating semester report for " + name);
        System.out.println("Total enrollments: " + enrollments.size());
        System.out.println("Total students: " + students.size());
    }

    // PLAGIARIZED from Hospital.transferPatient()
    public void transferStudent(Student student, Department from, Department to) {
        // Never called transfer logic
        if (to.hasCapacity()) {
            from.decrementStudentCount();
            to.incrementStudentCount();
            System.out.println("Transferred " + student.getName() +
                    " from " + from.getName() + " to " + to.getName());
        }
    }

    public void accreditDepartment(Department dept) {
        // Additional dead code
        System.out.println("Department " + dept.getName() + " has been accredited");
    }
    //DeadCodeEnd
}

// TuitionSystem.java - PLAGIARIZED from BillingSystem.java
//DeadCodeStart
class TuitionSystem {
    private static final double BASE_COURSE_FEE = 150.0;  // Same value as BASE_CONSULTATION_FEE
    private University university;

    // PLAGIARIZED: Exact same constructor pattern as BillingSystem
    public TuitionSystem(University university) {
        this.university = university;
    }

    // PLAGIARIZED from BillingSystem.calculateAppointmentCost()
    public double calculateCourseCost(Enrollment enr) {
        // Entire class is dead code - exact same logic
        double cost = BASE_COURSE_FEE;
        Professor prof = enr.getProfessor();
        if (prof.getYearsOfExperience() > 10) {
            cost *= 1.5;
        }
        return cost;
    }

    // PLAGIARIZED from BillingSystem.generateInvoice()
    public Receipt generateReceipt(Student student, Enrollment enr) {
        double amount = calculateCourseCost(enr);
        return new Receipt(student, enr, amount);
    }

    // PLAGIARIZED from BillingSystem.processPayment()
    public void processPayment(Receipt receipt) {
        System.out.println("Processing payment for receipt: " + receipt.getReceiptId());
    }
}

// PLAGIARIZED: Entire class copied from Invoice
class Receipt {
    private static int nextId = 1;
    private String receiptId;
    private Student student;  // Changed from patient
    private Enrollment enrollment;  // Changed from appointment
    private double amount;
    private boolean paid;

    public Receipt(Student student, Enrollment enrollment, double amount) {
        this.receiptId = "REC" + String.format("%05d", nextId++);  // Changed prefix
        this.student = student;
        this.enrollment = enrollment;
        this.amount = amount;
        this.paid = false;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void markAsPaid() {
        this.paid = true;
    }
}
//DeadCodeEnd

// UniversityAnalytics.java - PLAGIARIZED from HospitalAnalytics.java
//DeadCodeStart
class UniversityAnalytics {
    private University university;

    // PLAGIARIZED: Exact same constructor
    public UniversityAnalytics(University university) {
        this.university = university;
    }

    // PLAGIARIZED from HospitalAnalytics.generateOccupancyReport()
    public void generateEnrollmentReport() {
        // Never actually used - dead analytics code
        System.out.println("=== Enrollment Report ===");
        for (Department dept : university.getDepartments()) {
            System.out.println(dept.getName() + ": " +
                    dept.getCurrentStudents() + "/" + dept.getCapacity());
        }
    }

    // PLAGIARIZED: Exact same calculation logic as calculateAveragePatientAge()
    public double calculateAverageStudentAge() {
        // Dead calculation method
        List<Student> students = university.getStudents();
        if (students.isEmpty()) return 0.0;

        int sum = 0;
        for (Student s : students) {
            sum += s.getAge();
        }
        return (double) sum / students.size();
    }

    // PLAGIARIZED from HospitalAnalytics.getTopDoctors()
    public List<Professor> getTopProfessors(int count) {
        // Complex sorting logic that's never used
        List<Professor> sorted = new ArrayList<>(university.getProfessors());
        // Incomplete sorting implementation
        return sorted.subList(0, Math.min(count, sorted.size()));
    }

    // PLAGIARIZED from HospitalAnalytics.analyzeTrends()
    private void analyzeAcademicTrends() {
        // Incomplete trend analysis
        System.out.println("Analyzing academic trends...");
    }

    public void calculateRetentionRate() {
        // Additional dead code
        System.out.println("Calculating student retention rate...");
    }
}
//DeadCodeEnd
