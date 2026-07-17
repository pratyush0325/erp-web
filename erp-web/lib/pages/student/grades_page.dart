import 'package:flutter/material.dart';
import '../../api/student_api.dart';
import '../../widgets/sidebar.dart';

class GradesPage extends StatefulWidget {
  const GradesPage({super.key});

  @override
  State<GradesPage> createState() => _GradesPageState();
}

class _GradesPageState extends State<GradesPage> {
  List<dynamic> _grades = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final data = await StudentApi.getGrades();
      setState(() {
        _grades = data;
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
          const Text('My Grades',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          const SizedBox(height: 16),
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
                    DataColumn(label: Text('Course')),
                    DataColumn(label: Text('Title')),
                    DataColumn(label: Text('Component')),
                    DataColumn(label: Text('Score')),
                    DataColumn(label: Text('Max')),
                    DataColumn(label: Text('Weight %')),
                  ],
                  rows: _grades.map<DataRow>((g) {
                    return DataRow(cells: [
                      DataCell(Text(g['courseCode'] ?? '',
                          style: const TextStyle(
                              fontFamily: 'monospace', fontSize: 13))),
                      DataCell(Text(g['courseTitle'] ?? '')),
                      DataCell(Text(g['assignmentName'] ?? '')),
                      DataCell(Text('${g['scoreObtained'] ?? ''}',
                          style:
                              const TextStyle(fontWeight: FontWeight.w600))),
                      DataCell(Text('${g['maxScore'] ?? ''}')),
                      DataCell(Text('${g['weightPercent'] ?? ''}%')),
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
