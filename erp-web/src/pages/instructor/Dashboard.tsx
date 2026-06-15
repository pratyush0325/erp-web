import { useEffect, useState } from 'react'
import Sidebar from '../../components/Sidebar'
import { getMyCourses, getPendingGrades } from '../../api/instructor'

export default function InstructorDashboard() {
  const [courses, setCourses] = useState<any[]>([])
  const [pending, setPending] = useState(0)

  useEffect(() => {
    getMyCourses().then((r) => setCourses(r.data))
    getPendingGrades().then((r) => setPending(r.data))
  }, [])

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1 p-8 bg-gray-50 min-h-screen">
        <h1 className="text-2xl font-bold mb-2 text-gray-800">Instructor Dashboard</h1>
        <p className="text-sm text-orange-600 mb-6">Pending grades to submit: {pending}</p>
        <h2 className="text-lg font-semibold mb-3 text-gray-700">My Sections</h2>
        <div className="overflow-x-auto bg-white rounded-xl shadow-sm">
          <table className="w-full text-sm">
            <thead className="bg-gray-100 text-gray-600">
              <tr>
                {['Section ID', 'Code', 'Title', 'Schedule', 'Room', 'Enrolled', 'Capacity'].map((h) => (
                  <th key={h} className="px-4 py-3 text-left font-medium">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {courses.map((c: any) => (
                <tr key={c.sectionId} className="border-t hover:bg-gray-50">
                  <td className="px-4 py-3">{c.sectionId}</td>
                  <td className="px-4 py-3 font-mono">{c.courseCode}</td>
                  <td className="px-4 py-3">{c.courseTitle}</td>
                  <td className="px-4 py-3">{c.dayTime}</td>
                  <td className="px-4 py-3">{c.room}</td>
                  <td className="px-4 py-3 text-center">{c.enrolledCount}</td>
                  <td className="px-4 py-3 text-center">{c.capacity}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  )
}
