export const getToken = () => localStorage.getItem('token')
export const getRole = () => localStorage.getItem('role')
export const isLoggedIn = () => !!getToken()

export const saveAuth = (token: string, role: string) => {
  localStorage.setItem('token', token)
  localStorage.setItem('role', role)
}

export const clearAuth = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
}
