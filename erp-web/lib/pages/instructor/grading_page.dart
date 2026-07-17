import 'package:flutter/material.dart';
import '../../api/instructor_api.dart';
import '../../widgets/sidebar.dart';

class GradingPage extends StatefulWidget {
  const GradingPage({super.key});

  @override
  State<GradingPage> createState() => _GradingPageState();
}

class _GradingPageState extends State<GradingPage> {
  List<dynamic> _courses = [];
  int? _selectedSection;
  List<dynamic> _students = [];
  final Map<int, TextEditingController> _gradeControllers = {};
  String _msg = '';

  @override
  void initState() {
    super.initState();
    _loadCourses();
  }

  Future<void> _loadCourses() async {
    try {
      final data = await InstructorApi.getMyCourses();
      setState(() => _courses = data);
    } catch (_) {}
  }

  Future<void> _loadStudents(int sectionId) async {
    try {
      final data = await InstructorApi.getStudents(sectionId);
      for (final ctrl in _gradeControllers.values) {
        ctrl.dispose();
      }
      _gradeControllers.clear();
      for (final s in data) {
        final id = s['studentId'] as int;
        _gradeControllers[id] =
            TextEditingController(text: s['grade'] ?? '');
      }
      setState(() {
        _selectedSection = sectionId;
        _students = data;
      });
    } catch (_) {}
  }

  Future<void> _saveGrade(int studentId) async {
    if (_selectedSection == null) return;
    try {
      await InstructorApi.assignGrade(
          studentId, _selectedSection!, _gradeControllers[studentId]!.text);
      setState(() => _msg = 'Grade saved.');
    } catch (_) {
      setState(() => _msg = 'Failed to save grade.');
    }
  }

  @override
  void dispose() {
    for (final ctrl in _gradeControllers.values) {
      ctrl.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ScaffoldWithSidebar(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Grading',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          const SizedBox(height: 16),
          SizedBox(
            width: 400,
            child: DropdownButtonFormField<int>(
              decoration: const InputDecoration(
                hintText: 'Select a section',
              ),
              value: _selectedSection,
              items: _courses.map<DropdownMenuItem<int>>((c) {
                return DropdownMenuItem(
                  value: c['sectionId'] as int,
                  child: Text(
                      '${c['courseCode']} -- ${c['courseTitle']} (Sec ${c['sectionId']})',
                      style: const TextStyle(fontSize: 13)),
                );
              }).toList(),
              onChanged: (v) {
                if (v != null) _loadStudents(v);
              },
            ),
          ),
          if (_msg.isNotEmpty) ...[
            const SizedBox(height: 12),
            Text(_msg,
                style:
                    const TextStyle(fontSize: 13, color: Color(0xFF2563EB))),
          ],
          const SizedBox(height: 16),
          if (_students.isNotEmpty)
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
                    DataColumn(label: Text('Student ID')),
                    DataColumn(label: Text('Username')),
                    DataColumn(label: Text('Grade')),
                    DataColumn(label: Text('')),
                  ],
                  rows: _students.map<DataRow>((s) {
                    final id = s['studentId'] as int;
                    return DataRow(cells: [
                      DataCell(Text('$id')),
                      DataCell(Text(s['username'] ?? '')),
                      DataCell(SizedBox(
                        width: 80,
                        child: TextField(
                          controller: _gradeControllers[id],
                          style: const TextStyle(fontSize: 13),
                          decoration: const InputDecoration(
                            isDense: true,
                            contentPadding: EdgeInsets.symmetric(
                                horizontal: 8, vertical: 8),
                          ),
                        ),
                      )),
                      DataCell(FilledButton(
                        style: FilledButton.styleFrom(
                          backgroundColor: const Color(0xFF16A34A),
                          padding: const EdgeInsets.symmetric(
                              horizontal: 12, vertical: 4),
                          textStyle: const TextStyle(fontSize: 12),
                        ),
                        onPressed: () => _saveGrade(id),
                        child: const Text('Save'),
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
