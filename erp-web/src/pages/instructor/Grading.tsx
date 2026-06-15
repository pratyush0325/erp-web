import { useEffect, useState } from 'react'
import Sidebar from '../../components/Sidebar'
import { getMyCourses, getStudents, assignGrade } from '../../api/instructor'

export default function Grading() {
  const [courses, setCourses] = useState<any[]>([])
  const [selectedSection, setSelectedSection] = useState<number | null>(null)
  const [students, setStudents] = useState<any[]>([])
  const [grades, setGrades] = useState<Record<number, string>>({})
  const [msg, setMsg] = useState('')

  useEffect(() => {
    getMyCourses().then((r) => setCourses(r.data))
  }, [])

  const handleSectionChange = (sectionId: number) => {
    setSelectedSection(sectionId)
    getStudents(sectionId).then((r) => {
      setStudents(r.data)
      const init: Record<number, string> = {}
      r.data.forEach((s: any) => { init[s.studentId] = s.grade || '' })
      setGrades(init)
    })
  }

  const handleSave = async (studentId: number) => {
    if (!selectedSection) return
    try {
      await assignGrade(studentId, selectedSection, grades[studentId])
      setMsg('Grade saved.')
    } catch {
      setMsg('Failed to save grade.')
    }
  }

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1 p-8 bg-gray-50 min-h-screen">
        <h1 className="text-2xl font-bold mb-6 text-gray-800">Grading</h1>
        <div className="mb-4">
          <select
            className="border rounded-lg px-4 py-2 text-sm"
            onChange={(e) => handleSectionChange(Number(e.target.value))}
            defaultValue="">
            <option value="" disabled>Select a section</option>
            {courses.map((c: any) => (
              <option key={c.sectionId} value={c.sectionId}>
                {c.courseCode} — {c.courseTitle} (Sec {c.sectionId})
              </option>
            ))}
          </select>
        </div>
        {msg && <p className="mb-4 text-sm text-blue-600">{msg}</p>}
        {students.length > 0 && (
          <div className="overflow-x-auto bg-white rounded-xl shadow-sm">
            <table className="w-full text-sm">
              <thead className="bg-gray-100 text-gray-600">
                <tr>
                  {['Student ID', 'Username', 'Grade', ''].map((h) => (
                    <th key={h} className="px-4 py-3 text-left font-medium">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {students.map((s: any) => (
                  <tr key={s.studentId} className="border-t hover:bg-gray-50">
                    <td className="px-4 py-3">{s.studentId}</td>
                    <td className="px-4 py-3">{s.username}</td>
                    <td className="px-4 py-3">
                      <input
                        className="border rounded px-2 py-1 w-20 text-sm"
                        value={grades[s.studentId] || ''}
                        onChange={(e) => setGrades({ ...grades, [s.studentId]: e.target.value })}
                      />
                    </td>
                    <td className="px-4 py-3">
                      <button
                        onClick={() => handleSave(s.studentId)}
                        className="bg-green-600 text-white text-xs px-3 py-1 rounded hover:bg-green-700">
                        Save
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  )
}
