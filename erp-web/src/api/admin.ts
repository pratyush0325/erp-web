import client from './client'

export const getUsers = () => client.get('/admin/users')
export const addUser = (data: object) => client.post('/admin/users', data)
export const deleteUser = (userId: number) => client.delete(`/admin/users/${userId}`)
export const toggleUserStatus = (userId: number, currentStatus: string) =>
  client.patch(`/admin/users/${userId}/status`, { currentStatus })

export const getCourses = () => client.get('/admin/courses')
export const addCourse = (data: object) => client.post('/admin/courses', data)
export const deleteCourse = (code: string) => client.delete(`/admin/courses/${code}`)

export const getSections = () => client.get('/admin/sections')
export const addSection = (data: object) => client.post('/admin/sections', data)
export const updateSection = (sectionId: number, data: object) => client.patch(`/admin/sections/${sectionId}`, data)
export const deleteSection = (sectionId: number) => client.delete(`/admin/sections/${sectionId}`)
export const getInstructors = () => client.get('/admin/instructors')

export const getStats = () => client.get('/admin/stats')

export const getMaintenance = () => client.get('/admin/maintenance')
export const setMaintenance = (enabled: boolean) => client.post('/admin/maintenance', { enabled })

export const getDeadline = () => client.get('/admin/deadline')
export const setDeadline = (deadline: string) => client.post('/admin/deadline', { deadline })

export const backup = (path: string) => client.post('/admin/backup', { path })
export const restore = (path: string) => client.post('/admin/restore', { path })
