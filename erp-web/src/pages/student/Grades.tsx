import { useEffect, useState } from 'react'
import Sidebar from '../../components/Sidebar'
import { getGrades } from '../../api/student'

export default function Grades() {
  const [grades, setGrades] = useState<any[]>([])

  useEffect(() => {
    getGrades().then((r) => setGrades(r.data))
  }, [])

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1 p-8 bg-gray-50 min-h-screen">
        <h1 className="text-2xl font-bold mb-6 text-gray-800">My Grades</h1>
        <div className="overflow-x-auto bg-white rounded-xl shadow-sm">
          <table className="w-full text-sm">
            <thead className="bg-gray-100 text-gray-600">
              <tr>
                {['Course', 'Title', 'Component', 'Score', 'Max', 'Weight %'].map((h) => (
                  <th key={h} className="px-4 py-3 text-left font-medium">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {grades.map((g: any, i: number) => (
                <tr key={i} className="border-t hover:bg-gray-50">
                  <td className="px-4 py-3 font-mono">{g.courseCode}</td>
                  <td className="px-4 py-3">{g.courseTitle}</td>
                  <td className="px-4 py-3">{g.assignmentName}</td>
                  <td className="px-4 py-3 font-semibold">{g.scoreObtained}</td>
                  <td className="px-4 py-3">{g.maxScore}</td>
                  <td className="px-4 py-3">{g.weightPercent}%</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  )
}
