import 'client.dart';

class AuthApi {
  static Future<Map<String, dynamic>> login(
      String username, String password) async {
    final res = await apiClient
        .post('/auth/login', data: {'username': username, 'password': password});
    return res.data as Map<String, dynamic>;
  }

  static Future<void> changePassword(
      String currentPassword, String newPassword) async {
    await apiClient.post('/auth/change-password',
        data: {'currentPassword': currentPassword, 'newPassword': newPassword});
  }
}
