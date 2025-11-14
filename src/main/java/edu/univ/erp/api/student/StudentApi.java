package edu.univ.erp.api.student;

import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.data.RegistrationStore;
import edu.univ.erp.domain.RegistrationItem;
import java.util.List;

import edu.univ.erp.api.student.RegistrationStatus;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.api.student.DropStatus;

public class StudentApi {

    private final RegistrationStore registrationStore = new RegistrationStore();

    /**
     * Gets the list of registered courses for the *currently logged-in* student.
     *
     * @return A list of RegistrationItem objects.
     */
    public List<RegistrationItem> getMyRegistrations() {
        // Get the logged-in user's ID from the session
        int currentStudentId = UserSession.getInstance().getUserId();

        // Use the ID to fetch their registrations
        return registrationStore.findRegistrationsByStudentId(currentStudentId);
    }

    private final StudentService studentService = new StudentService();

    /**
     * Attempts to register the current student for a section.
     * @param sectionId The ID of the section to register for.
     * @return A RegistrationStatus enum indicating the outcome.
     */
    public RegistrationStatus registerForSection(int sectionId) {
        // Get the logged-in user's ID
        int currentStudentId = UserSession.getInstance().getUserId();

        // Call the "brain" to perform the logic
        return studentService.registerStudent(currentStudentId, sectionId);
    }

    /**
     * Attempts to drop the current student from a section.
     * @param sectionId The ID of the section to drop.
     * @return A DropStatus enum indicating the outcome.
     */
    public DropStatus dropSection(int sectionId) {
        int currentStudentId = UserSession.getInstance().getUserId();

        // Call the "brain" to perform the logic
        return studentService.dropStudent(currentStudentId, sectionId);
    }
}