export async function fetchTicketByCode(code) {
  const response = await fetch(`/api/tickets/${encodeURIComponent(code)}`);
  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.error || 'Khong tim thay ve');
  }
  return response.json();
}

export async function fetchTicketsByPhone(phone) {
  const response = await fetch(`/api/tickets/by-phone/${encodeURIComponent(phone)}`);
  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.error || 'Khong tim thay ve');
  }
  return response.json();
}

export function getTicketQrUrl(code) {
  return `/api/tickets/${encodeURIComponent(code)}/qr`;
}

export function formatMoney(value) {
  return new Intl.NumberFormat('vi-VN').format(value) + ' VND';
}

export function formatDateTime(value) {
  if (!value) return 'Chua cap nhat';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString('vi-VN');
}

export function statusLabel(status) {
  switch (status) {
    case 'CONFIRMED':
      return 'Da xac nhan';
    case 'PENDING':
      return 'Cho xu ly';
    case 'CANCELLED':
      return 'Da huy';
    default:
      return status;
  }
}
