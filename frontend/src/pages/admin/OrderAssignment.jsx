import { useState, useEffect } from 'react';
import { adminApi } from '../../api/admin';

function OrderAssignment() {
  const [orderId, setOrderId] = useState('');
  const [order, setOrder] = useState(null);
  const [agents, setAgents] = useState([]);
  const [selectedAgent, setSelectedAgent] = useState('');

  const fetchOrder = async (e) => {
    e.preventDefault();
    setOrder(null);
    try {
      const { data } = await adminApi.getOrder(orderId);
      setOrder(data);
    } catch (e) {
      alert("Order not found or error loading");
    }
  };

  const fetchAgents = async () => {
    try {
      const { data } = await adminApi.getAgents();
      // Only show available ones
      setAgents(data.filter(a => a.available));
    } catch (e) {
      alert("Error fetching agents");
    }
  };

  useEffect(() => {
    fetchAgents();
  }, []);

  const handleManualAssign = async () => {
    if (!selectedAgent) return alert("Select an agent");
    try {
      await adminApi.manualAssign(order.id, selectedAgent);
      alert("Order manually assigned");
      setOrder(null);
      setOrderId('');
    } catch (e) {
      alert(e.response?.data?.message || "Error assigning order");
    }
  };

  const handleAutoAssign = async () => {
    try {
      await adminApi.autoAssign(order.id);
      alert("Order auto-assigned successfully");
      setOrder(null);
      setOrderId('');
    } catch (e) {
      alert(e.response?.data?.message || "No agent available for auto-assign");
    }
  };

  return (
    <div>
      <h2>Order Assignment</h2>
      
      <form onSubmit={fetchOrder} style={{ display: 'flex', gap: '0.5rem', marginBottom: '2rem' }}>
        <input placeholder="Enter Order ID" value={orderId} onChange={e => setOrderId(e.target.value)} required />
        <button type="submit" className="btn btn-primary">Lookup</button>
      </form>

      {order && (
        <div style={{ border: '1px solid #ccc', padding: '1rem', maxWidth: '500px' }}>
          <h3>Order Details: #{order.id}</h3>
          <p>Pickup Zone: {order.pickupPincode} | Drop Zone: {order.dropPincode}</p>
          <p>Status: {order.status}</p>

          <div style={{ marginTop: '1rem', display: 'flex', gap: '1rem' }}>
            <select value={selectedAgent} onChange={e => setSelectedAgent(e.target.value)}>
              <option value="">Select Available Agent</option>
              {agents.map(a => <option key={a.id} value={a.id}>{a.name} ({a.currentZoneName})</option>)}
            </select>
            <button onClick={handleManualAssign} className="btn btn-primary">Manual Assign</button>
            <button onClick={handleAutoAssign} className="btn btn-primary">Auto Assign (Algorithm)</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default OrderAssignment;
