import { useState, useEffect, useCallback } from 'react';
import { getJobStatus, getMeetingReport } from '../api/speechApi';

export const useMeetingPolling = (jobId, interval = 3000) => {
  const [status, setStatus] = useState('PENDING');
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [statusLabel, setStatusLabel] = useState('PENDING');

  const fetchStatus = useCallback(async () => {
    if (!jobId) return false;
    
    try {
      const statusData = await getJobStatus(jobId);
      console.log('Meeting job status:', statusData);
      setStatus(statusData.status);
      setStatusLabel(statusData.status);
      
      if (statusData.isDone || statusData.status === 'DONE') {

        const report = await getMeetingReport(jobId);
        console.log('Meeting report loaded:', report);
        setResult(report);
        return true;
      }
      
      if (statusData.status === 'FAILED') {
        setError(statusData.errorMessage || 'Analysis failed');
        return true;
      }
      return false;
    } catch (err) {
      console.error('Polling error:', err);
      setError(err.message);
      return true;
    }
  }, [jobId]);

  useEffect(() => {
    if (!jobId) return;
    
    let timeoutId;
    let isMounted = true;
    
    const poll = async () => {
      if (!isMounted) return;
      const stop = await fetchStatus();
      if (!stop && isMounted) {
        timeoutId = setTimeout(poll, interval);
      }
    };
    
    poll();
    
    return () => {
      isMounted = false;
      if (timeoutId) clearTimeout(timeoutId);
    };
  }, [jobId, interval, fetchStatus]);

  const isLoading = !['DONE', 'FAILED'].includes(status);
  
  return { status, statusLabel, result, error, isLoading };
};