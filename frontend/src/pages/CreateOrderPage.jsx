import { useState } from 'react';
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

  const calculatePrice = async (e) => {
    e.preventDefault();
    setPriceData(null);
    setOrderRes(null);
    try {
      const { data } = await orderApi.calculatePrice(form);
      setPriceData(data);
    } catch (e) {
      alert("Error calculating price: " + (e.response?.data?.message || "Unknown error"));
    }
  };

  const confirmOrder = async () => {
    try {
      const { data } = await orderApi.createOrder(form);
      setOrderRes(data);
    } catch (e) {
      alert("Error creating order: " + (e.response?.data?.message || "Unknown error"));
    }
  };

  return (
    <div>
      <h2>Create Order</h2>
      {orderRes ? (
        <div style={{ padding: '1rem', border: '1px solid green', marginBottom: '1rem' }}>
          <h3>Order Created Successfully!</h3>
          <p>Order ID: {orderRes.id}</p>
          <p>Final Charge: {orderRes.deliveryCharge}</p>
          <p>Status: {orderRes.status}</p>
        </div>
      ) : (
        <>
          <form onSubmit={calculatePrice} style={{ display: 'grid', gap: '0.5rem', maxWidth: '400px' }}>
            <label>Pickup Address: <input value={form.pickupAddress} onChange={e => setForm({...form, pickupAddress: e.target.value})} required /></label>
            <label>Pickup Pincode: <input value={form.pickupPincode} onChange={e => setForm({...form, pickupPincode: e.target.value})} required maxLength="6" /></label>
            <label>Drop Address: <input value={form.dropAddress} onChange={e => setForm({...form, dropAddress: e.target.value})} required /></label>
            <label>Drop Pincode: <input value={form.dropPincode} onChange={e => setForm({...form, dropPincode: e.target.value})} required maxLength="6" /></label>
            
            <label>Order Type: 
              <select value={form.orderType} onChange={e => setForm({...form, orderType: e.target.value})}>
                <option value="B2B">B2B</option>
                <option value="B2C">B2C</option>
              </select>
            </label>
            
            <label>Payment Type: 
              <select value={form.paymentType} onChange={e => setForm({...form, paymentType: e.target.value})}>
                <option value="PREPAID">PREPAID</option>
                <option value="COD">COD</option>
              </select>
            </label>
            
            <label>Actual Weight (kg): <input type="number" step="0.01" value={form.actualWeight} onChange={e => setForm({...form, actualWeight: e.target.value})} required /></label>
            <label>Length (cm): <input type="number" step="0.01" value={form.length} onChange={e => setForm({...form, length: e.target.value})} required /></label>
            <label>Width (cm): <input type="number" step="0.01" value={form.width} onChange={e => setForm({...form, width: e.target.value})} required /></label>
            <label>Height (cm): <input type="number" step="0.01" value={form.height} onChange={e => setForm({...form, height: e.target.value})} required /></label>
            
            <button type="submit" className="btn btn-primary">Calculate Price</button>
          </form>

          {priceData && (
            <div style={{ marginTop: '2rem', padding: '1rem', border: '1px solid #ccc' }}>
              <h3>Price Quotation</h3>
              <p>Setup: {priceData.pickupZoneName} ➔ {priceData.dropZoneName} ({priceData.rateType})</p>
              <p>Actual Weight: {priceData.actualWeight} | Volumetric: {priceData.volumetricWeight} | Billing: {priceData.billingWeight}</p>
              <p>Base Rate: {priceData.baseRate} | Rate/Kg: {priceData.ratePerKg}</p>
              <p>Delivery: {priceData.deliveryCharge} + COD: {priceData.codSurcharge}</p>
              <h4>Final Charge: {priceData.finalCharge}</h4>
              <button type="button" onClick={confirmOrder} className="btn btn-primary" style={{ marginTop: '1rem' }}>Confirm Order</button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default CreateOrderPage;
