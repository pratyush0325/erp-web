import 'client.dart';

class InstructorApi {
  static Future<List<dynamic>> getMyCourses() async {
    final res = await apiClient.get('/instructor/courses');
    return res.data as List<dynamic>;
  }

  static Future<List<dynamic>> getStudents(int sectionId) async {
    final res =
        await apiClient.get('/instructor/sections/$sectionId/students');
    return res.data as List<dynamic>;
  }

  static Future<void> assignGrade(
      int studentId, int sectionId, String grade) async {
    await apiClient.put('/instructor/grades',
        data: {'studentId': studentId, 'sectionId': sectionId, 'grade': grade});
  }

  static Future<List<dynamic>> getAssignments(int sectionId) async {
    final res =
        await apiClient.get('/instructor/sections/$sectionId/assignments');
    return res.data as List<dynamic>;
  }

  static Future<void> updateScore(
      int assignmentId, int studentId, double score) async {
    await apiClient.put('/instructor/scores', data: {
      'assignmentId': assignmentId,
      'studentId': studentId,
      'score': score
    });
  }

  static Future<int> getPendingGrades() async {
    final res = await apiClient.get('/instructor/pending-grades');
    return res.data as int;
  }
}
