import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'services/auth_service.dart';
import 'pages/login_page.dart';
import 'pages/student/dashboard_page.dart';
import 'pages/student/catalog_page.dart';
import 'pages/student/grades_page.dart';
import 'pages/student/profile_page.dart';
import 'pages/instructor/dashboard_page.dart';
import 'pages/instructor/grading_page.dart';
import 'pages/admin/dashboard_page.dart';
import 'pages/admin/users_page.dart';
import 'pages/admin/settings_page.dart';

final router = GoRouter(
  initialLocation: '/login',
  redirect: (context, state) {
    final loggedIn = AuthService.isLoggedIn;
    final loggingIn = state.matchedLocation == '/login';

    if (!loggedIn && !loggingIn) return '/login';
    if (loggedIn && loggingIn) {
      final role = AuthService.role?.toLowerCase();
      if (role == 'admin') return '/admin';
      if (role == 'instructor') return '/instructor';
      return '/student';
    }
    return null;
  },
  routes: [
    GoRoute(path: '/', redirect: (_, __) => '/login'),
    GoRoute(path: '/login', builder: (_, __) => const LoginPage()),
    _guarded('/student', const StudentDashboardPage(), 'student'),
    _guarded('/student/courses', const StudentDashboardPage(), 'student'),
    _guarded('/student/catalog', const CatalogPage(), 'student'),
    _guarded('/student/grades', const GradesPage(), 'student'),
    _guarded('/student/profile', const ProfilePage(), 'student'),
    _guarded('/instructor', const InstructorDashboardPage(), 'instructor'),
    _guarded('/instructor/classes', const InstructorDashboardPage(), 'instructor'),
    _guarded('/instructor/grading', const GradingPage(), 'instructor'),
    _guarded('/instructor/stats', const InstructorDashboardPage(), 'instructor'),
    _guarded('/admin', const AdminDashboardPage(), 'admin'),
    _guarded('/admin/users', const UsersPage(), 'admin'),
    _guarded('/admin/settings', const SettingsPage(), 'admin'),
    _guarded('/admin/courses', const AdminDashboardPage(), 'admin'),
    _guarded('/admin/sections', const AdminDashboardPage(), 'admin'),
  ],
);

GoRoute _guarded(String path, Widget page, String role) {
  return GoRoute(
    path: path,
    redirect: (context, state) {
      if (AuthService.token == null) return '/login';
      if (AuthService.role?.toLowerCase() != role) return '/login';
      return null;
    },
    builder: (_, __) => page,
  );
}
