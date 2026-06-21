import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  fetchTicketByCode,
  formatDateTime,
  formatMoney,
  getTicketQrUrl,
  statusLabel,
} from '../services/ticket';

function statusClass(status) {
  if (status === 'CONFIRMED') return 'status status-confirmed';
  if (status === 'PENDING') return 'status status-pending';
  if (status === 'CANCELLED') return 'status status-cancelled';
  return 'status';
}

export default function TicketPage() {
  const { code } = useParams();
  const [ticket, setTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;

    async function loadTicket() {
      setLoading(true);
      setError('');
      try {
        const data = await fetchTicketByCode(code);
        if (active) {
          setTicket(data);
        }
      } catch (err) {
        if (active) {
          setError(err.message);
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadTicket();
    return () => {
      active = false;
    };
  }, [code]);

  if (loading) {
    return <div className="page loading-box">Đang tải thông tin vé...</div>;
  }

  if (error || !ticket) {
    return (
      <div className="page">
        <div className="card error-box">
          <p>{error || 'Không tìm thấy vé'}</p>
          <Link to="/">Quay lại trang tra cứu</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="card">
        <div className="ticket-header">
          <h2>{ticket.companyName || 'Booking Pro'}</h2>
          <p>Vé điện tử - {ticket.bookingCode}</p>
        </div>

        <div className="ticket-grid">
          <div>
            <div className="info-row">
              <span>Khách hàng</span>
              <strong>{ticket.customerName}</strong>
            </div>
            <div className="info-row">
              <span>Số điện thoại</span>
              <strong>{ticket.phoneNumber || 'Chưa cập nhật'}</strong>
            </div>
            <div className="info-row">
              <span>Chuyến đi</span>
              <strong>{ticket.tripName}</strong>
            </div>
            <div className="info-row">
              <span>Tuyến</span>
              <strong>{ticket.startLocation} → {ticket.endLocation}</strong>
            </div>
            <div className="info-row">
              <span>Giờ khởi hành</span>
              <strong>{formatDateTime(ticket.departureTime)}</strong>
            </div>
            <div className="info-row">
              <span>Số lượng ghế</span>
              <strong>{ticket.totalSeats || 'Chưa chọn'}</strong>
            </div>
            <div className="info-row">
              <span>Ngày đặt</span>
              <strong>{formatDateTime(ticket.bookingDate)}</strong>
            </div>
            <div className="info-row">
              <span>Thanh toán</span>
              <strong>{ticket.paymentMethod}</strong>
            </div>
            <div className="info-row">
              <span>Tổng tiền</span>
              <strong>{formatMoney(ticket.totalAmount)}</strong>
            </div>
            <div className="info-row">
              <span>Trạng thái</span>
              <strong><span className={statusClass(ticket.status)}>{statusLabel(ticket.status)}</span></strong>
            </div>
            <div className="info-row">
              <span>Hotline</span>
              <strong>{ticket.companyHotline}</strong>
            </div>
            <p className="footer-note">{ticket.ticketFooter}</p>
          </div>

          <div className="qr-box">
            <img src={getTicketQrUrl(ticket.bookingCode)} alt={`QR ${ticket.bookingCode}`} />
            <p>Quét mã QR để mở lại vé hoặc xuất trình khi lên xe</p>
            <Link to="/">Tra cứu vé khác</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
