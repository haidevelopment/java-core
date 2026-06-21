import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import TicketPage from './pages/TicketPage';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/ticket/:code" element={<TicketPage />} />
    </Routes>
  );
}
