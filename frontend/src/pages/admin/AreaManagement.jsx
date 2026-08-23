import { useState, useEffect } from 'react';
import { adminApi } from '../../api/admin';

function AreaManagement() {
  const [areas, setAreas] = useState([]);
  const [zones, setZones] = useState([]);
  const [name, setName] = useState('');
  const [pincode, setPincode] = useState('');
  const [zoneId, setZoneId] = useState('');

  const fetchData = async () => {
    try {
      const [areasRes, zonesRes] = await Promise.all([adminApi.getAreas(), adminApi.getZones()]);
      setAreas(areasRes.data);
      setZones(zonesRes.data);
      if(zonesRes.data.length > 0) setZoneId(zonesRes.data[0].id);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    if(pincode.length !== 6) {
        alert("Pincode must be exactly 6 digits");
        return;
    }
    try {
      await adminApi.createArea({ name, pincode, zoneId });
      setName('');
      setPincode('');
      fetchData();
    } catch (e) {
      alert("Error: " + e.response?.data?.message || "Creation failed");
    }
  };

  const handleDelete = async (id) => {
    try {
      await adminApi.deleteArea(id);
      fetchData();
    } catch (e) {
      alert("Error deleting area: " + (e.response?.data?.message || "unknown"));
    }
  };

  return (
    <div>
      <h2>Area Management</h2>
      <form onSubmit={handleCreate}>
        <input placeholder="Area Name" value={name} onChange={e => setName(e.target.value)} required />
        <input placeholder="Pincode (6 digits)" value={pincode} onChange={e => setPincode(e.target.value)} required />
        <select value={zoneId} onChange={e => setZoneId(e.target.value)} required>
            {zones.map(z => <option key={z.id} value={z.id}>{z.name}</option>)}
        </select>
        <button type="submit">Create Area</button>
      </form>
      
      <ul>
        {areas.map(a => (
          <li key={a.id}>
            {a.name} ({a.pincode}) - Zone: {a.zoneName} <button onClick={() => handleDelete(a.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default AreaManagement;
