import 'package:flutter/material.dart';
import '../../api/admin_api.dart';
import '../../widgets/sidebar.dart';

class AdminDashboardPage extends StatefulWidget {
  const AdminDashboardPage({super.key});

  @override
  State<AdminDashboardPage> createState() => _AdminDashboardPageState();
}

class _AdminDashboardPageState extends State<AdminDashboardPage> {
  Map<String, dynamic>? _stats;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final data = await AdminApi.getStats();
      setState(() {
        _stats = data;
        _loading = false;
      });
    } catch (_) {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return ScaffoldWithSidebar(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Admin Dashboard',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          const SizedBox(height: 24),
          if (_loading)
            const Center(child: CircularProgressIndicator())
          else if (_stats != null)
            Wrap(
              spacing: 16,
              runSpacing: 16,
              children: [
                _statCard('Total Users', '${_stats!['totalUsers'] ?? 0}'),
                _statCard('Courses', '${_stats!['totalCourses'] ?? 0}'),
                _statCard('Sections', '${_stats!['totalSections'] ?? 0}'),
                _statCard(
                  'Maintenance',
                  (_stats!['maintenanceOn'] == true) ? 'ON' : 'OFF',
                  accent: _stats!['maintenanceOn'] == true,
                ),
              ],
            ),
        ],
      ),
    );
  }

  Widget _statCard(String label, String value, {bool accent = false}) {
    return Container(
      width: 200,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: accent ? const Color(0xFFFFF7ED) : Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: accent
            ? Border.all(color: const Color(0xFFFED7AA))
            : null,
        boxShadow: accent
            ? null
            : [
                BoxShadow(
                    color: Colors.black.withValues(alpha: 0.04),
                    blurRadius: 4,
                    offset: const Offset(0, 1))
              ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label,
              style: TextStyle(
                  fontSize: 11,
                  color: Colors.grey[500],
                  fontWeight: FontWeight.w500,
                  letterSpacing: 0.5)),
          const SizedBox(height: 4),
          Text(value,
              style: TextStyle(
                  fontSize: 28,
                  fontWeight: FontWeight.bold,
                  color: accent
                      ? const Color(0xFFEA580C)
                      : const Color(0xFF1F2937))),
        ],
      ),
    );
  }
}
