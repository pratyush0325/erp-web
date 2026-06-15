import client from './client'

export const login = (username: string, password: string) =>
  client.post<{ token: string; role: string }>('/auth/login', { username, password })

export const changePassword = (currentPassword: string, newPassword: string) =>
  client.post('/auth/change-password', { currentPassword, newPassword })
