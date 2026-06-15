import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute'
import Login from './pages/Login'
import StudentDashboard from './pages/student/Dashboard'
import Catalog from './pages/student/Catalog'
import Grades from './pages/student/Grades'
import Profile from './pages/student/Profile'
import InstructorDashboard from './pages/instructor/Dashboard'
import Grading from './pages/instructor/Grading'
import AdminDashboard from './pages/admin/Dashboard'
import Users from './pages/admin/Users'
import Settings from './pages/admin/Settings'

export default function Router() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<Navigate to="/login" replace />} />

        <Route path="/student" element={<ProtectedRoute role="student"><StudentDashboard /></ProtectedRoute>} />
        <Route path="/student/courses" element={<ProtectedRoute role="student"><StudentDashboard /></ProtectedRoute>} />
        <Route path="/student/catalog" element={<ProtectedRoute role="student"><Catalog /></ProtectedRoute>} />
        <Route path="/student/grades" element={<ProtectedRoute role="student"><Grades /></ProtectedRoute>} />
        <Route path="/student/profile" element={<ProtectedRoute role="student"><Profile /></ProtectedRoute>} />

        <Route path="/instructor" element={<ProtectedRoute role="instructor"><InstructorDashboard /></ProtectedRoute>} />
        <Route path="/instructor/classes" element={<ProtectedRoute role="instructor"><InstructorDashboard /></ProtectedRoute>} />
        <Route path="/instructor/grading" element={<ProtectedRoute role="instructor"><Grading /></ProtectedRoute>} />
        <Route path="/instructor/stats" element={<ProtectedRoute role="instructor"><InstructorDashboard /></ProtectedRoute>} />

        <Route path="/admin" element={<ProtectedRoute role="admin"><AdminDashboard /></ProtectedRoute>} />
        <Route path="/admin/users" element={<ProtectedRoute role="admin"><Users /></ProtectedRoute>} />
        <Route path="/admin/settings" element={<ProtectedRoute role="admin"><Settings /></ProtectedRoute>} />
        <Route path="/admin/courses" element={<ProtectedRoute role="admin"><AdminDashboard /></ProtectedRoute>} />
        <Route path="/admin/sections" element={<ProtectedRoute role="admin"><AdminDashboard /></ProtectedRoute>} />
      </Routes>
    </BrowserRouter>
  )
}
