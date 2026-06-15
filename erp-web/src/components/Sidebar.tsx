import { Link, useNavigate } from 'react-router-dom'
import { clearAuth, getRole } from '../hooks/useAuth'

interface NavItem {
  label: string
  to: string
}

const navMap: Record<string, NavItem[]> = {
  student: [
    { label: 'Dashboard', to: '/student' },
    { label: 'My Courses', to: '/student/courses' },
    { label: 'Grades', to: '/student/grades' },
    { label: 'Catalog', to: '/student/catalog' },
    { label: 'Profile', to: '/student/profile' },
  ],
  instructor: [
    { label: 'Dashboard', to: '/instructor' },
    { label: 'Class List', to: '/instructor/classes' },
    { label: 'Grading', to: '/instructor/grading' },
    { label: 'Statistics', to: '/instructor/stats' },
  ],
  admin: [
    { label: 'Dashboard', to: '/admin' },
    { label: 'Users', to: '/admin/users' },
    { label: 'Courses', to: '/admin/courses' },
    { label: 'Sections', to: '/admin/sections' },
    { label: 'Settings', to: '/admin/settings' },
  ],
}

export default function Sidebar() {
  const role = getRole()?.toLowerCase() ?? ''
  const nav = navMap[role] ?? []
  const navigate = useNavigate()

  const logout = () => {
    clearAuth()
    navigate('/login')
  }

  return (
    <aside className="w-56 min-h-screen bg-gray-900 text-white flex flex-col p-4 gap-1">
      <div className="text-xl font-bold mb-6 px-2">ERP</div>
      {nav.map((item) => (
        <Link key={item.to} to={item.to}
          className="px-3 py-2 rounded hover:bg-gray-700 text-sm transition-colors">
          {item.label}
        </Link>
      ))}
      <div className="flex-1" />
      <button onClick={logout}
        className="px-3 py-2 text-sm text-left text-red-400 hover:bg-gray-700 rounded transition-colors">
        Logout
      </button>
    </aside>
  )
}
