import 'package:flutter/material.dart';
import '../../api/student_api.dart';
import '../../widgets/sidebar.dart';

class StudentDashboardPage extends StatefulWidget {
  const StudentDashboardPage({super.key});

  @override
  State<StudentDashboardPage> createState() => _StudentDashboardPageState();
}

class _StudentDashboardPageState extends State<StudentDashboardPage> {
  List<dynamic> _courses = [];
  String _deadline = '';
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final results = await Future.wait([
        StudentApi.getRegistrations(),
        StudentApi.getDeadline(),
      ]);
      setState(() {
        _courses = results[0] as List<dynamic>;
        _deadline = results[1] as String;
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
          const Text('Student Dashboard',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          if (_deadline.isNotEmpty && _deadline != 'None') ...[
            const SizedBox(height: 4),
            Text('Registration deadline: $_deadline',
                style:
                    const TextStyle(fontSize: 13, color: Color(0xFFEA580C))),
          ],
          const SizedBox(height: 24),
          const Text('My Enrolled Courses',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
          const SizedBox(height: 12),
          if (_loading)
            const Center(child: CircularProgressIndicator())
          else if (_courses.isEmpty)
            Text('No courses enrolled yet.',
                style: TextStyle(fontSize: 13, color: Colors.grey[500]))
          else
            Card(
              child: SizedBox(
                width: double.infinity,
                child: DataTable(
                  headingTextStyle: TextStyle(
                      fontWeight: FontWeight.w500,
                      fontSize: 13,
                      color: Colors.grey[600]),
                  dataTextStyle: const TextStyle(fontSize: 13),
                  columns: const [
                    DataColumn(label: Text('Code')),
                    DataColumn(label: Text('Title')),
                    DataColumn(label: Text('Schedule')),
                    DataColumn(label: Text('Room')),
                    DataColumn(label: Text('Status')),
                  ],
                  rows: _courses.map<DataRow>((c) {
                    return DataRow(cells: [
                      DataCell(Text(c['courseCode'] ?? '',
                          style: const TextStyle(
                              fontFamily: 'monospace', fontSize: 13))),
                      DataCell(Text(c['courseTitle'] ?? '')),
                      DataCell(Text(c['dayTime'] ?? '')),
                      DataCell(Text(c['room'] ?? '')),
                      DataCell(Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 8, vertical: 2),
                        decoration: BoxDecoration(
                          color: const Color(0xFFDCFCE7),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(c['status'] ?? '',
                            style: const TextStyle(
                                fontSize: 12, color: Color(0xFF15803D))),
                      )),
                    ]);
                  }).toList(),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
