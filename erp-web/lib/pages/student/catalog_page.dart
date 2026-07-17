import 'package:flutter/material.dart';
import '../../api/student_api.dart';
import '../../widgets/sidebar.dart';

class CatalogPage extends StatefulWidget {
  const CatalogPage({super.key});

  @override
  State<CatalogPage> createState() => _CatalogPageState();
}

class _CatalogPageState extends State<CatalogPage> {
  List<dynamic> _catalog = [];
  String _msg = '';
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final data = await StudentApi.getCatalog();
      setState(() {
        _catalog = data;
        _loading = false;
      });
    } catch (_) {
      setState(() => _loading = false);
    }
  }

  Future<void> _register(int sectionId) async {
    try {
      final result = await StudentApi.register(sectionId);
      setState(() => _msg = 'Registration: $result');
    } catch (_) {
      setState(() => _msg = 'Registration failed.');
    }
  }

  @override
  Widget build(BuildContext context) {
    return ScaffoldWithSidebar(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Course Catalog',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          if (_msg.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(_msg,
                style:
                    const TextStyle(fontSize: 13, color: Color(0xFF2563EB))),
          ],
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
                    DataColumn(label: Text('Code')),
                    DataColumn(label: Text('Title')),
                    DataColumn(label: Text('Credits')),
                    DataColumn(label: Text('Instructor')),
                    DataColumn(label: Text('Semester')),
                    DataColumn(label: Text('Capacity')),
                    DataColumn(label: Text('')),
                  ],
                  rows: _catalog.map<DataRow>((c) {
                    return DataRow(cells: [
                      DataCell(Text(c['courseCode'] ?? '',
                          style: const TextStyle(
                              fontFamily: 'monospace', fontSize: 13))),
                      DataCell(Text(c['courseTitle'] ?? '')),
                      DataCell(Text('${c['credits'] ?? ''}')),
                      DataCell(Text(c['instructorName'] ?? '')),
                      DataCell(
                          Text('${c['semester'] ?? ''} ${c['year'] ?? ''}')),
                      DataCell(Text('${c['capacity'] ?? ''}')),
                      DataCell(
                        FilledButton(
                          style: FilledButton.styleFrom(
                            padding: const EdgeInsets.symmetric(
                                horizontal: 12, vertical: 4),
                            textStyle: const TextStyle(fontSize: 12),
                          ),
                          onPressed: () =>
                              _register(c['sectionId'] as int),
                          child: const Text('Register'),
                        ),
                      ),
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
