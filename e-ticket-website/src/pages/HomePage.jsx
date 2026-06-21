import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchTicketsByPhone } from '../services/ticket';

export default function HomePage() {
  const navigate = useNavigate();
  const [bookingCode, setBookingCode] = useState('');
  const [phone, setPhone] = useState('');
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleOpenTicket = (event) => {
    event.preventDefault();
    if (!bookingCode.trim()) {
      setError('Vui lòng nhập mã vé');
      return;
    }
    navigate(`/ticket/${encodeURIComponent(bookingCode.trim())}`);
  };

  const handleSearchByPhone = async (event) => {
    event.preventDefault();
    setError('');
    setTickets([]);

    if (!phone.trim()) {
      setError('Vui lòng nhập số điện thoại của bạn');
      return;
    }

    setLoading(true);
    try {
      const data = await fetchTicketsByPhone(phone.trim());
      if (!data.length) {
        setError('Không tim thấy vé nào ứng với số điện thoại này');
      } else {
        setTickets(data);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="hero">
        <h1>Booking Pro</h1>
        <p>Tra cứu và xem vé điện tử của bạn</p>
      </div>

      <div className="card" style={{ marginBottom: 20 }}>
        <form className="search-form" onSubmit={handleOpenTicket}>
          <div className="field">
            <label htmlFor="bookingCode">Ma ve</label>
            <input
              id="bookingCode"
              value={bookingCode}
              onChange={(e) => setBookingCode(e.target.value)}
              placeholder="Vi du: BK-888"
            />
          </div>
          <div className="actions">
            <button type="submit" className="btn btn-primary">Xem ve</button>
          </div>
        </form>
      </div>

      <div className="card">
        <form className="search-form" onSubmit={handleSearchByPhone}>
          <div className="field">
            <label htmlFor="phone">So dien thoai</label>
            <input
              id="phone"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="Số điện thoại đã đăng ký khi đặt vé"
            />
          </div>
          <div className="actions">
            <button type="submit" className="btn btn-secondary">Tìm vé theo SĐT</button>
          </div>
        </form>

        {loading && <div className="loading-box">Đang tải dữ liệu...</div>}
        {error && <div className="error-box" style={{color: "red"}}>{error}</div>}

        {tickets.length > 0 && (
          <div className="ticket-list">
            {tickets.map((ticket) => (
              <div className="ticket-item" key={ticket.bookingCode}>
                <div>
                  <strong>{ticket.bookingCode}</strong>
                  <div>{ticket.tripName}</div>
                </div>
                <button
                  type="button"
                  className="btn btn-primary"
                  onClick={() => navigate(`/ticket/${encodeURIComponent(ticket.bookingCode)}`)}
                >
                  Xem
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
