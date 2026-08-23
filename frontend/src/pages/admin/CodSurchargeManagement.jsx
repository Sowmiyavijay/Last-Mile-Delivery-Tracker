import { useState, useEffect } from 'react';
import { adminApi } from '../../api/admin';

function CodSurchargeManagement() {
  const [surcharges, setSurcharges] = useState([]);
  const [orderType, setOrderType] = useState('B2B');
  const [amount, setAmount] = useState(0);

  const fetchSurcharges = async () => {
    try {
      const { data } = await adminApi.getCodSurcharges();
      setSurcharges(data);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchSurcharges();
  }, []);

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      await adminApi.updateCodSurcharge(orderType, { surchargeAmount: amount });
      fetchSurcharges();
    } catch (e) {
      alert("Error: " + e.response?.data?.message || "Update failed");
    }
  };

  return (
    <div>
      <h2>COD Surcharge Management</h2>
      <form onSubmit={handleUpdate}>
        <select value={orderType} onChange={e => setOrderType(e.target.value)} required>
            <option value="B2B">B2B</option>
            <option value="B2C">B2C</option>
        </select>
        <input type="number" step="0.01" placeholder="Surcharge Amount" value={amount} onChange={e => setAmount(e.target.value)} required />
        <button type="submit">Set Surcharge</button>
      </form>
      
      <ul>
        {surcharges.map(s => (
          <li key={s.id}>
            {s.orderType}: {s.surchargeAmount}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default CodSurchargeManagement;
