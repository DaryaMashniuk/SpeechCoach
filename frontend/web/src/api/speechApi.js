import axiosInstance from './axiosInstance';

const getUserIdFromToken = () => {
  const token = localStorage.getItem('auth_token');
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    return payload.id;
  } catch { return null; }
};

export const startTraining = async (formData) => {
  const userId = getUserIdFromToken();
  if (!userId) throw new Error('User not authenticated');
  formData.set('userId', userId);
  const response = await axiosInstance.post('/api/v1/orchestrator/training', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  
  console.log('Training response - UUID:', response.data);
  return response.data;
};

export const startMeeting = async (formData) => {
  const userId = getUserIdFromToken();
  if (!userId) throw new Error('User not authenticated');
  formData.set('userId', userId);

  const translated = formData.get('translated') === 'true';
  const translatingLanguage = formData.get('translatingLanguage');
  
  if (translated && translatingLanguage) {
    formData.set('translated', 'true');
    formData.set('translatingLanguage', translatingLanguage);
  } else {
    formData.set('translated', 'false');
  }
  
  const response = await axiosInstance.post('/api/v1/orchestrator/meeting', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data;
};

export const getJobStatus = async (jobId) => {
  const response = await axiosInstance.get(`/api/v1/orchestrator/jobs/${jobId}/status`);
  return response.data;
};

export const getReport = async (jobId) => {
  const response = await axiosInstance.get(`/api/v1/orchestrator/results/${jobId}`);
  return response.data;
};

export const getMeetingReport = async (jobId) => {
  const response = await axiosInstance.get(`/api/v1/orchestrator/meetings/${jobId}`);
  return response.data;
};

export const getUserPresentations = async (userId, page = 0, size = 50, training = null) => {
  const params = { page, size };
  if (training !== null) params.training = training;
  
  console.log('Fetching presentations for userId:', userId, 'training:', training);
  
  const response = await axiosInstance.get(`/api/v1/orchestrator/presentations/user/${userId}`, { params });
  
  console.log('Presentations response:', response.data);
  console.log('First item:', response.data?.content?.[0]);

  if (response.data?.content?.length > 0) {
    const first = response.data.content[0];
    console.log('First presentation fields:', Object.keys(first));
    console.log('Has jobId?', !!first.jobId);
    console.log('Has analysisJobId?', !!first.analysisJobId);
  }
  
  return response.data;
};
export const getUserProgress = async (userId) => {
  const response = await axiosInstance.get('/api/v1/orchestrator/analytics/progress', { params: { userId } });
  return response.data;
};

export const deletePresentation = async (id) => {
  const response = await axiosInstance.delete(`/api/v1/orchestrator/${id}`);
  return response.data;
};

export const getAudioUrl = (audioUrl) => {
  if (!audioUrl) return null;
  if (audioUrl.startsWith('http')) return audioUrl;
  return `http://localhost:8083${audioUrl}`;
};

