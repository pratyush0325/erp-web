import 'package:dio/dio.dart';
import '../services/auth_service.dart';

final apiClient = Dio(BaseOptions(baseUrl: '/api'))
  ..interceptors.add(InterceptorsWrapper(
    onRequest: (options, handler) async {
      final token = await AuthService.getToken();
      if (token != null) {
        options.headers['Authorization'] = 'Bearer $token';
      }
      handler.next(options);
    },
    onError: (error, handler) async {
      if (error.response?.statusCode == 401) {
        await AuthService.clearAuth();
      }
      handler.next(error);
    },
  ));
