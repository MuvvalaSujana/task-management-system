import { useEffect, useMemo, useState } from 'react'
import { taskApi } from './api'

const blank = { title: '', description: '', status: 'TODO', priority: 'MEDIUM', dueDate: '' }

export default function App() {
  const [tasks, setTasks] = useState([])
  const [form, setForm] = useState(blank)
  const [editingId, setEditingId] = useState(null)
  const [filter, setFilter] = useState('')
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  async function loadTasks() {
    try { setError(''); setLoading(true); setTasks(await taskApi.getAll(filter, search)) }
    catch (e) { setError(e.message) }
    finally { setLoading(false) }
  }
  useEffect(() => { loadTasks() }, [filter, search])

  const counts = useMemo(() => ({
    total: tasks.length,
    todo: tasks.filter(t => t.status === 'TODO').length,
    progress: tasks.filter(t => t.status === 'IN_PROGRESS').length,
    done: tasks.filter(t => t.status === 'DONE').length
  }), [tasks])

  function change(e) { setForm({ ...form, [e.target.name]: e.target.value }) }
  function reset() { setForm(blank); setEditingId(null) }

  async function submit(e) {
    e.preventDefault()
    if (!form.title.trim()) { setError('Task title is required.'); return }
    try {
      setSaving(true); setError('')
      const payload = { ...form, dueDate: form.dueDate || null }
      if (editingId) await taskApi.update(editingId, payload)
      else await taskApi.create(payload)
      reset(); await loadTasks()
    } catch (err) { setError(err.message) }
    finally { setSaving(false) }
  }

  function edit(task) {
    setEditingId(task.id)
    setForm({ title: task.title || '', description: task.description || '',
      status: task.status || 'TODO', priority: task.priority || 'MEDIUM', dueDate: task.dueDate || '' })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  async function remove(id) {
    if (!window.confirm('Delete this task?')) return
    try { await taskApi.remove(id); await loadTasks() }
    catch (err) { setError(err.message) }
  }

  return <main className="page">
    <header className="topbar">
      <div><p className="eyebrow">WORKSPACE</p><h1>Task Management</h1>
        <p className="subtitle">Plan clearly. Track progress. Get things done.</p></div>
      <span className="mark">TM</span>
    </header>

    <section className="stats">
      <Stat label="Visible tasks" value={counts.total}/><Stat label="To do" value={counts.todo}/>
      <Stat label="In progress" value={counts.progress}/><Stat label="Completed" value={counts.done}/>
    </section>

    <section className="panel">
      <div className="heading"><div><p className="eyebrow">{editingId ? 'UPDATE DETAILS' : 'GET STARTED'}</p>
        <h2>{editingId ? 'Edit task' : 'Create a task'}</h2></div>
        {editingId && <button className="btn secondary" type="button" onClick={reset}>Cancel edit</button>}</div>
      <form onSubmit={submit}>
        <label>Task title *
          <input name="title" value={form.title} onChange={change} maxLength="120" required placeholder="e.g. Prepare project documentation"/>
        </label>
        <label>Description
          <textarea name="description" value={form.description} onChange={change} rows="3" maxLength="1000" placeholder="Add details or notes..."/>
        </label>
        <div className="form-grid">
          <label>Status<select name="status" value={form.status} onChange={change}>
            <option value="TODO">To do</option><option value="IN_PROGRESS">In progress</option><option value="DONE">Done</option>
          </select></label>
          <label>Priority<select name="priority" value={form.priority} onChange={change}>
            <option value="LOW">Low</option><option value="MEDIUM">Medium</option><option value="HIGH">High</option>
          </select></label>
          <label>Due date<input type="date" name="dueDate" value={form.dueDate || ''} onChange={change}/></label>
        </div>
        <button className="btn primary" disabled={saving}>{saving ? 'Saving...' : editingId ? 'Save changes' : 'Add task'}</button>
      </form>
    </section>

    {error && <div className="alert" role="alert">{error}<button onClick={() => setError('')}>×</button></div>}

    <section className="list-section">
      <div className="heading list-heading"><div><p className="eyebrow">YOUR WORK</p><h2>Tasks</h2></div>
        <div className="filters">
          <input aria-label="Search tasks" value={search} onChange={e => setSearch(e.target.value)} placeholder="Search tasks..."/>
          <select aria-label="Filter status" value={filter} onChange={e => setFilter(e.target.value)}>
            <option value="">All statuses</option><option value="TODO">To do</option>
            <option value="IN_PROGRESS">In progress</option><option value="DONE">Completed</option>
          </select>
        </div>
      </div>
      {loading ? <p className="empty">Loading tasks...</p> : tasks.length === 0 ?
        <div className="empty"><h3>No tasks found</h3><p>Create a task above or change your filters.</p></div> :
        <div className="task-list">{tasks.map(task => <article className="task-card" key={task.id}>
          <div className="task-main"><div className="title-row"><h3>{task.title}</h3>
            <span className={`priority ${task.priority.toLowerCase()}`}>{task.priority}</span></div>
            {task.description && <p className="description">{task.description}</p>}
            <div className="meta"><span className={`status ${task.status.toLowerCase()}`}>{task.status.replace('_', ' ')}</span>
              {task.dueDate && <span>Due {task.dueDate}</span>}</div>
          </div>
          <div className="actions"><button className="btn secondary small" onClick={() => edit(task)}>Edit</button>
            <button className="btn danger small" onClick={() => remove(task.id)}>Delete</button></div>
        </article>)}</div>}
    </section>
    <footer>Task Management System · React + Spring Boot + MySQL</footer>
  </main>
}

function Stat({ label, value }) {
  return <div className="stat"><span>{label}</span><strong>{value}</strong></div>
}
