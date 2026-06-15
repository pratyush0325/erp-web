import client from './client'

export const getRegistrations = () => client.get('/student/registrations')
export const register = (sectionId: number) => client.post(`/student/register/${sectionId}`)
export const drop = (sectionId: number) => client.delete(`/student/drop/${sectionId}`)
export const getGrades = () => client.get('/student/grades')
export const getProfile = () => client.get('/student/profile')
export const getDeadline = () => client.get('/student/deadline')
export const getCatalog = () => client.get('/catalog')
