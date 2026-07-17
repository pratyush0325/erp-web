import 'package:shared_preferences/shared_preferences.dart';

class AuthService {
  static const _tokenKey = 'token';
  static const _roleKey = 'role';

  static SharedPreferencesAsync? _prefs;

  static SharedPreferencesAsync get _instance =>
      _prefs ??= SharedPreferencesAsync();

  static Future<String?> getToken() => _instance.getString(_tokenKey);

  static Future<String?> getRole() => _instance.getString(_roleKey);

  static Future<bool> isLoggedIn() async =>
      (await _instance.getString(_tokenKey)) != null;

  static Future<void> saveAuth(String token, String role) async {
    await _instance.setString(_tokenKey, token);
    await _instance.setString(_roleKey, role);
  }

  static Future<void> clearAuth() async {
    await _instance.remove(_tokenKey);
    await _instance.remove(_roleKey);
  }
}
