import { useEffect, useState } from 'react'
import Sidebar from '../../components/Sidebar'
import { getCatalog, register } from '../../api/student'

export default function Catalog() {
  const [catalog, setCatalog] = useState<any[]>([])
  const [msg, setMsg] = useState('')

  useEffect(() => {
    getCatalog().then((r) => setCatalog(r.data))
  }, [])

  const handleRegister = async (sectionId: number) => {
    try {
      const { data } = await register(sectionId)
      setMsg(`Registration: ${data}`)
    } catch {
      setMsg('Registration failed.')
    }
  }

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1 p-8 bg-gray-50 min-h-screen">
        <h1 className="text-2xl font-bold mb-6 text-gray-800">Course Catalog</h1>
        {msg && <p className="mb-4 text-sm text-blue-600">{msg}</p>}
        <div className="overflow-x-auto bg-white rounded-xl shadow-sm">
          <table className="w-full text-sm">
            <thead className="bg-gray-100 text-gray-600">
              <tr>
                {['Code', 'Title', 'Credits', 'Instructor', 'Semester', 'Capacity', ''].map((h) => (
                  <th key={h} className="px-4 py-3 text-left font-medium">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {catalog.map((c: any) => (
                <tr key={c.sectionId} className="border-t hover:bg-gray-50">
                  <td className="px-4 py-3 font-mono">{c.courseCode}</td>
                  <td className="px-4 py-3">{c.courseTitle}</td>
                  <td className="px-4 py-3 text-center">{c.credits}</td>
                  <td className="px-4 py-3">{c.instructorName}</td>
                  <td className="px-4 py-3">{c.semester} {c.year}</td>
                  <td className="px-4 py-3 text-center">{c.capacity}</td>
                  <td className="px-4 py-3">
                    <button
                      onClick={() => handleRegister(c.sectionId)}
                      className="bg-blue-600 text-white text-xs px-3 py-1 rounded hover:bg-blue-700">
                      Register
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
