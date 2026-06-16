import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '../api/auth'
import { saveAuth } from '../hooks/useAuth'

const DEMO_ACCOUNTS = [
  { label: 'Admin',      username: 'admin',      role: 'admin' },
  { label: 'Instructor', username: 'prof_smith', role: 'instructor' },
  { label: 'Student',    username: 'alice',      role: 'student' },
]

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await login(username, password)
      saveAuth(data.token, data.role)
      const role = data.role.toLowerCase()
      if (role === 'admin') navigate('/admin')
      else if (role === 'instructor') navigate('/instructor')
      else navigate('/student')
    } catch (err: any) {
      const status = err.response?.data?.status
      setError(
        status === 'ACCOUNT_LOCKED'   ? 'Account is locked. Try again later.' :
        status === 'ACCOUNT_INACTIVE' ? 'Account is inactive.' :
                                        'Invalid username or password.'
      )
    } finally {
      setLoading(false)
    }
  }

  const fillDemo = (account: typeof DEMO_ACCOUNTS[0]) => {
    setUsername(account.username)
    setPassword('demo123')
    setError('')
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="bg-white p-8 rounded-xl shadow-md w-full max-w-sm">
        <h1 className="text-2xl font-bold mb-1 text-gray-800">ERP Login</h1>
        <p className="text-xs text-gray-400 mb-6">University Course Management</p>

        {/* Demo quick-fill */}
        <div className="mb-5 p-3 bg-blue-50 rounded-lg border border-blue-100">
          <p className="text-xs font-medium text-blue-600 mb-2">Try a demo account</p>
          <div className="flex gap-2">
            {DEMO_ACCOUNTS.map(account => (
              <button
                key={account.username}
                type="button"
                onClick={() => fillDemo(account)}
                className="flex-1 text-xs py-1.5 rounded-md border border-blue-200 text-blue-700 bg-white hover:bg-blue-100 transition-colors"
              >
                {account.label}
              </button>
            ))}
          </div>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            className="border rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            className="border rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          {error && <p className="text-red-500 text-sm">{error}</p>}
          <button
            type="submit"
            disabled={loading}
            className="bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 transition-colors">
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
      </div>
    </div>
  )
}
