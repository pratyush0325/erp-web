import { useEffect, useState } from 'react'
import Sidebar from '../../components/Sidebar'
import { getMaintenance, setMaintenance, getDeadline, setDeadline } from '../../api/admin'

export default function Settings() {
  const [maintenanceOn, setMaintenanceOn] = useState(false)
  const [deadline, setDeadlineVal] = useState('')
  const [msg, setMsg] = useState('')

  useEffect(() => {
    getMaintenance().then((r) => setMaintenanceOn(r.data.maintenanceOn))
    getDeadline().then((r) => setDeadlineVal(r.data.deadline || ''))
  }, [])

  const toggleMaintenance = async () => {
    await setMaintenance(!maintenanceOn)
    setMaintenanceOn(!maintenanceOn)
    setMsg(`Maintenance mode ${!maintenanceOn ? 'enabled' : 'disabled'}.`)
  }

  const saveDeadline = async () => {
    await setDeadline(deadline)
    setMsg('Registration deadline saved.')
  }

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1 p-8 bg-gray-50 min-h-screen">
        <h1 className="text-2xl font-bold mb-6 text-gray-800">System Settings</h1>
        {msg && <p className="mb-4 text-sm text-blue-600">{msg}</p>}

        <div className="bg-white rounded-xl shadow-sm p-6 mb-4 max-w-md space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="font-medium text-gray-800">Maintenance Mode</p>
              <p className="text-xs text-gray-500">Block all student registrations</p>
            </div>
            <button onClick={toggleMaintenance}
              className={`px-4 py-2 rounded-lg text-sm font-medium ${maintenanceOn ? 'bg-red-100 text-red-700 hover:bg-red-200' : 'bg-green-100 text-green-700 hover:bg-green-200'}`}>
              {maintenanceOn ? 'Turn OFF' : 'Turn ON'}
            </button>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6 max-w-md">
          <p className="font-medium text-gray-800 mb-3">Registration Deadline</p>
          <div className="flex gap-2">
            <input type="date" className="border rounded px-3 py-2 text-sm flex-1"
              value={deadline} onChange={(e) => setDeadlineVal(e.target.value)} />
            <button onClick={saveDeadline}
              className="bg-blue-600 text-white px-4 py-2 rounded text-sm hover:bg-blue-700">
              Save
            </button>
          </div>
        </div>
      </main>
    </div>
  )
}
