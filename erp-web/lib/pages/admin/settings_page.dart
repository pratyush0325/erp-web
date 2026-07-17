import 'package:flutter/material.dart';
import '../../api/admin_api.dart';
import '../../widgets/sidebar.dart';

class SettingsPage extends StatefulWidget {
  const SettingsPage({super.key});

  @override
  State<SettingsPage> createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  bool _maintenanceOn = false;
  final _deadlineCtrl = TextEditingController();
  String _msg = '';

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final results = await Future.wait([
        AdminApi.getMaintenance(),
        AdminApi.getDeadline(),
      ]);
      setState(() {
        _maintenanceOn = results[0] as bool;
        _deadlineCtrl.text = results[1] as String;
      });
    } catch (_) {}
  }

  Future<void> _toggleMaintenance() async {
    final next = !_maintenanceOn;
    await AdminApi.setMaintenance(next);
    setState(() {
      _maintenanceOn = next;
      _msg = 'Maintenance mode ${next ? 'enabled' : 'disabled'}.';
    });
  }

  Future<void> _saveDeadline() async {
    await AdminApi.setDeadline(_deadlineCtrl.text);
    setState(() => _msg = 'Registration deadline saved.');
  }

  Future<void> _pickDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: DateTime.tryParse(_deadlineCtrl.text) ?? DateTime.now(),
      firstDate: DateTime(2020),
      lastDate: DateTime(2100),
    );
    if (picked != null) {
      _deadlineCtrl.text =
          '${picked.year}-${picked.month.toString().padLeft(2, '0')}-${picked.day.toString().padLeft(2, '0')}';
    }
  }

  @override
  void dispose() {
    _deadlineCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ScaffoldWithSidebar(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('System Settings',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          if (_msg.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(_msg,
                style:
                    const TextStyle(fontSize: 13, color: Color(0xFF2563EB))),
          ],
          const SizedBox(height: 16),
          Card(
            child: Container(
              width: 420,
              padding: const EdgeInsets.all(24),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('Maintenance Mode',
                          style: TextStyle(
                              fontSize: 15, fontWeight: FontWeight.w500)),
                      const SizedBox(height: 2),
                      Text('Block all student registrations',
                          style:
                              TextStyle(fontSize: 12, color: Colors.grey[500])),
                    ],
                  ),
                  FilledButton(
                    style: FilledButton.styleFrom(
                      backgroundColor: _maintenanceOn
                          ? const Color(0xFFFEE2E2)
                          : const Color(0xFFDCFCE7),
                      foregroundColor: _maintenanceOn
                          ? const Color(0xFFDC2626)
                          : const Color(0xFF15803D),
                    ),
                    onPressed: _toggleMaintenance,
                    child: Text(_maintenanceOn ? 'Turn OFF' : 'Turn ON'),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          Card(
            child: Container(
              width: 420,
              padding: const EdgeInsets.all(24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Registration Deadline',
                      style: TextStyle(
                          fontSize: 15, fontWeight: FontWeight.w500)),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Expanded(
                        child: TextField(
                          controller: _deadlineCtrl,
                          readOnly: true,
                          onTap: _pickDate,
                          style: const TextStyle(fontSize: 13),
                          decoration: const InputDecoration(
                            hintText: 'Select date',
                            suffixIcon: Icon(Icons.calendar_today, size: 18),
                          ),
                        ),
                      ),
                      const SizedBox(width: 8),
                      FilledButton(
                        onPressed: _saveDeadline,
                        child: const Text('Save'),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
