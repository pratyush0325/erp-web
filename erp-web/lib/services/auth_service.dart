import 'package:shared_preferences/shared_preferences.dart';

class AuthService {
  static const _tokenKey = 'token';
  static const _roleKey = 'role';

  static String? _token;
  static String? _role;

  static final _prefs = SharedPreferencesAsync();

  static Future<void> init() async {
    _token = await _prefs.getString(_tokenKey);
    _role = await _prefs.getString(_roleKey);
  }

  static String? get token => _token;
  static String? get role => _role;
  static bool get isLoggedIn => _token != null;

  static Future<void> saveAuth(String token, String role) async {
    _token = token;
    _role = role;
    await _prefs.setString(_tokenKey, token);
    await _prefs.setString(_roleKey, role);
  }

  static Future<void> clearAuth() async {
    _token = null;
    _role = null;
    await _prefs.remove(_tokenKey);
    await _prefs.remove(_roleKey);
  }
}
