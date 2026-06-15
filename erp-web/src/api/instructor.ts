import client from './client'

export const getMyCourses = () => client.get('/instructor/courses')
export const getStudents = (sectionId: number) => client.get(`/instructor/sections/${sectionId}/students`)
export const assignGrade = (studentId: number, sectionId: number, grade: string) =>
  client.put('/instructor/grades', { studentId, sectionId, grade })
export const getAssignments = (sectionId: number) => client.get(`/instructor/sections/${sectionId}/assignments`)
export const updateScore = (assignmentId: number, studentId: number, score: number) =>
  client.put('/instructor/scores', { assignmentId, studentId, score })
export const getStats = () => client.get('/instructor/stats')
export const getPendingGrades = () => client.get('/instructor/pending-grades')
