import { useState, useEffect } from 'react';
import { adminApi } from '../../api/admin';

function AgentManagement() {
  const [agents, setAgents] = useState([]);
  const [zones, setZones] = useState([]);
  const [form, setForm] = useState({ name: '', email: '', password: '', phone: '', currentZoneId: '', available: true });
  const [isEditing, setIsEditing] = useState(false);
  const [editId, setEditId] = useState(null);

  const fetchAgents = async () => {
    try {
      const { data } = await adminApi.getAgents();
      setAgents(data);
    } catch (e) {
      alert("Error fetching agents");
    }
  };

  const fetchZones = async () => {
    try {
      const { data } = await adminApi.getZones();
      setZones(data);
    } catch (e) {
      alert("Error fetching zones");
    }
  };

  useEffect(() => {
    fetchAgents();
    fetchZones();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isEditing) {
        await adminApi.updateAgent(editId, form);
      } else {
        await adminApi.createAgent(form);
      }
      setForm({ name: '', email: '', password: '', phone: '', currentZoneId: '', available: true });
      setIsEditing(false);
      setEditId(null);
      fetchAgents();
    } catch (e) {
      alert("Error saving agent: " + (e.response?.data?.message || ""));
    }
  };

  const handleEdit = (a) => {
    setForm({
      name: a.name,
      email: a.email,
      password: '',
      phone: a.phone,
      currentZoneId: a.currentZoneId || '',
      available: a.available
    });
    setEditId(a.id);
    setIsEditing(true);
  };

  const toggleAvailability = async (id, currentVal) => {
    try {
      await adminApi.updateAvailability(id, !currentVal);
      fetchAgents();
    } catch (e) {
      alert("Error updating availability");
    }
  };

  return (
    <div>
      <h2>Delivery Agent Management</h2>
      
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '0.5rem', maxWidth: '400px', marginBottom: '2rem' }}>
        <input placeholder="Name" value={form.name} onChange={e => setForm({...form, name: e.target.value})} required />
        <input placeholder="Email" type="email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} required={!isEditing} disabled={isEditing} />
        <input placeholder="Password" type="password" value={form.password} onChange={e => setForm({...form, password: e.target.value})} required={!isEditing} />
        <input placeholder="Phone" value={form.phone} onChange={e => setForm({...form, phone: e.target.value})} required />
        <select value={form.currentZoneId} onChange={e => setForm({...form, currentZoneId: e.target.value})}>
          <option value="">Select Zone</option>
          {zones.map(z => <option key={z.id} value={z.id}>{z.name}</option>)}
        </select>
        <label>
          <input type="checkbox" checked={form.available} onChange={e => setForm({...form, available: e.target.checked})} />
          Available
        </label>
        <button type="submit" className="btn btn-primary">{isEditing ? 'Update Agent' : 'Create Agent'}</button>
      </form>

      <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
        <thead>
          <tr style={{ borderBottom: '1px solid #ddd' }}>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Zone</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {agents.map(a => (
            <tr key={a.id} style={{ borderBottom: '1px solid #eee' }}>
              <td>{a.id}</td>
              <td>{a.name}</td>
              <td>{a.email}</td>
              <td>{a.phone}</td>
              <td>{a.currentZoneName || 'None'}</td>
              <td>{a.available ? 'Available' : 'Unavailable'}</td>
              <td>
                <button onClick={() => handleEdit(a)}>Edit</button>
                <button onClick={() => toggleAvailability(a.id, a.available)}>
                  Toggle Status
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AgentManagement;
