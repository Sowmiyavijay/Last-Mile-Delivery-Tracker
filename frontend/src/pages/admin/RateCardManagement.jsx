import { useState, useEffect } from 'react';
import { adminApi } from '../../api/admin';

function RateCardManagement() {
  const [rateCards, setRateCards] = useState([]);
  const [zones, setZones] = useState([]);
  const [form, setForm] = useState({
    rateType: 'INTRA_ZONE',
    orderType: 'B2B',
    pickupZoneId: '',
    dropZoneId: '',
    baseRate: 0,
    ratePerKg: 0
  });

  const fetchData = async () => {
    try {
      const [rcRes, zonesRes] = await Promise.all([adminApi.getRateCards(), adminApi.getZones()]);
      setRateCards(rcRes.data);
      setZones(zonesRes.data);
      if(zonesRes.data.length > 0) {
        setForm(prev => ({ ...prev, pickupZoneId: zonesRes.data[0].id, dropZoneId: zonesRes.data[0].id }));
      }
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await adminApi.createRateCard(form);
      fetchData();
    } catch (e) {
      alert("Error: " + e.response?.data?.message || "Creation failed");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this rate card?')) return;
    try {
      await adminApi.deleteRateCard(id);
      fetchData();
    } catch (e) {
      alert("Error deleting rate card: " + (e.response?.data?.message || "unknown"));
    }
  };

  const handleEdit = async (rateCard) => {
    const baseRate = window.prompt('Base rate', rateCard.baseRate);
    if (baseRate === null) return;
    const ratePerKg = window.prompt('Rate per kg', rateCard.ratePerKg);
    if (ratePerKg === null) return;
    try {
      await adminApi.updateRateCard(rateCard.id, { ...rateCard, baseRate, ratePerKg, pickupZoneId: rateCard.pickupZoneId, dropZoneId: rateCard.dropZoneId });
      fetchData();
    } catch (e) {
      alert(e.response?.data?.message || 'Unable to update rate card.');
    }
  };

  return (
    <div>
      <h2>Rate Card Management</h2>
      <form onSubmit={handleCreate}>
        <select value={form.rateType} onChange={e => setForm({...form, rateType: e.target.value})} required>
            <option value="INTRA_ZONE">INTRA_ZONE</option>
            <option value="INTER_ZONE">INTER_ZONE</option>
        </select>
        <select value={form.orderType} onChange={e => setForm({...form, orderType: e.target.value})} required>
            <option value="B2B">B2B</option>
            <option value="B2C">B2C</option>
        </select>
        <select value={form.pickupZoneId} onChange={e => setForm({...form, pickupZoneId: e.target.value})} required>
            {zones.map(z => <option key={z.id} value={z.id}>Pickup: {z.name}</option>)}
        </select>
        <select value={form.dropZoneId} onChange={e => setForm({...form, dropZoneId: e.target.value})} required>
            {zones.map(z => <option key={z.id} value={z.id}>Drop: {z.name}</option>)}
        </select>
        <input type="number" step="0.01" placeholder="Base Rate" value={form.baseRate} onChange={e => setForm({...form, baseRate: e.target.value})} required />
        <input type="number" step="0.01" placeholder="Rate / Kg" value={form.ratePerKg} onChange={e => setForm({...form, ratePerKg: e.target.value})} required />
        <button type="submit">Create Rate Card</button>
      </form>
      
      <ul>
        {rateCards.map(rc => (
          <li key={rc.id}>
            {rc.rateType} - {rc.orderType} | {rc.pickupZoneName} to {rc.dropZoneName} | Base: {rc.baseRate}, Per Kg: {rc.ratePerKg} 
            <button onClick={() => handleEdit(rc)}>Edit</button> <button onClick={() => handleDelete(rc.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default RateCardManagement;
