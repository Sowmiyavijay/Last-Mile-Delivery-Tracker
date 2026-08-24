import { useState, useEffect } from 'react';
import { adminApi } from '../../api/admin';

function ZoneManagement() {
  const [zones, setZones] = useState([]);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');

  const fetchZones = async () => {
    try {
      const { data } = await adminApi.getZones();
      setZones(data);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchZones();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await adminApi.createZone({ name, description });
      setName('');
      setDescription('');
      fetchZones();
    } catch (e) {
      alert("Error: " + e.response?.data?.message || "Creation failed");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this zone?')) return;
    try {
      await adminApi.deleteZone(id);
      fetchZones();
    } catch (e) {
      alert("Error deleting zone: " + (e.response?.data?.message || "unknown"));
    }
  };

  const handleEdit = async (zone) => {
    const nextName = window.prompt('Zone name', zone.name);
    if (nextName === null) return;
    const nextDescription = window.prompt('Description', zone.description || '');
    if (nextDescription === null) return;
    try {
      await adminApi.updateZone(zone.id, { name: nextName, description: nextDescription });
      fetchZones();
    } catch (e) {
      alert(e.response?.data?.message || 'Unable to update zone.');
    }
  };

  return (
    <div>
      <h2>Zone Management</h2>
      <form onSubmit={handleCreate}>
        <input placeholder="Zone Name" value={name} onChange={e => setName(e.target.value)} required />
        <input placeholder="Description" value={description} onChange={e => setDescription(e.target.value)} />
        <button type="submit">Create Zone</button>
      </form>
      
      <ul>
        {zones.map(z => (
          <li key={z.id}>
            {z.name} - {z.description} <button onClick={() => handleEdit(z)}>Edit</button> <button onClick={() => handleDelete(z.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default ZoneManagement;
