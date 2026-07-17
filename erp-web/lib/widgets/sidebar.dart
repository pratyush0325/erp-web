import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../services/auth_service.dart';

class NavItem {
  final String label;
  final String path;
  final IconData icon;
  const NavItem(this.label, this.path, this.icon);
}

const _navMap = <String, List<NavItem>>{
  'student': [
    NavItem('Dashboard', '/student', Icons.dashboard_outlined),
    NavItem('My Courses', '/student/courses', Icons.book_outlined),
    NavItem('Grades', '/student/grades', Icons.grade_outlined),
    NavItem('Catalog', '/student/catalog', Icons.search),
    NavItem('Profile', '/student/profile', Icons.person_outline),
  ],
  'instructor': [
    NavItem('Dashboard', '/instructor', Icons.dashboard_outlined),
    NavItem('Class List', '/instructor/classes', Icons.people_outline),
    NavItem('Grading', '/instructor/grading', Icons.edit_note),
    NavItem('Statistics', '/instructor/stats', Icons.bar_chart),
  ],
  'admin': [
    NavItem('Dashboard', '/admin', Icons.dashboard_outlined),
    NavItem('Users', '/admin/users', Icons.group_outlined),
    NavItem('Courses', '/admin/courses', Icons.menu_book_outlined),
    NavItem('Sections', '/admin/sections', Icons.class_outlined),
    NavItem('Settings', '/admin/settings', Icons.settings_outlined),
  ],
};

class Sidebar extends StatelessWidget {
  const Sidebar({super.key});

  @override
  Widget build(BuildContext context) {
    final role = (AuthService.role ?? '').toLowerCase();
    final items = _navMap[role] ?? [];
    final currentPath = GoRouterState.of(context).matchedLocation;

    return Container(
      width: 224,
      color: const Color(0xFF111827),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.fromLTRB(20, 24, 20, 32),
            child: Text(
              'ERP',
              style: TextStyle(
                color: Colors.white,
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          ...items.map((item) {
            final selected = currentPath == item.path;
            return Padding(
              padding:
                  const EdgeInsets.symmetric(horizontal: 8, vertical: 1),
              child: Material(
                color: selected
                    ? Colors.white.withValues(alpha: 0.1)
                    : Colors.transparent,
                borderRadius: BorderRadius.circular(8),
                child: InkWell(
                  borderRadius: BorderRadius.circular(8),
                  hoverColor: Colors.white.withValues(alpha: 0.07),
                  onTap: () => context.go(item.path),
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 12, vertical: 10),
                    child: Row(
                      children: [
                        Icon(item.icon, color: Colors.white70, size: 18),
                        const SizedBox(width: 12),
                        Text(
                          item.label,
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 13,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            );
          }),
          const Spacer(),
          Padding(
            padding: const EdgeInsets.all(8),
            child: Material(
              color: Colors.transparent,
              borderRadius: BorderRadius.circular(8),
              child: InkWell(
                borderRadius: BorderRadius.circular(8),
                hoverColor: Colors.white.withValues(alpha: 0.07),
                onTap: () async {
                  await AuthService.clearAuth();
                  if (context.mounted) context.go('/login');
                },
                child: const Padding(
                  padding:
                      EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                  child: Row(
                    children: [
                      Icon(Icons.logout, color: Color(0xFFF87171), size: 18),
                      SizedBox(width: 12),
                      Text(
                        'Logout',
                        style: TextStyle(
                          color: Color(0xFFF87171),
                          fontSize: 13,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
          const SizedBox(height: 8),
        ],
      ),
    );
  }
}

class ScaffoldWithSidebar extends StatelessWidget {
  final Widget child;
  const ScaffoldWithSidebar({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        children: [
          const Sidebar(),
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(32),
              child: child,
            ),
          ),
        ],
      ),
    );
  }
}
