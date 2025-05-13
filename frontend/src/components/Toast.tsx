import React, { useEffect } from 'react';
import { createPortal } from 'react-dom';
import styled from 'styled-components';

interface ToastProps {
  message: string;
  type: 'error' | 'success';
  onClose: () => void;
  duration?: number;
}

interface ToastContainerProps {
  type: 'error' | 'success';
}

const ToastContainer = styled.div<ToastContainerProps>`
  position: fixed;
  top: 40px;
  left: 50%;
  transform: translateX(-50%);
  min-width: 320px;
  max-width: 90vw;
  padding: 18px 32px;
  border-radius: 6px;
  background-color: ${({ type }) => (type === 'error' ? '#ff4d4f' : '#52c41a')};
  color: white;
  font-size: 1.1rem;
  font-weight: 500;
  box-shadow: 0 4px 16px rgba(0,0,0,0.18);
  z-index: 9999;
  text-align: center;
  animation: slideIn 0.3s ease-in-out;

  @keyframes slideIn {
    from {
      transform: translate(-50%, -100%);
      opacity: 0;
    }
    to {
      transform: translate(-50%, 0);
      opacity: 1;
    }
  }
`;

const Toast: React.FC<ToastProps> = ({
  message,
  type,
  onClose,
  duration = 3000,
}) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, duration);

    return () => clearTimeout(timer);
  }, [duration, onClose]);

  return createPortal(
    <ToastContainer type={type}>
      {message}
    </ToastContainer>,
    document.body
  );
};

export default Toast; 