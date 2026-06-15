import { useEffect, useState } from 'react'
import Sidebar from '../../components/Sidebar'
import { getUsers, deleteUser, toggleUserStatus, addUser } from '../../api/admin'

export default function Users() {
  const [users, setUsers] = useState<any[]>([])
  const [form, setForm] = useState({ username: '', password: '', role: 'student', extra1: '', extra2: '', extra3: '' })
  const [msg, setMsg] = useState('')

  const load = () => getUsers().then((r) => setUsers(r.data))
  useEffect(() => { load() }, [])

  const handleAdd = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await addUser(form)
      setMsg('User added.')
      setForm({ username: '', password: '', role: 'student', extra1: '', extra2: '', extra3: '' })
      load()
    } catch { setMsg('Failed to add user.') }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this user?')) return
    await deleteUser(id)
    load()
  }

  const handleToggle = async (id: number, status: string) => {
    await toggleUserStatus(id, status)
    load()
  }

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1 p-8 bg-gray-50 min-h-screen">
        <h1 className="text-2xl font-bold mb-6 text-gray-800">User Management</h1>
        <form onSubmit={handleAdd} className="bg-white rounded-xl shadow-sm p-4 mb-6 flex flex-wrap gap-2">
          {[
            { key: 'username', placeholder: 'Username' },
            { key: 'password', placeholder: 'Password' },
            { key: 'extra1', placeholder: 'Roll No / Dept' },
            { key: 'extra2', placeholder: 'Year' },
            { key: 'extra3', placeholder: 'Program' },
          ].map(({ key, placeholder }) => (
            <input key={key} className="border rounded px-3 py-1.5 text-sm"
              placeholder={placeholder} value={(form as any)[key]}
              onChange={(e) => setForm({ ...form, [key]: e.target.value })} />
          ))}
          <select className="border rounded px-3 py-1.5 text-sm" value={form.role}
            onChange={(e) => setForm({ ...form, role: e.target.value })}>
            <option value="student">Student</option>
            <option value="instructor">Instructor</option>
            <option value="admin">Admin</option>
          </select>
          <button type="submit" className="bg-blue-600 text-white px-4 py-1.5 rounded text-sm hover:bg-blue-700">
            Add User
          </button>
          {msg && <span className="text-sm text-blue-600 self-center">{msg}</span>}
        </form>

        <div className="overflow-x-auto bg-white rounded-xl shadow-sm">
          <table className="w-full text-sm">
            <thead className="bg-gray-100 text-gray-600">
              <tr>{['ID', 'Username', 'Role', 'Status', 'Actions'].map((h) => (
                <th key={h} className="px-4 py-3 text-left font-medium">{h}</th>
              ))}</tr>
            </thead>
            <tbody>
              {users.map((u: any) => (
                <tr key={u.userId} className="border-t hover:bg-gray-50">
                  <td className="px-4 py-3">{u.userId}</td>
                  <td className="px-4 py-3">{u.username}</td>
                  <td className="px-4 py-3 capitalize">{u.role}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-0.5 rounded-full text-xs ${u.status === 'Active' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                      {u.status}
                    </span>
                  </td>
                  <td className="px-4 py-3 flex gap-2">
                    <button onClick={() => handleToggle(u.userId, u.status)}
                      className="text-xs px-2 py-1 rounded bg-yellow-100 text-yellow-700 hover:bg-yellow-200">
                      Toggle
                    </button>
                    <button onClick={() => handleDelete(u.userId)}
                      className="text-xs px-2 py-1 rounded bg-red-100 text-red-700 hover:bg-red-200">
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  )
}
