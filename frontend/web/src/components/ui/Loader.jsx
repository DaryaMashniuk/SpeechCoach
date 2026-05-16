import React from 'react';

export const Loader = ({ size = 'md', text = '' }) => {
  const sizes = { sm: 24, md: 48, lg: 64 };
  const s = sizes[size] || 48;
  return (
    <div className="sc-loader-wrap">
      <div className="sc-spinner" style={{ width: s, height: s }} />
      {text && <p className="sc-loader-text">{text}</p>}
    </div>
  );
};

export const StatusBadge = ({ status, t }) => {
  const colors = {
    PENDING: '#6b7280', IN_PROGRESS: '#3b82f6', WAITING_AUDIO_SERVICE: '#8b5cf6',
    WAITING_INTELLIGENCE_SERVICE: '#f59e0b', DONE: '#10b981', FAILED: '#ef4444',
  };
  const label = t?.status?.[status] || status;
  return (
    <span className="status-badge" style={{ '--badge-color': colors[status] || '#6b7280' }}>
      {status === 'DONE' ? '✓' : status === 'FAILED' ? '✕' : '⟳'} {label}
    </span>
  );
};

export default Loader;