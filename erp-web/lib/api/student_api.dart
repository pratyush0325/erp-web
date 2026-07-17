import 'client.dart';

class StudentApi {
  static Future<List<dynamic>> getRegistrations() async {
    final res = await apiClient.get('/student/registrations');
    return res.data as List<dynamic>;
  }

  static Future<String> register(int sectionId) async {
    final res = await apiClient.post('/student/register/$sectionId');
    return res.data.toString();
  }

  static Future<void> drop(int sectionId) async {
    await apiClient.delete('/student/drop/$sectionId');
  }

  static Future<List<dynamic>> getGrades() async {
    final res = await apiClient.get('/student/grades');
    return res.data as List<dynamic>;
  }

  static Future<Map<String, dynamic>> getProfile() async {
    final res = await apiClient.get('/student/profile');
    return res.data as Map<String, dynamic>;
  }

  static Future<String> getDeadline() async {
    final res = await apiClient.get('/student/deadline');
    return res.data.toString();
  }

  static Future<List<dynamic>> getCatalog() async {
    final res = await apiClient.get('/catalog');
    return res.data as List<dynamic>;
  }
}
