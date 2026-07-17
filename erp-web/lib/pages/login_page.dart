import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../api/auth_api.dart';
import '../services/auth_service.dart';

class _DemoAccount {
  final String label;
  final String username;
  const _DemoAccount(this.label, this.username);
}

const _demoAccounts = [
  _DemoAccount('Admin', 'admin'),
  _DemoAccount('Instructor', 'prof_smith'),
  _DemoAccount('Student', 'alice'),
];

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _usernameCtrl = TextEditingController();
  final _passwordCtrl = TextEditingController();
  String _error = '';
  bool _loading = false;

  Future<void> _submit() async {
    setState(() {
      _error = '';
      _loading = true;
    });
    try {
      final data =
          await AuthApi.login(_usernameCtrl.text, _passwordCtrl.text);
      final token = data['token'] as String;
      final role = data['role'] as String;
      await AuthService.saveAuth(token, role);
      if (!mounted) return;
      final r = role.toLowerCase();
      if (r == 'admin') {
        context.go('/admin');
      } else if (r == 'instructor') {
        context.go('/instructor');
      } else {
        context.go('/student');
      }
    } on DioException catch (e) {
      final status = e.response?.data?['status'];
      setState(() {
        _error = status == 'ACCOUNT_LOCKED'
            ? 'Account is locked. Try again later.'
            : status == 'ACCOUNT_INACTIVE'
                ? 'Account is inactive.'
                : 'Invalid username or password.';
      });
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  void _fillDemo(_DemoAccount account) {
    _usernameCtrl.text = account.username;
    _passwordCtrl.text = 'demo123';
    setState(() => _error = '');
  }

  @override
  void dispose() {
    _usernameCtrl.dispose();
    _passwordCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Card(
          elevation: 2,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          child: Container(
            width: 380,
            padding: const EdgeInsets.all(32),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const Text('ERP Login',
                    style:
                        TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
                const SizedBox(height: 4),
                Text('University Course Management',
                    style: TextStyle(fontSize: 12, color: Colors.grey[500])),
                const SizedBox(height: 24),
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: const Color(0xFFEFF6FF),
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(color: const Color(0xFFDBEAFE)),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('Try a demo account',
                          style: TextStyle(
                              fontSize: 12,
                              fontWeight: FontWeight.w500,
                              color: Color(0xFF2563EB))),
                      const SizedBox(height: 8),
                      Row(
                        children: _demoAccounts
                            .map((a) => Expanded(
                                  child: Padding(
                                    padding: const EdgeInsets.symmetric(
                                        horizontal: 4),
                                    child: OutlinedButton(
                                      style: OutlinedButton.styleFrom(
                                        padding: const EdgeInsets.symmetric(
                                            vertical: 8),
                                        side: const BorderSide(
                                            color: Color(0xFFBFDBFE)),
                                        foregroundColor:
                                            const Color(0xFF1D4ED8),
                                        textStyle:
                                            const TextStyle(fontSize: 12),
                                      ),
                                      onPressed: () => _fillDemo(a),
                                      child: Text(a.label),
                                    ),
                                  ),
                                ))
                            .toList(),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 20),
                TextField(
                  controller: _usernameCtrl,
                  decoration: const InputDecoration(hintText: 'Username'),
                  textInputAction: TextInputAction.next,
                ),
                const SizedBox(height: 12),
                TextField(
                  controller: _passwordCtrl,
                  obscureText: true,
                  decoration: const InputDecoration(hintText: 'Password'),
                  onSubmitted: (_) => _submit(),
                ),
                if (_error.isNotEmpty) ...[
                  const SizedBox(height: 12),
                  Text(_error,
                      style:
                          const TextStyle(color: Colors.red, fontSize: 13)),
                ],
                const SizedBox(height: 16),
                FilledButton(
                  onPressed: _loading ? null : _submit,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    child: Text(_loading ? 'Signing in...' : 'Sign In'),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
