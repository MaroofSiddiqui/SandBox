import React from 'react';

/**
 * WarningModal Component
 *
 * Renders an un-dismissible, high-priority modal overlay that alerts candidates
 * of proctoring violations (e.g., tab switching, lost focus, fullscreen exits).
 * Uses a clean enterprise light blue and white theme.
 *
 * @component
 * @param {Object} props - Component props.
 * @param {boolean} props.isOpen - Controls the visibility of the warning overlay.
 * @param {string} props.warningText - The violation alert message displayed to the candidate.
 * @param {Function} props.onClose - Callback function triggered upon acknowledgment; clears modal and re-enforces fullscreen mode.
 * @returns {JSX.Element|null} The modal overlay component or null if not open.
 */
export const WarningModal = ({ isOpen, warningText, onClose }) => {
  // Render nothing if the modal is not active
  if (!isOpen) return null;

  return (
    <div 
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        width: '100vw',
        height: '100vh',
        backgroundColor: 'rgba(234, 242, 250, 0.88)', // Soft translucent light-blue backdrop
        backdropFilter: 'blur(4px)',
        zIndex: 99999, // Guarantees modal renders above all UI elements
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        color: '#1e293b',
        textAlign: 'center',
        fontFamily: 'system-ui, -apple-system, sans-serif'
      }}
      role="dialog"
      aria-modal="true"
      aria-labelledby="violation-modal-title"
    >
      <div style={{
        backgroundColor: '#ffffff', // Crisp white modal container
        padding: '2.5rem',
        borderRadius: '12px',
        maxWidth: '520px',
        width: '90%',
        border: '1px solid #cbd5e1',
        borderTop: '5px solid #2563eb', // Enterprise light-blue header bar
        boxShadow: '0 20px 25px -5px rgba(37, 99, 235, 0.15), 0 8px 10px -6px rgba(0, 0, 0, 0.05)'
      }}>
        {/* Warning Icon Badge */}
        <div style={{
          backgroundColor: '#eff6ff',
          width: '56px',
          height: '56px',
          borderRadius: '50%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          margin: '0 auto 1.25rem auto',
          fontSize: '1.75rem',
          color: '#2563eb'
        }}>
          ⚠️
        </div>

        {/* Header */}
        <h2 id="violation-modal-title" style={{ color: '#1e3a8a', marginBottom: '0.75rem', fontSize: '1.5rem', fontWeight: '700' }}>
          Proctoring Notice
        </h2>
        
        {/* Violation Description */}
        <p style={{ fontSize: '1.05rem', marginBottom: '1.25rem', color: '#334155', lineHeight: '1.5' }}>
          {warningText}
        </p>

        {/* Audit Disclaimer */}
        <p style={{ fontSize: '0.875rem', color: '#64748b', marginBottom: '1.75rem', lineHeight: '1.4' }}>
          This event has been logged for your assessment integrity report. Clicking below will re-engage Fullscreen mode.
        </p>

        {/* Acknowledgment Action Button */}
        <button
          onClick={onClose}
          style={{
            backgroundColor: '#2563eb',
            color: '#ffffff',
            padding: '0.875rem 1.75rem',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer',
            fontWeight: '600',
            fontSize: '1rem',
            width: '100%',
            boxShadow: '0 4px 6px -1px rgba(37, 99, 235, 0.2)',
            transition: 'background-color 0.2s ease'
          }}
        >
          Acknowledge & Resume Assessment
        </button>
      </div>
    </div>
  );
};