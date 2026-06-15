import { useEffect, useState } from 'react'
import Sidebar from '../../components/Sidebar'
import { getProfile } from '../../api/student'

export default function Profile() {
  const [profile, setProfile] = useState<any>(null)

  useEffect(() => {
    getProfile().then((r) => setProfile(r.data))
  }, [])

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1 p-8 bg-gray-50 min-h-screen">
        <h1 className="text-2xl font-bold mb-6 text-gray-800">My Profile</h1>
        {profile ? (
          <div className="bg-white rounded-xl shadow-sm p-6 max-w-sm space-y-3">
            <Row label="Username" value={profile.username} />
            <Row label="Roll No." value={profile.rollNo} />
            <Row label="Program" value={profile.program} />
            <Row label="Year" value={String(profile.year)} />
          </div>
        ) : (
          <p className="text-gray-500">Loading...</p>
        )}
      </main>
    </div>
  )
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex justify-between text-sm">
      <span className="text-gray-500">{label}</span>
      <span className="font-medium text-gray-800">{value}</span>
    </div>
  )
}
