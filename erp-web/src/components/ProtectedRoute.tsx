import { Navigate } from 'react-router-dom'
import { getToken, getRole } from '../hooks/useAuth'

interface Props {
  children: React.ReactNode
  role?: string
}

export default function ProtectedRoute({ children, role }: Props) {
  const token = getToken()
  const userRole = getRole()

  if (!token) return <Navigate to="/login" replace />
  if (role && userRole?.toLowerCase() !== role.toLowerCase()) return <Navigate to="/login" replace />

  return <>{children}</>
}
