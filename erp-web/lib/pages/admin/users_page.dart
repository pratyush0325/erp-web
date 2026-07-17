import 'package:flutter/material.dart';
import '../../api/admin_api.dart';
import '../../widgets/sidebar.dart';

class UsersPage extends StatefulWidget {
  const UsersPage({super.key});

  @override
  State<UsersPage> createState() => _UsersPageState();
}

class _UsersPageState extends State<UsersPage> {
  List<dynamic> _users = [];
  final _usernameCtrl = TextEditingController();
  final _passwordCtrl = TextEditingController();
  final _extra1Ctrl = TextEditingController();
  final _extra2Ctrl = TextEditingController();
  final _extra3Ctrl = TextEditingController();
  String _role = 'student';
  String _msg = '';

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final data = await AdminApi.getUsers();
      setState(() => _users = data);
    } catch (_) {}
  }

  Future<void> _addUser() async {
    try {
      await AdminApi.addUser({
        'username': _usernameCtrl.text,
        'password': _passwordCtrl.text,
        'role': _role,
        'extra1': _extra1Ctrl.text,
        'extra2': _extra2Ctrl.text,
        'extra3': _extra3Ctrl.text,
      });
      setState(() => _msg = 'User added.');
      _usernameCtrl.clear();
      _passwordCtrl.clear();
      _extra1Ctrl.clear();
      _extra2Ctrl.clear();
      _extra3Ctrl.clear();
      _load();
    } catch (_) {
      setState(() => _msg = 'Failed to add user.');
    }
  }

  Future<void> _deleteUser(int id) async {
    await AdminApi.deleteUser(id);
    _load();
  }

  Future<void> _toggleStatus(int id, String status) async {
    await AdminApi.toggleUserStatus(id, status);
    _load();
  }

  @override
  void dispose() {
    _usernameCtrl.dispose();
    _passwordCtrl.dispose();
    _extra1Ctrl.dispose();
    _extra2Ctrl.dispose();
    _extra3Ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ScaffoldWithSidebar(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('User Management',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          const SizedBox(height: 16),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Wrap(
                spacing: 8,
                runSpacing: 8,
                crossAxisAlignment: WrapCrossAlignment.center,
                children: [
                  _input(_usernameCtrl, 'Username'),
                  _input(_passwordCtrl, 'Password'),
                  _input(_extra1Ctrl, 'Roll No / Dept'),
                  _input(_extra2Ctrl, 'Year'),
                  _input(_extra3Ctrl, 'Program'),
                  SizedBox(
                    width: 120,
                    child: DropdownButtonFormField<String>(
                      value: _role,
                      isDense: true,
                      decoration: const InputDecoration(
                        contentPadding:
                            EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                      ),
                      items: const [
                        DropdownMenuItem(
                            value: 'student', child: Text('Student')),
                        DropdownMenuItem(
                            value: 'instructor', child: Text('Instructor')),
                        DropdownMenuItem(value: 'admin', child: Text('Admin')),
                      ],
                      onChanged: (v) => setState(() => _role = v!),
                    ),
                  ),
                  FilledButton(
                    onPressed: _addUser,
                    child: const Text('Add User'),
                  ),
                  if (_msg.isNotEmpty)
                    Text(_msg,
                        style: const TextStyle(
                            fontSize: 13, color: Color(0xFF2563EB))),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
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
                  DataColumn(label: Text('ID')),
                  DataColumn(label: Text('Username')),
                  DataColumn(label: Text('Role')),
                  DataColumn(label: Text('Status')),
                  DataColumn(label: Text('Actions')),
                ],
                rows: _users.map<DataRow>((u) {
                  final status = u['status'] ?? '';
                  final isActive = status == 'Active';
                  return DataRow(cells: [
                    DataCell(Text('${u['userId'] ?? ''}')),
                    DataCell(Text(u['username'] ?? '')),
                    DataCell(Text(
                        (u['role'] ?? '').toString().substring(0, 1).toUpperCase() +
                            (u['role'] ?? '').toString().substring(1))),
                    DataCell(Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 8, vertical: 2),
                      decoration: BoxDecoration(
                        color: isActive
                            ? const Color(0xFFDCFCE7)
                            : const Color(0xFFFEE2E2),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(status,
                          style: TextStyle(
                              fontSize: 12,
                              color: isActive
                                  ? const Color(0xFF15803D)
                                  : const Color(0xFFDC2626))),
                    )),
                    DataCell(Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        TextButton(
                          style: TextButton.styleFrom(
                            padding: const EdgeInsets.symmetric(
                                horizontal: 8, vertical: 4),
                            backgroundColor: const Color(0xFFFEF3C7),
                            foregroundColor: const Color(0xFFA16207),
                            textStyle: const TextStyle(fontSize: 12),
                          ),
                          onPressed: () =>
                              _toggleStatus(u['userId'] as int, status),
                          child: const Text('Toggle'),
                        ),
                        const SizedBox(width: 8),
                        TextButton(
                          style: TextButton.styleFrom(
                            padding: const EdgeInsets.symmetric(
                                horizontal: 8, vertical: 4),
                            backgroundColor: const Color(0xFFFEE2E2),
                            foregroundColor: const Color(0xFFDC2626),
                            textStyle: const TextStyle(fontSize: 12),
                          ),
                          onPressed: () => _deleteUser(u['userId'] as int),
                          child: const Text('Delete'),
                        ),
                      ],
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

  Widget _input(TextEditingController ctrl, String hint) {
    return SizedBox(
      width: 140,
      child: TextField(
        controller: ctrl,
        style: const TextStyle(fontSize: 13),
        decoration: InputDecoration(
          hintText: hint,
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        ),
      ),
    );
  }
}
