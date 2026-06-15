import { useEffect, useState } from 'react'
import Sidebar from '../../components/Sidebar'
import { getStats } from '../../api/admin'

export default function AdminDashboard() {
  const [stats, setStats] = useState<any>(null)

  useEffect(() => {
    getStats().then((r) => setStats(r.data))
  }, [])

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1 p-8 bg-gray-50 min-h-screen">
        <h1 className="text-2xl font-bold mb-6 text-gray-800">Admin Dashboard</h1>
        {stats ? (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <StatCard label="Total Users" value={stats.totalUsers} />
            <StatCard label="Courses" value={stats.totalCourses} />
            <StatCard label="Sections" value={stats.totalSections} />
            <StatCard label="Maintenance" value={stats.maintenanceOn ? 'ON' : 'OFF'}
              accent={stats.maintenanceOn} />
          </div>
        ) : (
          <p className="text-gray-500">Loading...</p>
        )}
      </main>
    </div>
  )
}

function StatCard({ label, value, accent }: { label: string; value: any; accent?: boolean }) {
  return (
    <div className={`rounded-xl p-5 shadow-sm ${accent ? 'bg-orange-50 border border-orange-200' : 'bg-white'}`}>
      <p className="text-xs text-gray-500 uppercase tracking-wide mb-1">{label}</p>
      <p className={`text-3xl font-bold ${accent ? 'text-orange-600' : 'text-gray-800'}`}>{value}</p>
    </div>
  )
}
