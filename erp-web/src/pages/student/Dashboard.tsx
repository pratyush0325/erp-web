import { useEffect, useState } from 'react'
import Sidebar from '../../components/Sidebar'
import { getRegistrations, getDeadline } from '../../api/student'

export default function StudentDashboard() {
  const [courses, setCourses] = useState<any[]>([])
  const [deadline, setDeadline] = useState('')

  useEffect(() => {
    getRegistrations().then((r) => setCourses(r.data))
    getDeadline().then((r) => setDeadline(r.data))
  }, [])

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1 p-8 bg-gray-50 min-h-screen">
        <h1 className="text-2xl font-bold mb-2 text-gray-800">Student Dashboard</h1>
        {deadline && deadline !== 'None' && (
          <p className="text-sm text-orange-600 mb-6">Registration deadline: {deadline}</p>
        )}
        <section>
          <h2 className="text-lg font-semibold mb-3 text-gray-700">My Enrolled Courses</h2>
          {courses.length === 0 ? (
            <p className="text-gray-500 text-sm">No courses enrolled yet.</p>
          ) : (
            <div className="overflow-x-auto bg-white rounded-xl shadow-sm">
              <table className="w-full text-sm">
                <thead className="bg-gray-100 text-gray-600">
                  <tr>
                    {['Code', 'Title', 'Schedule', 'Room', 'Status'].map((h) => (
                      <th key={h} className="px-4 py-3 text-left font-medium">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {courses.map((c: any) => (
                    <tr key={c.sectionId} className="border-t hover:bg-gray-50">
                      <td className="px-4 py-3 font-mono">{c.courseCode}</td>
                      <td className="px-4 py-3">{c.courseTitle}</td>
                      <td className="px-4 py-3">{c.dayTime}</td>
                      <td className="px-4 py-3">{c.room}</td>
                      <td className="px-4 py-3">
                        <span className="px-2 py-0.5 rounded-full text-xs bg-green-100 text-green-700">
                          {c.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>
    </div>
  )
}
