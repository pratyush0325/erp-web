import 'package:flutter/material.dart';
import '../../api/instructor_api.dart';
import '../../widgets/sidebar.dart';

class InstructorDashboardPage extends StatefulWidget {
  const InstructorDashboardPage({super.key});

  @override
  State<InstructorDashboardPage> createState() =>
      _InstructorDashboardPageState();
}

class _InstructorDashboardPageState extends State<InstructorDashboardPage> {
  List<dynamic> _courses = [];
  int _pending = 0;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final results = await Future.wait([
        InstructorApi.getMyCourses(),
        InstructorApi.getPendingGrades(),
      ]);
      setState(() {
        _courses = results[0] as List<dynamic>;
        _pending = results[1] as int;
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
          const Text('Instructor Dashboard',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          const SizedBox(height: 4),
          Text('Pending grades to submit: $_pending',
              style: const TextStyle(fontSize: 13, color: Color(0xFFEA580C))),
          const SizedBox(height: 24),
          const Text('My Sections',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
          const SizedBox(height: 12),
          if (_loading)
            const Center(child: CircularProgressIndicator())
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
                    DataColumn(label: Text('Section ID')),
                    DataColumn(label: Text('Code')),
                    DataColumn(label: Text('Title')),
                    DataColumn(label: Text('Schedule')),
                    DataColumn(label: Text('Room')),
                    DataColumn(label: Text('Enrolled')),
                    DataColumn(label: Text('Capacity')),
                  ],
                  rows: _courses.map<DataRow>((c) {
                    return DataRow(cells: [
                      DataCell(Text('${c['sectionId'] ?? ''}')),
                      DataCell(Text(c['courseCode'] ?? '',
                          style: const TextStyle(
                              fontFamily: 'monospace', fontSize: 13))),
                      DataCell(Text(c['courseTitle'] ?? '')),
                      DataCell(Text(c['dayTime'] ?? '')),
                      DataCell(Text(c['room'] ?? '')),
                      DataCell(Text('${c['enrolledCount'] ?? ''}')),
                      DataCell(Text('${c['capacity'] ?? ''}')),
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
