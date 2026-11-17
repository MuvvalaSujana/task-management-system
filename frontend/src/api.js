const API = 'http://localhost:8080/api/tasks'

async function request(url, options = {}) {
  const response = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  })
  if (response.status === 204) return null
  const data = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(data.error || `Request failed (${response.status})`)
  return data
}

export const taskApi = {
  getAll(status = '', search = '') {
    const params = new URLSearchParams()
    if (status) params.set('status', status)
    if (search.trim()) params.set('search', search.trim())
    const query = params.toString()
    return request(`${API}${query ? `?${query}` : ''}`)
  },
  create: task => request(API, { method: 'POST', body: JSON.stringify(task) }),
  update: (id, task) => request(`${API}/${id}`, { method: 'PUT', body: JSON.stringify(task) }),
  remove: id => request(`${API}/${id}`, { method: 'DELETE' })
}
