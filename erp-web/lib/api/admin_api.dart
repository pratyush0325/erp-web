import 'client.dart';

class AdminApi {
  static Future<List<dynamic>> getUsers() async {
    final res = await apiClient.get('/admin/users');
    return res.data as List<dynamic>;
  }

  static Future<void> addUser(Map<String, String> data) async {
    await apiClient.post('/admin/users', data: data);
  }

  static Future<void> deleteUser(int userId) async {
    await apiClient.delete('/admin/users/$userId');
  }

  static Future<void> toggleUserStatus(int userId, String currentStatus) async {
    await apiClient.patch('/admin/users/$userId/status',
        data: {'currentStatus': currentStatus});
  }

  static Future<List<dynamic>> getCourses() async {
    final res = await apiClient.get('/admin/courses');
    return res.data as List<dynamic>;
  }

  static Future<void> addCourse(Map<String, dynamic> data) async {
    await apiClient.post('/admin/courses', data: data);
  }

  static Future<void> deleteCourse(String code) async {
    await apiClient.delete('/admin/courses/$code');
  }

  static Future<List<dynamic>> getSections() async {
    final res = await apiClient.get('/admin/sections');
    return res.data as List<dynamic>;
  }

  static Future<void> addSection(Map<String, dynamic> data) async {
    await apiClient.post('/admin/sections', data: data);
  }

  static Future<void> updateSection(
      int sectionId, Map<String, dynamic> data) async {
    await apiClient.patch('/admin/sections/$sectionId', data: data);
  }

  static Future<void> deleteSection(int sectionId) async {
    await apiClient.delete('/admin/sections/$sectionId');
  }

  static Future<List<dynamic>> getInstructors() async {
    final res = await apiClient.get('/admin/instructors');
    return res.data as List<dynamic>;
  }

  static Future<Map<String, dynamic>> getStats() async {
    final res = await apiClient.get('/admin/stats');
    return res.data as Map<String, dynamic>;
  }

  static Future<bool> getMaintenance() async {
    final res = await apiClient.get('/admin/maintenance');
    return res.data['maintenanceOn'] as bool;
  }

  static Future<void> setMaintenance(bool enabled) async {
    await apiClient.post('/admin/maintenance', data: {'enabled': enabled});
  }

  static Future<String> getDeadline() async {
    final res = await apiClient.get('/admin/deadline');
    return (res.data['deadline'] ?? '') as String;
  }

  static Future<void> setDeadline(String deadline) async {
    await apiClient.post('/admin/deadline', data: {'deadline': deadline});
  }

  static Future<void> backup(String path) async {
    await apiClient.post('/admin/backup', data: {'path': path});
  }

  static Future<void> restore(String path) async {
    await apiClient.post('/admin/restore', data: {'path': path});
  }
}
