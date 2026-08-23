import { useState, useEffect } from 'react';
import { orderApi } from '../api/orders';

function MyOrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchOrders = async () => {
    try {
      const { data } = await orderApi.getMyOrders();
      setOrders(data);
    } catch (e) {
      console.error(e);
      alert("Error fetching orders");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  if (loading) return <div>Loading orders...</div>;

  return (
    <div>
      <h2>My Orders</h2>
      {orders.length === 0 ? (
        <p>You have no orders yet.</p>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid #ddd' }}>
              <th>ID</th>
              <th>Pickup</th>
              <th>Drop</th>
              <th>Type</th>
              <th>Payment</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {orders.map(o => (
              <tr key={o.id} style={{ borderBottom: '1px solid #eee' }}>
                <td>{o.id}</td>
                <td>{o.pickupPincode}</td>
                <td>{o.dropPincode}</td>
                <td>{o.orderType}</td>
                <td>{o.paymentType}</td>
                <td>{o.deliveryCharge}</td>
                <td>{o.status}</td>
                <td>{new Date(o.createdAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default MyOrdersPage;
