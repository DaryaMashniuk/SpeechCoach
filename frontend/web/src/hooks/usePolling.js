import { useState, useEffect, useCallback } from 'react';
import { getJobStatus, getReport } from '../api/speechApi';

export const usePolling = (jobId, interval = 3000) => {
  const [status, setStatus] = useState('PENDING');
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [statusLabel, setStatusLabel] = useState('PENDING');

  const fetchStatus = useCallback(async () => {
    if (!jobId) return false;
    
    try {
      const statusData = await getJobStatus(jobId);
      setStatus(statusData.status);
      setStatusLabel(statusData.status);
      
      if (statusData.isDone || statusData.status === 'DONE') {
        const report = await getReport(jobId);
        setResult(report);
        return true;
      }
      
      if (statusData.status === 'FAILED') {
        setError(statusData.errorMessage || 'Analysis failed');
        return true;
      }
      return false;
    } catch (err) {
      setError(err.message);
      return true;
    }
  }, [jobId]);

  useEffect(() => {
    if (!jobId) return;
    let timeoutId;
    const poll = async () => {
      const stop = await fetchStatus();
      if (!stop) timeoutId = setTimeout(poll, interval);
    };
    poll();
    return () => { if (timeoutId) clearTimeout(timeoutId); };
  }, [jobId, interval, fetchStatus]);

  const isLoading = !['DONE', 'FAILED'].includes(status);
  return { status, statusLabel, result, error, isLoading: !['DONE', 'FAILED'].includes(status) };
};