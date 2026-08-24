import { useState } from 'react';
import { Link } from 'react-router-dom';
import { orderApi } from '../api/orders';

function CreateOrderPage() {
  const [form, setForm] = useState({
    pickupAddress: '',
    pickupPincode: '',
    dropAddress: '',
    dropPincode: '',
    orderType: 'B2C',
    paymentType: 'PREPAID',
    actualWeight: 1,
    length: 10,
    width: 10,
    height: 10
  });

  const [priceData, setPriceData] = useState(null);
  const [orderRes, setOrderRes] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const calculatePrice = async (e) => {
    e.preventDefault();
    setPriceData(null);
    setOrderRes(null);
    setError('');
    setLoading(true);
    try {
      const { data } = await orderApi.calculatePrice(form);
      setPriceData(data);
    } catch (e) {
      setError(e.response?.data?.message || 'Unable to calculate a price. Check the shipment details.');
    } finally {
      setLoading(false);
    }
  };

  const confirmOrder = async () => {
    setError('');
    setLoading(true);
    try {
      const { data } = await orderApi.createOrder(form);
      setOrderRes(data);
    } catch (e) {
      setError(e.response?.data?.message || 'Unable to create the order.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <div><p className="eyebrow">Shipment desk</p><h1>Create Order</h1></div>
        <Link to="/dashboard" className="btn btn-secondary" style={{ width: 'auto' }}>Back</Link>
      </div>
      {error && <p className="error-message">{error}</p>}
      {orderRes ? (
        <div className="success-message order-success">
          <h3>Order Created Successfully!</h3>
          <p>Order ID: {orderRes.id}</p>
          <p>Final Charge: {orderRes.deliveryCharge}</p>
          <p>Status: {orderRes.status}</p>
          <Link to="/orders/my" className="btn btn-primary" style={{ width: 'auto', marginTop: '1rem' }}>View My Orders</Link>
        </div>
      ) : (
        <>
          <form onSubmit={calculatePrice} className="form-panel">
            <div className="form-grid">
            <label>Pickup Address<input value={form.pickupAddress} onChange={e => setForm({...form, pickupAddress: e.target.value})} required /></label>
            <label>Pickup Pincode<input value={form.pickupPincode} onChange={e => setForm({...form, pickupPincode: e.target.value.replace(/\D/g, '')})} required maxLength="6" pattern="[0-9]{6}" /></label>
            <label>Drop Address<input value={form.dropAddress} onChange={e => setForm({...form, dropAddress: e.target.value})} required /></label>
            <label>Drop Pincode<input value={form.dropPincode} onChange={e => setForm({...form, dropPincode: e.target.value.replace(/\D/g, '')})} required maxLength="6" pattern="[0-9]{6}" /></label>
            <label>Order Type<select value={form.orderType} onChange={e => setForm({...form, orderType: e.target.value})}>
                <option value="B2B">B2B</option>
                <option value="B2C">B2C</option>
              </select></label>
            <label>Payment Type<select value={form.paymentType} onChange={e => setForm({...form, paymentType: e.target.value})}>
                <option value="PREPAID">PREPAID</option>
                <option value="COD">COD</option>
              </select></label>
            <label>Actual Weight (kg)<input type="number" min="0.01" step="0.01" value={form.actualWeight} onChange={e => setForm({...form, actualWeight: e.target.value})} required /></label>
            <label>Length (cm)<input type="number" min="0.01" step="0.01" value={form.length} onChange={e => setForm({...form, length: e.target.value})} required /></label>
            <label>Breadth (cm)<input type="number" min="0.01" step="0.01" value={form.width} onChange={e => setForm({...form, width: e.target.value})} required /></label>
            <label>Height (cm)<input type="number" min="0.01" step="0.01" value={form.height} onChange={e => setForm({...form, height: e.target.value})} required /></label>
            </div>
            <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Calculating...' : 'Calculate Price'}</button>
          </form>

          {priceData && (
            <div className="quote-panel">
              <div className="section-heading"><div><p className="eyebrow">Backend quotation</p><h2>Price breakdown</h2></div><span className="status-badge status-pending">{priceData.rateType}</span></div>
              <div className="quote-grid">
                <span>Route</span><strong>{priceData.pickupZoneName} to {priceData.dropZoneName}</strong>
                <span>Chargeable weight</span><strong>{priceData.billingWeight} kg</strong>
                <span>Base rate</span><strong>{priceData.baseRate}</strong>
                <span>Weight charge</span><strong>{priceData.deliveryCharge}</strong>
                <span>COD surcharge</span><strong>{priceData.codSurcharge}</strong>
                <span className="quote-total">Final delivery charge</span><strong className="quote-total">{priceData.finalCharge}</strong>
              </div>
              <button type="button" onClick={confirmOrder} className="btn btn-primary" disabled={loading}>{loading ? 'Creating...' : 'Confirm Order'}</button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default CreateOrderPage;
